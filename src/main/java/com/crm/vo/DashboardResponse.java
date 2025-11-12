package com.crm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 仪表盘响应数据
 */
@Data
@Schema(description = "首页统计数据")
public class DashboardResponse {
    private StatisticsData statistics;
    private TrendData trend;
}
