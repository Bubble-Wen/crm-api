package com.crm.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.crm.utils.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <p>
 * 商机跟进记录
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
@Getter
@Setter
@TableName("t_opportunity_follow")
@ApiModel(value = "OpportunityFollow对象", description = "商机跟进记录")
public class OpportunityFollow {

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("商机id")
    @TableField("opportunity_id")
    @NotNull(message = "商机id不能为空")
    private Integer opportunityId;

    @ApiModelProperty("跟进时间")
    @TableField("follow_time")
    @JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
    private LocalDateTime followTime;

    @ApiModelProperty("跟进方式：1-电话，2-邮件，3-面谈，4-其他")
    @TableField("follow_type")
    private Integer followType;

    @ApiModelProperty("沟通内容")
    @TableField("content")
    @NotBlank(message = "沟通内容不能为空")
    private String content;

    @ApiModelProperty("下一步计划")
    @TableField("next_plan")
    private String nextPlan;

    @ApiModelProperty("跟进人id")
    @TableField("follow_user_id")
    private Integer followUserId;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
    private LocalDateTime createTime;
}