package com.crm.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.crm.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商机跟进记录VO
 */
@Data
@Schema(description = "商机跟进记录")
public class OpportunityFollowVO {
    @Schema(description = "主键id")
    private Integer id;

    @Schema(description = "商机id")
    @NotNull(message = "商机id不能为空")
    private Integer opportunityId;

    @Schema(description = "跟进时间")
    @JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
    private LocalDateTime followTime;

    @Schema(description = "跟进方式：1-电话，2-邮件，3-面谈，4-其他")
    private Integer followType;

    @Schema(description = "跟进方式名称")
    private String followTypeName;

    @Schema(description = "沟通内容")
    @NotBlank(message = "沟通内容不能为空")
    private String content;

    @Schema(description = "下一步计划")
    private String nextPlan;

    @Schema(description = "跟进人名称")
    private String followUserName;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
    private LocalDateTime createTime;
}