package com.hopeshop.goods.controller;

import com.hopeshop.goods.client.GoodsClient;
import com.hopeshop.goods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by silence on 2020/1/11.
 */
@Controller
@RequestMapping("item")
public class GoodsController {

    @Autowired
    GoodsService goodsService;
    /**
     * 跳转到商品详情页
     * @param model
     * @param id
     * @return
     */
    @GetMapping("{id}.html")
    public String toItemPage(Model model, @PathVariable("id")Long id){
        Map<String, Object> item = this.goodsService.loadModel(id);
        model.addAttribute("item",item);
        return "item";
    }
}
