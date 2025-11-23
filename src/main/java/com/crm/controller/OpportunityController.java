package com.crm.controller;

import com.crm.common.aop.Log;
import com.crm.common.result.PageResult;
import com.crm.common.result.Result;
import com.crm.common.dto.OpportunityStatusDTO;
import com.crm.entity.OpportunityFollow;
import com.crm.enums.BusinessType;
import com.crm.query.OpportunityQuery;
import com.crm.service.OpportunityService;
import com.crm.vo.ContractVO;
import com.crm.vo.OpportunityVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 商机控制器
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
@Tag(name = "商机管理")
@RestController
@RequestMapping("opportunity")
@AllArgsConstructor
public class OpportunityController {
    private final OpportunityService opportunityService;

    @PostMapping("page")
    @Operation(summary = "分页查询商机列表")
    @Log(title = "商机管理-分页列表", businessType = BusinessType.SELECT)
    public Result<PageResult<OpportunityVO>> getPage(@RequestBody @Validated OpportunityQuery query) {
        return Result.ok(opportunityService.getPage(query));
    }

    @GetMapping("detail/{id}")
    @Operation(summary = "获取商机详情")
    public Result<OpportunityVO> getDetail(@PathVariable Integer id) {
        return Result.ok(opportunityService.getDetail(id));
    }

    @PostMapping("saveOrEdit")
    @Operation(summary = "保存或修改商机")
    @Log(title = "商机管理-保存或修改", businessType = BusinessType.INSERT_OR_UPDATE)
    public Result saveOrEdit(@RequestBody @Validated OpportunityVO opportunityVO) {
        opportunityService.saveOrEdit(opportunityVO);
        return Result.ok();
    }

    @PostMapping("addFollow")
    @Operation(summary = "添加商机跟进记录")
    @Log(title = "商机管理-添加跟进记录", businessType = BusinessType.INSERT_OR_UPDATE)
    public Result addFollow(@RequestBody @Validated OpportunityFollow follow) {
        opportunityService.addFollow(follow);
        return Result.ok();
    }

    @PostMapping("updateStatus")
    @Operation(summary = "更新商机状态")
    @Log(title = "商机管理-更新状态", businessType = BusinessType.INSERT_OR_UPDATE)
    public Result updateStatus(@RequestBody @Validated OpportunityStatusDTO statusDTO) {
        opportunityService.updateStatus(statusDTO);
        return Result.ok();
    }

    @GetMapping("generateContract/{id}")
    @Operation(summary = "从商机生成合同草稿")
    public Result<ContractVO> generateContract(@PathVariable Integer id) {
        return Result.ok(opportunityService.generateContract(id));
    }
}