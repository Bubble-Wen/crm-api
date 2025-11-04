// crm/service/ProductService.java
package com.crm.service;

import com.crm.common.result.PageResult;
import com.crm.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.query.ProductQuery;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
public interface ProductService extends IService<Product> {
    // 分页查询
    PageResult<Product> getPage(ProductQuery query);

    // 新增或修改
    void saveOrEdit(Product product);

    // 定时任务：批量更新商品状态
    void batchUpdateProductState();
}