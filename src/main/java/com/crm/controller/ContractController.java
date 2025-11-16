package com.crm.controller;

import com.crm.common.aop.Log;
import com.crm.common.result.PageResult;
import com.crm.common.result.Result;
import com.crm.enums.BusinessType;
import com.crm.query.ApprovalQuery;
import com.crm.query.ContractQuery;
import com.crm.query.IdQuery;
import com.crm.service.ContractService;
import com.crm.vo.ContractTrendPieVO;
import com.crm.vo.ContractVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
@Tag(name = "合同管理")
@RestController
@RequestMapping("contract")
@AllArgsConstructor
public class ContractController {
    private final ContractService contractService;

    @PostMapping("page")
    @Operation(summary = "合同列表-分页")
    @Log(title = "合同列表-分页", businessType = BusinessType.SELECT)
    public Result<PageResult<ContractVO>> getPage(@RequestBody @Validated ContractQuery query) {
        return Result.ok(contractService.getPage(query));
    }

    @PostMapping("saveOrUpdate")
    @Operation(summary = "保存或修改合同")
    public Result<Void> saveOrUpdate(@RequestBody @Validated ContractVO contractVO) {
        contractService.saveOrUpdate(contractVO);
        return Result.ok();
    }

    // 合同状态饼图统计接口
    @PostMapping("/statusPieData")
    @Operation(summary = "合同状态分布统计（饼图）")
    public Result<List<ContractTrendPieVO>> getContractStatusPieData() {
        return Result.ok(contractService.getContractStatusPieData());
    }

    @PostMapping("startApproval")
    @Operation(summary = "合同审批")
    @Log(title = "合同审批", businessType = BusinessType.INSERT_OR_UPDATE)
    public Result startApproval(@RequestBody @Validated IdQuery idQuery) {
        contractService.startApproval(idQuery);
        return Result.ok();
    }

    @PostMapping("approvalContract")
    @Operation(summary = "合同审批")
    @Log(title = "合同审批", businessType = BusinessType.INSERT_OR_UPDATE)
    public Result approvalContract(@RequestBody @Validated ApprovalQuery query) {
        contractService.approvalContract(query);
        return Result.ok();
    }

    @ApiOperation("统计当日审核总数（通过+拒绝）")
    @PostMapping("countTodayApproval")
    public Result<Integer> countTodayApproval() {
        return Result.ok(contractService.countTodayApprovalTotal());
    }

}
