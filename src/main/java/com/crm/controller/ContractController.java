package com.crm.controller;

import com.crm.common.aop.Log;
import com.crm.common.result.PageResult;
import com.crm.common.result.Result;
import com.crm.enums.BusinessType;
import com.crm.query.ContractQuery;
import com.crm.service.ContractService;
import com.crm.vo.ContractTrendPieVO;
import com.crm.vo.ContractVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public Result<PageResult<ContractVO>> getPage(@RequestBody @Validated ContractQuery contractQuery){
        return Result.ok(contractService.getPage(contractQuery));
    }

    @PostMapping("saveOrEdit")
    @Operation(summary = "保存或修改合同")
    public Result<Void> saveOrUpdate(@RequestBody @Validated ContractVO contractVO){
        contractService.saveOrUpdate(contractVO);
        return Result.ok();
    }

    // 合同状态饼图统计接口
    @PostMapping("/statusPieData")
    @Operation(summary = "合同状态分布统计（饼图）")
    public Result<List<ContractTrendPieVO>> getContractStatusPieData() {
        return Result.ok(contractService.getContractStatusPieData());
    }
}
