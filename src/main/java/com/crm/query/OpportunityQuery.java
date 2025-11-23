package com.crm.query;

import com.crm.common.model.Query;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商机查询对象
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "商机查询参数")
public class OpportunityQuery extends Query {
    @Schema(description = "商机名称")
    private String name;

    @Schema(description = "客户id")
    private Integer customerId;

    @Schema(description = "商机状态：0-潜在，1-跟进中，2-已报价，3-谈判中，4-成交，5-失败")
    private Integer status;

    @Schema(description = "跟进人id")
    private Integer followUserId;
}