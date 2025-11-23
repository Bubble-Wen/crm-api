package com.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.common.exception.ServerException;
import com.crm.common.result.PageResult;
import com.crm.convert.OpportunityConvert;
import com.crm.common.dto.OpportunityStatusDTO;
import com.crm.entity.Customer;
import com.crm.entity.Opportunity;
import com.crm.entity.OpportunityFollow;
import com.crm.mapper.ContractMapper;
import com.crm.mapper.CustomerMapper;
import com.crm.mapper.OpportunityFollowMapper;
import com.crm.mapper.OpportunityMapper;
import com.crm.query.OpportunityQuery;
import com.crm.security.user.SecurityUser;
import com.crm.service.OpportunityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.vo.ContractVO;
import com.crm.vo.OpportunityVO;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 商机服务实现类
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
@Service
@AllArgsConstructor
public class OpportunityServiceImpl extends ServiceImpl<OpportunityMapper, Opportunity> implements OpportunityService {

    private final OpportunityFollowMapper opportunityFollowMapper;
    private final CustomerMapper customerMapper;
    private final ContractMapper contractMapper;

    @Override
    public PageResult<OpportunityVO> getPage(OpportunityQuery query) {
        Page<Opportunity> page = new Page<>(query.getPage(), query.getLimit());
        LambdaQueryWrapper<Opportunity> wrapper = new LambdaQueryWrapper<>();

        // 条件查询（字段名已全部对齐）
        if (StringUtils.isNotBlank(query.getName())) {
            wrapper.like(Opportunity::getName, query.getName());
        }
        if (query.getCustomerId() != null) {
            wrapper.eq(Opportunity::getCustomerId, query.getCustomerId()); // 已修复：customerId
        }
        if (query.getStatus() != null) {
            wrapper.eq(Opportunity::getStatus, query.getStatus());
        }
        if (query.getFollowUserId() != null) {
            wrapper.eq(Opportunity::getFollowerId, query.getFollowUserId()); // 已修复：followerId
        }

        wrapper.eq(Opportunity::getDeleteFlag, 0)
                .orderByDesc(Opportunity::getUpdateTime);

        Page<Opportunity> opportunityPage = baseMapper.selectPage(page, wrapper);
        List<OpportunityVO> voList = OpportunityConvert.INSTANCE.convertList(opportunityPage.getRecords());

        // 补充客户名称和跟进人名称（字段名已对齐）
        voList.forEach(vo -> {
            // 已修复：vo.getCustomerId()（VO 中已删除 consumerId）
            Customer customer = customerMapper.selectById(vo.getCustomerId());
            if (customer != null) {
                vo.setCustomerName(customer.getName());
            }
            // 可选：补充跟进人名称查询（需关联 Manager 表）
            // Manager follower = managerMapper.selectById(vo.getFollowerId());
            // if (follower != null) {
            //     vo.setFollowUserName(follower.getNickname() || follower.getName());
            // }
        });

        return new PageResult<>(voList, opportunityPage.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrEdit(OpportunityVO opportunityVO) {
        Opportunity opportunity = OpportunityConvert.INSTANCE.convert(opportunityVO);
        Integer currentUserId = SecurityUser.getManagerId();

        // 校验当前登录用户ID
        if (currentUserId == null) {
            throw new ServerException("创建人ID获取失败，请登录后操作");
        }

        if (opportunity.getId() == null) {
            // 新增商机（全量修复）
            LambdaQueryWrapper<Opportunity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Opportunity::getName, opportunity.getName())
                    .eq(Opportunity::getDeleteFlag, 0);
            if (baseMapper.exists(wrapper)) {
                throw new ServerException("商机名称已存在");
            }

            // 设置创建人ID（与合同模块一致）
            opportunity.setCreaterId(currentUserId);
            // 默认跟进人为创建人（字段名已修复：followerId）
            if (opportunity.getFollowerId() == null) {
                opportunity.setFollowerId(currentUserId);
            }
            // 默认状态为潜在（0）
            if (opportunity.getStatus() == null) {
                opportunity.setStatus(0);
            }
            // 逻辑删除默认值（0-未删除，实体类为 Byte 类型，需强制转换）
            opportunity.setDeleteFlag((byte) 0);
            // 创建时间（覆盖自动填充，确保一致性）
            opportunity.setCreateTime(LocalDateTime.now());
            // 保存到数据库
            baseMapper.insert(opportunity);
        } else {
            // 修改商机（全量修复）
            Opportunity oldOpportunity = baseMapper.selectById(opportunity.getId());
            if (oldOpportunity == null || oldOpportunity.getDeleteFlag() == 1) {
                throw new ServerException("商机不存在或已删除");
            }

            // 检查名称是否重复（排除当前记录）
            LambdaQueryWrapper<Opportunity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Opportunity::getName, opportunity.getName())
                    .ne(Opportunity::getId, opportunity.getId())
                    .eq(Opportunity::getDeleteFlag, 0);
            if (baseMapper.exists(wrapper)) {
                throw new ServerException("商机名称已存在");
            }

            // 设置更新时间（覆盖自动填充）
            opportunity.setUpdateTime(LocalDateTime.now());
            // 保留原始创建人、删除标记，避免修改
            opportunity.setCreaterId(oldOpportunity.getCreaterId());
            opportunity.setDeleteFlag(oldOpportunity.getDeleteFlag());
            // 更新到数据库
            baseMapper.updateById(opportunity);
        }
    }

    @Override
    public OpportunityVO getDetail(Integer id) {
        if (id == null) {
            throw new ServerException("商机ID不能为空");
        }

        Opportunity opportunity = baseMapper.selectById(id);
        if (opportunity == null || opportunity.getDeleteFlag() == 1) {
            throw new ServerException("商机不存在或已删除");
        }

        OpportunityVO vo = OpportunityConvert.INSTANCE.convert(opportunity);

        // 查询客户信息（字段名已修复：getCustomerId）
        Customer customer = customerMapper.selectById(opportunity.getCustomerId());
        if (customer != null) {
            vo.setCustomerName(customer.getName());
        }

        // 查询跟进记录
        LambdaQueryWrapper<OpportunityFollow> followWrapper = new LambdaQueryWrapper<>();
        followWrapper.eq(OpportunityFollow::getOpportunityId, id)
                .orderByDesc(OpportunityFollow::getFollowTime);
        List<OpportunityFollow> follows = opportunityFollowMapper.selectList(followWrapper);
        vo.setFollowList(OpportunityConvert.INSTANCE.convertFollowList(follows));

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFollow(OpportunityFollow follow) {
        if (follow == null || follow.getOpportunityId() == null) {
            throw new ServerException("商机ID不能为空");
        }

        // 检查商机是否存在
        Opportunity opportunity = baseMapper.selectById(follow.getOpportunityId());
        if (opportunity == null || opportunity.getDeleteFlag() == 1) {
            throw new ServerException("商机不存在或已删除");
        }

        // 设置跟进人ID（当前登录用户）
        Integer followUserId = SecurityUser.getManagerId();
        if (followUserId == null) {
            throw new ServerException("跟进人ID获取失败，请登录后操作");
        }
        follow.setFollowUserId(followUserId);
        follow.setFollowTime(LocalDateTime.now());
        opportunityFollowMapper.insert(follow);

        // 更新商机下次跟进时间（优化逻辑：优先使用跟进记录的 nextPlan）
        if (follow.getNextPlan() != null && !follow.getNextPlan().trim().isEmpty()) {
            // 假设 nextPlan 是日期字符串，需转换为 LocalDateTime（根据实际格式调整）
            try {
                LocalDateTime nextFollowTime = LocalDateTime.parse(follow.getNextPlan(), java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                opportunity.setNextFollowTime(nextFollowTime);
            } catch (Exception e) {
                // 格式错误时，默认设置为当前跟进时间+1天
                opportunity.setNextFollowTime(LocalDateTime.now().plusDays(1));
            }
        } else {
            opportunity.setNextFollowTime(LocalDateTime.now().plusDays(1));
        }
        baseMapper.updateById(opportunity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(OpportunityStatusDTO statusDTO) {
        if (statusDTO == null || statusDTO.getId() == null) {
            throw new ServerException("商机ID不能为空");
        }
        if (statusDTO.getStatus() == null) {
            throw new ServerException("请选择目标状态");
        }

        Opportunity opportunity = baseMapper.selectById(statusDTO.getId());
        if (opportunity == null || opportunity.getDeleteFlag() == 1) {
            throw new ServerException("商机不存在或已删除");
        }

        // 记录原状态
        Integer oldStatus = opportunity.getStatus();
        // 校验状态变更合法性（可选：根据业务规则限制状态流转）
        // if (!isValidStatusTransition(oldStatus, statusDTO.getStatus())) {
        //     throw new ServerException("状态流转不合法");
        // }

        // 设置新状态和原因
        opportunity.setStatus(statusDTO.getStatus());
        opportunity.setStatusReason(statusDTO.getStatusReason());
        opportunity.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(opportunity);

        // 自动添加状态变更跟进记录
        OpportunityFollow follow = new OpportunityFollow();
        follow.setOpportunityId(opportunity.getId());
        follow.setFollowUserId(SecurityUser.getManagerId());
        follow.setFollowTime(LocalDateTime.now());
        follow.setFollowType(4); // 4-状态变更类型
        follow.setContent("商机状态从 " + getStatusName(oldStatus) + " 变更为 " + getStatusName(statusDTO.getStatus()));
        follow.setNextPlan("无");
        opportunityFollowMapper.insert(follow);
    }

    @Override
    public ContractVO generateContract(Integer opportunityId) {
        if (opportunityId == null) {
            throw new ServerException("商机ID不能为空");
        }

        Opportunity opportunity = baseMapper.selectById(opportunityId);
        if (opportunity == null || opportunity.getDeleteFlag() == 1) {
            throw new ServerException("商机不存在或已删除");
        }

        // 检查商机状态是否为成交（4）
        if (opportunity.getStatus() != 4) {
            throw new ServerException("只有成交状态的商机才能生成合同");
        }

        // 查询客户信息（字段名已修复：getCustomerId）
        Customer customer = customerMapper.selectById(opportunity.getCustomerId());
        if (customer == null) {
            throw new ServerException("关联客户不存在");
        }

        // 构建合同信息（优化空值处理）
        BigDecimal budget = opportunity.getBudget() == null ? BigDecimal.ZERO : opportunity.getBudget();
        ContractVO contractVO = new ContractVO();
        contractVO.setName("合同-" + opportunity.getName());
        contractVO.setAmount(budget);
        contractVO.setReceivedAmount(BigDecimal.ZERO); // 初始未收款（简化写法）
        contractVO.setSignTime(LocalDate.now());
        contractVO.setCustomerId(opportunity.getCustomerId()); // 字段名对齐
        contractVO.setCustomerName(customer.getName());
        contractVO.setOpportunityId(opportunity.getId());
        contractVO.setStatus(0); // 合同初始状态
        contractVO.setStartTime(LocalDate.now());
        contractVO.setEndTime(LocalDate.now().plusYears(1)); // 默认一年期限

        return contractVO;
    }

    /**
     * 获取状态名称（优化空值处理）
     */
    private String getStatusName(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0: return "潜在";
            case 1: return "跟进中";
            case 2: return "已报价";
            case 3: return "谈判中";
            case 4: return "成交";
            case 5: return "失败";
            default: return "未知";
        }
    }

    /**
     * 可选：状态流转合法性校验（根据业务规则扩展）
     */
    private boolean isValidStatusTransition(Integer oldStatus, Integer newStatus) {
        // 示例规则：潜在→跟进中→已报价→谈判中→成交/失败
        switch (oldStatus) {
            case 0: return newStatus == 1; // 潜在只能转跟进中
            case 1: return newStatus == 2; // 跟进中只能转已报价
            case 2: return newStatus == 3; // 已报价只能转谈判中
            case 3: return newStatus == 4 || newStatus == 5; // 谈判中可转成交/失败
            default: return false;
        }
    }
}