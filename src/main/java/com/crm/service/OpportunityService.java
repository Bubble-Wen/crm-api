package com.crm.service;

import com.crm.common.result.PageResult;
import com.crm.common.dto.OpportunityStatusDTO;
import com.crm.entity.Opportunity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.entity.OpportunityFollow;
import com.crm.query.OpportunityQuery;
import com.crm.vo.ContractVO;
import com.crm.vo.OpportunityVO;

/**
 * <p>
 * 商机服务接口
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
public interface OpportunityService extends IService<Opportunity> {

    /**
     * 分页查询商机列表
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<OpportunityVO> getPage(OpportunityQuery query);

    /**
     * 保存或修改商机
     * @param opportunityVO 商机信息
     */
    void saveOrEdit(OpportunityVO opportunityVO);

    /**
     * 获取商机详情
     * @param id 商机id
     * @return 商机详情
     */
    OpportunityVO getDetail(Integer id);

    /**
     * 添加商机跟进记录
     * @param follow 跟进记录
     */
    void addFollow(OpportunityFollow follow);

    /**
     * 更新商机状态
     * @param statusDTO 状态变更信息
     */
    void updateStatus(OpportunityStatusDTO statusDTO);

    /**
     * 从商机生成合同草稿
     * @param opportunityId 商机id
     * @return 合同草稿
     */
    ContractVO generateContract(Integer opportunityId);
}