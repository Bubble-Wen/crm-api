package com.crm.common.filter;

import com.alibaba.fastjson2.filter.SimplePropertyPreFilter;

/**
 * @description: 排除JSON敏感属性
 **/
public class PropertyPerExcludeFilter extends SimplePropertyPreFilter {
    public PropertyPerExcludeFilter addExcludes(String... filters) {
        for (int i = 0; i < filters.length; i++) {
            this.getExcludes().add(filters[i]);
        }
        return this;
    }
}