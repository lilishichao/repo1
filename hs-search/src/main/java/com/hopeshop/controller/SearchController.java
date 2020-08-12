package com.hopeshop.controller;


import com.hopeshop.common.pojo.PageResult;
import com.hopeshop.pojo.Goods;
import com.hopeshop.pojo.SearchRequest;
import com.hopeshop.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by silence on 2020/2/7.
 */
@RestController
public class SearchController {
    @Autowired
    private SearchService searchService;

    @PostMapping("page")
    public ResponseEntity<PageResult<Goods>> queryGoodsBykey(@RequestBody SearchRequest searchRequest){
        PageResult<Goods> pageResult= searchService.search(searchRequest);
        searchService.search(searchRequest);
        if(pageResult==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(pageResult);
    }
}
