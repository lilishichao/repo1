package com.hopeshop.repository;

import com.hopeshop.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by silence on 2020/1/8.
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods, Long> {
}
