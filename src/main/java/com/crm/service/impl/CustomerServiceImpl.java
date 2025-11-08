package com.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.common.exception.ServerException;
import com.crm.common.result.PageResult;
import com.crm.convert.CustomerConvert;
import com.crm.entity.Customer;
import com.crm.entity.SysManager;
import com.crm.mapper.CustomerMapper;
import com.crm.query.CustomerQuery;
import com.crm.query.IdQuery;
import com.crm.security.user.SecurityUser;
import com.crm.service.CustomerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.utils.ExcelUtils;
import com.crm.vo.CustomerVO;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Override
    public PageResult<CustomerVO> getPage(CustomerQuery query) {

//        1、声明分页参数
        Page<CustomerVO> page = new Page<>(query.getPage(), query.getLimit());
        MPJLambdaWrapper<Customer> wrapper = selectCondition(query);
        Page<CustomerVO> result = baseMapper.selectJoinPage(page, CustomerVO.class, wrapper);

        return new PageResult<>(result.getRecords(), page.getTotal());
    }

    @Override
    public void saveOrUpdate(CustomerVO customerVO) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<Customer>().eq(Customer::getPhone, customerVO.getPhone());
        if (customerVO.getId() == null) {
//            1、判断手机号信息是否已经存在
            Customer customer = baseMapper.selectOne(wrapper);
            if (customer != null) {
                throw new ServerException("该手机号客户已经存在，请勿重复添加客户信息");
            }

            Customer convertCustomer = CustomerConvert.INSTANCE.convert(customerVO);
//            2、获取新增的管理员信息
            Integer managerId = SecurityUser.getManagerId();
            convertCustomer.setCreaterId(managerId);
            convertCustomer.setOwnerId(managerId);
            baseMapper.insert(convertCustomer);
        } else {
            wrapper.ne(Customer::getId, customerVO.getId());
            Customer customer = baseMapper.selectOne(wrapper);
            if (customer != null) {
                throw new ServerException("该手机号客户已经存在，请勿重复添加客户信息");
            }
            Customer convertCustomer = CustomerConvert.INSTANCE.convert(customerVO);
            baseMapper.updateById(convertCustomer);
        }
    }

    @Override
    public void removeCustomer(List<Integer> ids) {
        removeByIds(ids);
    }


    @Override
    public void exportCustomer(CustomerQuery query, HttpServletResponse response) {
        MPJLambdaWrapper<Customer> wrapper = selectCondition(query);
        List<CustomerVO> list = baseMapper.selectJoinList(CustomerVO.class, wrapper);
        ExcelUtils.writeExcel(response, list, "客户信息", "客户信息", CustomerVO.class);
    }

    @Override
    public void customerToPublicPool(IdQuery idQuery) {
        Customer customer = baseMapper.selectById(idQuery.getId());
        if (customer == null) {
            throw new ServerException("客户不存在,无法转入公海");
        }
        customer.setIsPublic(1);
        customer.setOwnerId(null);
        baseMapper.updateById(customer);
    }

    @Override
    public void publicPoolToPrivate(IdQuery idQuery) {
        Customer customer = baseMapper.selectById(idQuery.getId());
        if (customer == null) {
            throw new ServerException("客户不存在,无法转入公海");
        }
        customer.setIsPublic(0);
        Integer ownerId = SecurityUser.getManagerId();
        customer.setOwnerId(ownerId);
        baseMapper.updateById(customer);
    }

    private MPJLambdaWrapper<Customer> selectCondition(CustomerQuery query) {
        MPJLambdaWrapper<Customer> wrapper = new MPJLambdaWrapper<>();
//        2、构建查询关系
        wrapper.selectAll(Customer.class)
                .selectAs("o", SysManager::getAccount, CustomerVO::getOwnerName)
                .selectAs("c", SysManager::getAccount, CustomerVO::getCreaterName)
                .leftJoin(SysManager.class, "o", SysManager::getId, Customer::getOwnerId)
                .leftJoin(SysManager.class, "c", SysManager::getId, Customer::getCreaterId);
//        3、构建查询条件
        if (StringUtils.isNotBlank(query.getName())) {
            wrapper.like(Customer::getName, query.getName());
        }
        if (StringUtils.isNotBlank(query.getPhone())) {
            wrapper.like(Customer::getPhone, query.getPhone());
        }
        if (query.getLevel() != null) {
            wrapper.eq(Customer::getLevel, query.getLevel());
        }
        if (query.getSource() != null) {
            wrapper.eq(Customer::getSource, query.getSource());
        }
        if (query.getFollowStatus() != null) {
            wrapper.eq(Customer::getFollowStatus, query.getFollowStatus());
        }
        if (query.getIsPublic() != null) {
            wrapper.eq(Customer::getIsPublic, query.getIsPublic());
        }
        wrapper.orderByDesc(Customer::getCreateTime);

        return wrapper;
    }
}