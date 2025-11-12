package com.crm.query;

import lombok.Data;
import java.util.List;

/**
 * 合同趋势查询参数类
 * 用于查询合同在不同时间维度的趋势数据
 * @author alani
 */
@Data
public class ContractTrendQuery {
    // 时间数组，存储开始时间和结束时间，格式如["2025-01-01", "2025-12-31"]
    private List<String> timeRange;

    // 时间类型，如"day"（按天）、"week"（按周）、"monthrange"（按月）等
    private String transactionType;

    // 时间格式化类型，用于数据库查询时格式化时间，如"%Y-%m-%d"、"%Y-%m"
    private String timeFormat;
}