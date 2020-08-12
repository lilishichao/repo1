package com.hopeshop.item.api;

import com.hopeshop.item.pojo.Brand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface BrandApi {
    @GetMapping("brand/list")
    List<Brand> queryBrandByIds(@RequestParam("ids") List<Long> ids);

}
