package com.crm.mapper;

import com.crm.entity.Customer;
import com.crm.query.CustomerTrendQuery;
import com.crm.vo.CustomerTrendVO;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
public interface CustomerMapper extends MPJBaseMapper<Customer> {
    List<CustomerTrendVO> getTradeStatistics(@Param("query") CustomerTrendQuery query);
    List<CustomerTrendVO> getTradeStatisticsByDay(@Param("query") CustomerTrendQuery query);
    List<CustomerTrendVO> getTradeStatisticsByWeek(@Param("query") CustomerTrendQuery query);

    /**
     * 根据创建时间范围统计客户数量
     * @param startTime 开始时间，格式为 "yyyy-MM-dd HH:mm:ss"
     * @param endTime 结束时间，格式为 "yyyy-MM-dd HH:mm:ss"
     * @return 客户数量
     */
    int countByCreateDate(@Param("startTime") String startTime, @Param("endTime") String endTime);

}


