package com.hopeshop.pojo;

import com.hopeshop.common.pojo.PageResult;
import com.hopeshop.item.pojo.Brand;
import com.hopeshop.item.pojo.Category;

import java.util.List;
import java.util.Map;

/**
 * Created by silence on 2020/1/10.
 */
public class SearchResult extends PageResult<Goods> {

    private List<Category> categories;

    private List<Brand> brands;

    private List<Map<String,Object>> specs; // 规格参数过滤条件

    public SearchResult(Long total, Long totalPage, List<Goods> items, List<Category> categories, List<Brand> brands,List<Map<String,Object>> specs) {
        super(total, totalPage, items);
        this.categories = categories;
        this.brands = brands;
        this.specs = specs;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Brand> getBrands() {
        return brands;
    }

    public void setBrands(List<Brand> brands) {
        this.brands = brands;
    }

    public List<Map<String, Object>> getSpecs() {
        return specs;
    }

    public void setSpecs(List<Map<String, Object>> specs) {
        this.specs = specs;
    }

}
