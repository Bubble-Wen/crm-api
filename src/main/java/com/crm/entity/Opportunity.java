package com.crm.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.crm.utils.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 商机实体类
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
@Getter
@Setter
@TableName("t_opportunity") // 与数据库表名一致
@ApiModel(value = "Opportunity对象", description = "商机信息")
public class Opportunity {

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("商机名称")
    @TableField("name") // 映射数据库 name 字段
    @NotBlank(message = "商机名称不能为空")
    private String name;

    @ApiModelProperty("预算/预估金额")
    @TableField("budget") // 映射数据库 budget 字段（对应前端 estimatedAmount）
    @NotNull(message = "预估金额不能为空")
    private BigDecimal budget;

    @ApiModelProperty("关联客户id")
    @TableField("customer_id") // 修复：数据库字段是 customer_id，删除原错误的 consumerId
    @NotNull(message = "客户信息不能为空")
    private Integer customerId; // 与前端传参 customerId 一致，与 Service 逻辑对齐

    @ApiModelProperty("客户名称")
    @TableField(exist = false) // 非数据库字段，用于前端显示
    private String customerName;

    @ApiModelProperty("商品id")
    @TableField("product_id") // 保留原始商品id字段（若数据库存在）
    private Integer productId;

    @ApiModelProperty("成交概率(%)")
    @TableField("win_rate") // 映射数据库 win_rate 字段（对应前端 successRate）
    private Integer winRate;

    @ApiModelProperty("跟进人id")
    @TableField("follower_id") // 修复：数据库字段是 follower_id，删除原错误的 followUserId
    private Integer followerId; // 与前端传参 followerId 一致，与 Service 逻辑对齐

    @ApiModelProperty("跟进人名称")
    @TableField(exist = false) // 非数据库字段，用于前端显示
    private String followUserName;

    @ApiModelProperty("预期成交时间")
    @TableField("expected_close_date") // 映射数据库 expected_close_date 字段
    @JsonFormat(pattern = DateUtils.DATE_PATTERN)
    private LocalDateTime expectedCloseDate; // 对应前端 expectedClosingTime

    @ApiModelProperty("下次跟进时间")
    @TableField("next_follow_time") // 映射数据库 next_follow_time 字段
    @JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
    private LocalDateTime nextFollowTime;

    @ApiModelProperty("商机状态：0-潜在，1-跟进中，2-已报价，3-谈判中，4-成交，5-失败")
    @TableField("status") // 映射数据库 status 字段
    private Integer status;

    @ApiModelProperty("状态变更原因")
    @TableField("status_reason") // 映射数据库 status_reason 字段
    private String statusReason;

    @ApiModelProperty("备注")
    @TableField("remark") // 映射数据库 remark 字段（对应前端 description）
    private String remark;

    @ApiModelProperty("创建人id")
    @TableField("creater_id") // 与合同模块一致，映射数据库 creater_id 字段
    private Integer createrId;

    @ApiModelProperty("逻辑删除，0-未删除，1-已删除")
    @TableField(value = "delete_flag", fill = FieldFill.INSERT) // 自动填充初始值 0
    @TableLogic // MyBatis-Plus 逻辑删除注解
    private Byte deleteFlag; // 注意：数据库字段类型是 tinyint，对应 Java Byte 类型

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT) // 自动填充创建时间
    @JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE) // 自动填充更新时间
    @JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
    private LocalDateTime updateTime;
}