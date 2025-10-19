package com.crm.convert;

import com.crm.entity.Customer;
import com.crm.vo.CustomerVO;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerConvert {
    static CustomerConvert getInstance() {
        return org.mapstruct.factory.Mappers.getMapper(CustomerConvert.class);
    }

    Customer convert(CustomerVO customerVO);
}
