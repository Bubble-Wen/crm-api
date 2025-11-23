package com.crm.service.impl;
import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.common.exception.ServerException;
import com.crm.common.result.PageResult;
import com.crm.convert.ContractConvert;
import com.crm.entity.*;
import com.crm.enums.ContractStatusEnum;
import com.crm.mapper.ContractMapper;
import com.crm.mapper.ApprovalMapper;
import com.crm.mapper.ContractProductMapper;
import com.crm.mapper.ManagerMapper;
import com.crm.query.ApprovalQuery;
import com.crm.query.ContractQuery;
import com.crm.query.IdQuery;
import com.crm.security.user.SecurityUser;
import com.crm.service.ContractService;
import com.crm.service.EmailService;
import com.crm.vo.ContractTrendPieVO;
import com.crm.vo.ContractVO;
import com.crm.vo.ProductVO;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@AllArgsConstructor
public class ContractServiceImpl extends ServiceImpl<ContractMapper, Contract> implements ContractService {

    private final ContractMapper contractMapper;
    private final ContractProductMapper contractProductMapper;
    private final ApprovalMapper approvalMapper;
    private final ManagerMapper managerMapper;
    private final EmailService emailService;

    // 原有方法：状态用枚举筛选（已确保无 creator_id，统一为 creater_id）
    @Override
    public PageResult<ContractVO> getPage(ContractQuery query) {
        Page<ContractVO> page = new Page<>(query.getPage(), query.getLimit());
        MPJLambdaWrapper<Contract> wrapper = new MPJLambdaWrapper<Contract>()
                .selectAll(Contract.class) // Contract 表字段为 creater_id，无 creator_id 冲突
                .selectAs(Customer::getName, ContractVO::getCustomerName)
                .leftJoin(Customer.class, Customer::getId, Contract::getCustomerId)
                .eq(Contract::getOwnerId, SecurityUser.getManagerId())
                .eq(Contract::getDeleteFlag, 0)
                .orderByDesc(Contract::getCreateTime);

        // 枚举状态筛选（避免无效值）
        if (query.getStatus() != null) {
            if (ContractStatusEnum.getByValue(query.getStatus()) == null) {
                throw new ServerException("无效的合同状态");
            }
            wrapper.eq(Contract::getStatus, query.getStatus());
        }

        // 原有筛选条件
        if (StringUtils.isNotBlank(query.getName())) wrapper.like(Contract::getName, query.getName());
        if (query.getCustomerId() != null) wrapper.eq(Contract::getCustomerId, query.getCustomerId());

        Page<ContractVO> resultPage = contractMapper.selectJoinPage(page, ContractVO.class, wrapper);
        // 关联产品
        resultPage.getRecords().forEach(vo -> {
            List<ContractProduct> products = contractProductMapper.selectList(
                    new LambdaQueryWrapper<ContractProduct>().eq(ContractProduct::getCId, vo.getId())
            );
            vo.setProducts(ContractConvert.INSTANCE.convertToProductVOList(products));
        });
        return new PageResult<>(resultPage.getRecords(), resultPage.getTotal());
    }

    // 核心方法：保存/修改合同（全程使用 creater_id，无 creator_id）
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(ContractVO contractVO) {
        boolean isNew = contractVO.getId() == null;
        if (isNew && contractMapper.exists(new LambdaQueryWrapper<Contract>()
                .eq(Contract::getName, contractVO.getName())
                .eq(Contract::getDeleteFlag, 0))) {
            throw new ServerException("合同名称已存在");
        }

        Contract contract = ContractConvert.INSTANCE.convert(contractVO);
        // 关键：设置创建人ID（字段为 creater_id，与数据库一致）
        Integer currentManagerId = SecurityUser.getManagerId();
        if (currentManagerId == null) {
            throw new ServerException("创建人ID获取失败，请登录后操作");
        }
        contract.setCreaterId(currentManagerId); // 统一用 creater_id
        contract.setOwnerId(currentManagerId);

        // 默认状态：初始化（枚举值）
        if (contract.getStatus() == null) {
            contract.setStatus(ContractStatusEnum.INIT.getValue());
        }
        if (contract.getReceivedAmount() == null) {
            contract.setReceivedAmount(BigDecimal.ZERO);
        }

        if (isNew) {
            contract.setNumber(com.crm.utils.NumberUtils.generateContractNumber());
            contract.setCreateTime(LocalDateTime.now());
            contract.setUpdateTime(LocalDateTime.now());
            contract.setDeleteFlag(0);
            contractMapper.insert(contract);
        } else {
            Contract old = contractMapper.selectById(contractVO.getId());
            if (old == null) {
                throw new ServerException("合同不存在");
            }
            // 审核中不可修改（枚举判断）
            if (ContractStatusEnum.UNDER_REVIEW.getValue().equals(old.getStatus())) {
                throw new ServerException("审核中合同无法修改");
            }
            contract.setUpdateTime(LocalDateTime.now());
            contractMapper.updateById(contract);
        }
        handleContractProducts(contract.getId(), contractVO.getProducts());
    }

    // 原有方法：保留（无 creator_id 相关字段）
    @Override
    public List<ContractTrendPieVO> getContractStatusPieData() {
        return contractMapper.countByStatus(SecurityUser.getManagerId());
    }

    // 原有方法：状态改为枚举（无 creator_id 相关字段）
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startApproval(IdQuery idQuery) {
        Contract contract = contractMapper.selectById(idQuery.getId());
        if (contract == null) {
            throw new ServerException("合同不存在");
        }
        // 仅初始化可发起（枚举判断）
        if (!ContractStatusEnum.INIT.getValue().equals(contract.getStatus())) {
            throw new ServerException("只有初始化状态的合同可发起审核");
        }
        // 改为审核中（枚举值）
        contract.setStatus(ContractStatusEnum.UNDER_REVIEW.getValue());
        contract.setUpdateTime(LocalDateTime.now());
        contractMapper.updateById(contract);
    }

    // 核心改造：审核内容+邮件通知（使用 creater_id 查询创建人）
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approvalContract(ApprovalQuery query) {
        // 审核内容必填
        if (StringUtils.isBlank(query.getComment())) {
            throw new ServerException("请填写审核意见");
        }

        Contract contract = contractMapper.selectById(query.getId());
        if (contract == null) {
            throw new ServerException("合同不存在");
        }
        // 仅审核中可操作（枚举判断）
        if (!ContractStatusEnum.UNDER_REVIEW.getValue().equals(contract.getStatus())) {
            throw new ServerException("合同未在审核状态");
        }

        // 保存审核记录（创建人ID用 creater_id）
        Approval approval = new Approval();
        approval.setStatus(query.getType());
        approval.setCreaterId(SecurityUser.getManagerId()); // 统一用 creater_id
        approval.setContractId(query.getId());
        approval.setComment(query.getComment());
        approval.setCreateTime(LocalDateTime.now());
        approvalMapper.insert(approval);

        // 更新合同状态（枚举值）
        Integer targetStatus = query.getType() == 0
                ? ContractStatusEnum.APPROVED.getValue()
                : ContractStatusEnum.REJECTED.getValue();
        contract.setStatus(targetStatus);
        contract.setUpdateTime(LocalDateTime.now());
        contractMapper.updateById(contract);

        // 无论审核通过还是拒绝都发送邮件
        sendApprovalEmail(contract, query.getType() == 0, query.getComment());
    }

    // 邮件发送方法（根据 creater_id 查询创建人邮箱）
    private void sendApprovalEmail(Contract contract, boolean isApproved, String comment) {
        try {
            // 查询销售（合同创建人）：用 contract.getCreaterId()（creater_id 字段）
            Manager seller = managerMapper.selectById(contract.getCreaterId());
            if (seller == null || StringUtils.isBlank(seller.getEmail())) {
//                log.warn("合同创建人邮箱不存在，无法发送邮件。合同ID: {}", contract.getId());
                return;
            }

            // 构建邮件主题和内容
            String subject = isApproved ? "合同审核通过通知" : "合同审核未通过通知";
            String content = String.format(
                    "您的合同《%s》已%s审核！\n审核意见：%s\n合同编号：%s\n审核时间：%s",
                    contract.getName(),
                    isApproved ? "通过" : "未通过",
                    comment,
                    contract.getNumber(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );

            // 发送邮件
            emailService.sendSimpleMail(
                    seller.getEmail(),
                    subject,
                    content
            );
        } catch (Exception e) {
            // 邮件失败不影响主流程
        }
    }

    // 新增：当日审核总数（通过+拒绝）
    @Override
    public Integer countTodayApprovalTotal() {
        String today = LocalDate.now().toString();
        Integer managerId = SecurityUser.getManagerId();
        // 统计通过+拒绝
        int approved = contractMapper.countByStatusAndDate(managerId, today, ContractStatusEnum.APPROVED.getValue());
        int rejected = contractMapper.countByStatusAndDate(managerId, today, ContractStatusEnum.REJECTED.getValue());
        return approved + rejected;
    }

    // 原有工具方法：保留（无 creator_id 相关字段）
    private void handleContractProducts(Integer contractId, List<ProductVO> products) {
        contractProductMapper.delete(new LambdaQueryWrapper<ContractProduct>().eq(ContractProduct::getCId, contractId));
        if (products != null && !products.isEmpty()) {
            products.forEach(vo -> {
                ContractProduct cp = new ContractProduct();
                cp.setCId(contractId);
                cp.setPId(vo.getId());
                cp.setPName(vo.getPName());
                cp.setPrice(vo.getPrice());
                cp.setCount(vo.getCount());
                cp.setTotalPrice(vo.getPrice().multiply(new BigDecimal(vo.getCount())));
                contractProductMapper.insert(cp);
            });
        }
    }

    // 移除重复的邮件发送方法（避免冲突，统一使用上面的 sendApprovalEmail）
}