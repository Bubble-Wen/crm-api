package com.crm.convert;

import com.crm.entity.Opportunity;
import com.crm.entity.OpportunityFollow;
import com.crm.vo.OpportunityFollowVO;
import com.crm.vo.OpportunityVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 商机对象转换类（VO ↔ 实体类）
 */
@Mapper
public interface OpportunityConvert {
    OpportunityConvert INSTANCE = Mappers.getMapper(OpportunityConvert.class);

    /**
     * VO → 实体类（前端提交参数 → 数据库存储）
     * 明确映射规则，忽略后端手动设置/自动填充的字段
     */
    @Mapping(target = "budget", source = "estimatedAmount") // 前端 estimatedAmount → 实体 budget
    @Mapping(target = "winRate", source = "successRate") // 前端 successRate → 实体 winRate
    @Mapping(target = "expectedCloseDate", source = "expectedClosingTime") // 前端 expectedClosingTime → 实体 expectedCloseDate
    @Mapping(target = "remark", source = "description") // 前端 description → 实体 remark
    @Mapping(target = "createrId", ignore = true) // 后端手动设置创建人ID
    @Mapping(target = "deleteFlag", ignore = true) // MyBatis-Plus 自动填充（0-未删除）
    @Mapping(target = "createTime", ignore = true) // MyBatis-Plus 自动填充创建时间
    @Mapping(target = "updateTime", ignore = true) // MyBatis-Plus 自动填充更新时间
    @Mapping(target = "nextFollowTime", ignore = true) // 下次跟进时间由跟进功能设置，前端不直接传
    Opportunity convert(OpportunityVO vo);


    /**
     * 实体类 → VO（数据库查询结果 → 前端显示）
     */
    @Mapping(target = "estimatedAmount", source = "budget")
    @Mapping(target = "successRate", source = "winRate")
    @Mapping(target = "expectedClosingTime", source = "expectedCloseDate")
    @Mapping(target = "description", source = "remark")
    OpportunityVO convert(Opportunity entity);

    /**
     * 实体类列表 → VO列表
     */
    List<OpportunityVO> convertList(List<Opportunity> list);

    /**
     * 跟进记录实体类列表 → 跟进记录VO列表
     */
    List<OpportunityFollowVO> convertFollowList(List<OpportunityFollow> list);
}