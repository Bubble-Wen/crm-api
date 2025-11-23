package com.crm.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.crm.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商机VO对象（用于前端传参、后端响应）
 */
@Data
@Schema(description = "商机信息VO")
public class OpportunityVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键id")
    private Integer id;

    @Schema(description = "商机名称")
    @NotBlank(message = "商机名称不能为空")
    private String name;

    @Schema(description = "预估金额")
    @NotNull(message = "预估金额不能为空")
    private BigDecimal estimatedAmount; // 原 budget → 改为 estimatedAmount

    @Schema(description = "客户id")
    @NotNull(message = "客户信息不能为空")
    private Integer customerId; // 修复：删除错误的 consumerId，统一为 customerId（与实体类/前端一致）

    @Schema(description = "客户名称")
    private String customerName; // 非数据库字段，用于前端显示

    @Schema(description = "商品id")
    private Integer productId;

    @Schema(description = "成交概率(%)")
    private Integer successRate; // 原 winRate → 改为 successRate

    @Schema(description = "跟进人id")
    private Integer followerId; // 修复：删除错误的 followUserId，统一为 followerId（与实体类/前端一致）

    @Schema(description = "跟进人名称")
    private String followUserName; // 非数据库字段，用于前端显示

    @Schema(description = "预期成交时间")
    @JsonFormat(pattern = DateUtils.DATE_PATTERN)
    private LocalDate expectedClosingTime; // 原 expectedCloseDate → 改为 expectedClosingTime

    @Schema(description = "下次跟进时间")
    @JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
    private LocalDateTime nextFollowTime;

    @Schema(description = "商机状态：0-潜在，1-跟进中，2-已报价，3-谈判中，4-成交，5-失败")
    private Integer status;

    @Schema(description = "状态变更原因")
    private String statusReason;

    @Schema(description = "备注")
    private String description; // 原 remark → 改为 description

    @Schema(description = "跟进记录列表")
    private List<OpportunityFollowVO> followList;

    // 移除冗余方法：原 getCustomerId() 方法返回 null，导致字段值丢失
}