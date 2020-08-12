package com.hopeshop.item.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Created by silence on 2020-04-10.
 */
public interface CategoryApi {
    /**
     * 根据商品分类id查询名称
     * @param ids 要查询的分类id集合
     * @return 多个名称的集合
     */
    @GetMapping("category/names")
    public ResponseEntity<List<String>> queryNameByIds(@RequestParam("ids") List<Long> ids);
}
