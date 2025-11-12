package com.crm.service;

import com.crm.common.result.PageResult;
import com.crm.entity.Contract;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.query.ContractQuery;
import com.crm.vo.ContractTrendPieVO;
import com.crm.vo.ContractVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
public interface ContractService extends IService<Contract> {

    // 分页查询合同列表方法
    PageResult<ContractVO> getPage(ContractQuery query);

    // 保存或更新合同方法
    void saveOrUpdate(ContractVO contractVO);

    // 新增：按合同状态统计饼图数据
    List<ContractTrendPieVO> getContractStatusPieData();
}
