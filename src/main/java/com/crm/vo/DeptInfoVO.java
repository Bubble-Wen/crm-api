package com.crm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

// 新增DeptInfoVO.java
@Data
@Schema(description = "部门信息VO")
public class DeptInfoVO {
    @Schema(description = "部门ID")
    private Integer deptId;
    @Schema(description = "部门名称")
    private String deptName;
    @Schema(description = "父级ID链")
    private String parentIds;
    @Schema(description = "可访问的部门ID列表")
    private List<Integer> accessibleDeptIds;
}