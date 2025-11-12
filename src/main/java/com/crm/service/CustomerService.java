package com.crm.service;

import com.crm.common.result.PageResult;
import com.crm.entity.Customer;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.query.CustomerQuery;
import com.crm.query.CustomerTrendQuery;
import com.crm.query.IdQuery;
import com.crm.vo.CustomerVO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
public interface CustomerService extends IService<Customer> {
    /**
     * 客户列表
     * @param query
     * @return
     */
    PageResult<CustomerVO> getPage(CustomerQuery query);

    /**
     * 导出客户列表
     * @param query
     * @param httpResource
     */
    void exportCustomer(CustomerQuery query, HttpServletResponse httpResource);

    /**
     * 保存或更新客户
     * @param customerVO
     */
    void saveOrUpdate(CustomerVO customerVO);

    /**
     * 删除客户
     * @param ids
     */
    void removeCustomer(List<Integer> ids);

    /**
     * 添加客户到公海
     * @param idQuery
     */
    void customerToPublicPool(IdQuery idQuery);

    void publicPoolToPrivate(IdQuery idQuery);

    /**
     * 公海客户转私海
    void publicPoolToPrivate(IdQuery idQuery);

    /**
     * 客户数量趋势变化
     * @param query
     * @return
     */
    Map<String,List> getCustomerTrendData(CustomerTrendQuery query);
}
