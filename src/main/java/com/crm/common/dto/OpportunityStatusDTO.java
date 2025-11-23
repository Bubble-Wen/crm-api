package com.crm.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 商机状态变更DTO
 */
@Data
@Schema(description = "商机状态变更参数")
public class OpportunityStatusDTO {
    @Schema(description = "商机id")
    @NotNull(message = "商机id不能为空")
    private Integer id;

    @Schema(description = "新状态：0-潜在，1-跟进中，2-已报价，3-谈判中，4-成交，5-失败")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "状态变更原因")
    private String statusReason;
}

