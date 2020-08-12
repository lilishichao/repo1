package com.hopeshop;

import com.hopeshop.item.pojo.Sku;
import com.hopeshop.item.pojo.SpuBo;
import com.hopeshop.client.GoodsClient;
import com.hopeshop.common.pojo.PageResult;
import com.hopeshop.item.pojo.SpuDetail;
import com.hopeshop.pojo.Goods;
import com.hopeshop.repository.GoodsRepository;
import com.hopeshop.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by silence on 2020/1/8.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class ElasticsearchTest {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    GoodsClient goodsClient;

    @Autowired
    SearchService searchService;

    @Test
    public void createIndex(){
        // 创建索引
        this.elasticsearchTemplate.createIndex(Goods.class);
        // 配置映射
        this.elasticsearchTemplate.putMapping(Goods.class);
    }

    @Test
    public void loadData(){
        // 创建索引
        this.elasticsearchTemplate.createIndex(Goods.class);
        // 配置映射
        this.elasticsearchTemplate.putMapping(Goods.class);
        Integer page = 1;
        Integer rows = 100;
        Integer size = 0;
        do {
            // 查询分页数据
            PageResult<SpuBo> result = this.goodsClient.querySpuByPage(page,rows,null,true,null,true).getBody();
            List<SpuBo> spus = result.getItems();
            size = spus.size();
            // 创建Goods集合
            List<Goods> goodsList = new ArrayList<>();
            // 遍历spu
            for (SpuBo spu : spus) {
                try {
                    Goods goods = this.searchService.buildGoods(spu);
                    goodsList.add(goods);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            this.goodsRepository.saveAll(goodsList);
            page++;
        } while (size == 100);
    }
}
