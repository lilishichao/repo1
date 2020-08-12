package com.hopeshop.service;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hopeshop.client.BrandClient;
import com.hopeshop.client.CategoryClient;
import com.hopeshop.client.GoodsClient;
import com.hopeshop.client.SpecificationClient;
import com.hopeshop.item.pojo.*;
import com.hopeshop.pojo.Goods;
import com.hopeshop.pojo.SearchRequest;
import com.hopeshop.pojo.SearchResult;
import com.hopeshop.repository.GoodsRepository;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Created by silence on 2020/1/8.
 */
@Service
public class SearchService {

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    private ObjectMapper mapper = new ObjectMapper();

    public Goods buildGoods(Spu spu) {
        Goods goods = new Goods();

        // 查询商品分类名称
        List<String> names = this.categoryClient.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3())).getBody();
        // 查询sku
        List<Sku> skus = this.goodsClient.querySkuBySpuId(spu.getId()).getBody();
        // 查询详情querySpuDetailById
        SpuDetail spuDetail = this.goodsClient.querySpuDetailById(spu.getId()).getBody();

        // 处理sku，仅封装id、价格、标题、图片，并获得价格集合
        List<Long> prices = new ArrayList<>();
        List<Map<String, Object>> skuList = new ArrayList<>();
        skus.forEach(sku -> {
            prices.add(sku.getPrice());
            Map<String, Object> skuMap = new HashMap<>();
            skuMap.put("id", sku.getId());
            skuMap.put("title", sku.getTitle());
            skuMap.put("price", sku.getPrice());
            skuMap.put("image", StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
            skuList.add(skuMap);
        });

        try {
            Map<String, Object> specialSpecs = mapper.readValue(spuDetail.getSpecTemplate(), new TypeReference<Map<String, Object>>() {});

            // 获取可搜索的规格参数
            Map<String, Object> searchSpec = new HashMap<>();

            // 过滤规格模板，把所有可搜索的信息保存到Map中
            Map<String, Object> specMap = new HashMap<>();
            List<Object> params = JSON.parseArray(spuDetail.getSpecifications());
            params.forEach(p -> {
                Map<String,Object> map = JSON.parseObject(p.toString(),HashMap.class);
                Set<String> sets = map.keySet();
                for(String key:sets){
                    if(key.equals("params")){
                        List<Object> ps = JSON.parseArray(map.get(key).toString());
                        if(ps.size()>0) {
                            for (Object op : ps) {
                                Map<String, Object> pm = JSON.parseObject(op.toString(), HashMap.class);
                                String k = pm.get("k").toString();
                                Object v = pm.get("v");
                                if(Boolean.valueOf(pm.get("searchable").toString())){
                                    if(Boolean.valueOf(pm.get("global").toString())){
                                        specMap.put(k, v==null ? "其它" :v);
                                    }else{
                                        specMap.put(k, specialSpecs.get(k));
                                    }
                                }
                            }
                        }
                    }
                }
            });

            goods.setId(spu.getId());
            goods.setSubTitle(spu.getSubTitle());
            goods.setBrandId(spu.getBrandId());
            goods.setCid1(spu.getCid1());
            goods.setCid2(spu.getCid2());
            goods.setCid3(spu.getCid3());
            goods.setCreateTime(spu.getCreateTime());
            goods.setAll(spu.getTitle() + " " + StringUtils.join(names, " "));
            goods.setPrice(prices);
            goods.setSkus(mapper.writeValueAsString(skuList));
            goods.setSpecs(specMap);
        } catch (Exception e) {
            System.out.println("---------"+spuDetail+"---------");
        }
        return goods;
    }

    public SearchResult search(SearchRequest request) {
        String key = request.getKey();
        // 判断是否有搜索条件，如果没有，直接返回null。不允许搜索全部商品
        if (StringUtils.isBlank(key)) {
            return null;
        }

        // 1、构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        MatchQueryBuilder basicQuery = QueryBuilders.matchQuery("all", key).operator(Operator.AND);

        // 1.1、基本查询
        queryBuilder.withQuery(QueryBuilders.matchQuery("all", request.getKey()));
        // 通过sourceFilter设置返回的结果字段,我们只需要id、skus、subTitle
        queryBuilder.withSourceFilter(new FetchSourceFilter(
                new String[]{"id", "skus", "subTitle"}, null));

        // 1.2.分页排序
        searchWithPageAndSort(queryBuilder,request);

        QueryBuilder basicquery = buildBasicQueryWithFilter(request);
        queryBuilder.withQuery(basicquery);

        // 1.3、聚合
        String categoryAggName = "category"; // 商品分类聚合名称
        String brandAggName = "brand"; // 品牌聚合名称
        // 对商品分类进行聚合
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        // 对品牌进行聚合
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        // 2、查询，获取结果
        AggregatedPage<Goods> pageInfo = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());

        // 3、解析查询结果
        // 3.1、分页信息
        Long total = pageInfo.getTotalElements();
        int totalPage = (total.intValue() + request.getSize() - 1) / request.getSize();
        // 3.2、商品分类的聚合结果
        List<Category> categories =
                getCategoryAggResult(pageInfo.getAggregation(categoryAggName));
        // 3.3、品牌的聚合结果
        List<Brand> brands = getBrandAggResult(pageInfo.getAggregation(brandAggName));

        // 根据商品分类判断是否需要聚合
        List<Map<String, Object>> specs = new ArrayList<>();
        if (categories.size() == 1) {
            // 如果商品分类只有一个才进行聚合，并根据分类与基本查询条件聚合
            specs = getSpec(categories.get(0).getId(), basicQuery);
        }

        // 返回结果
        return new SearchResult(pageInfo.getTotalElements(), Long.valueOf(pageInfo.getTotalPages()), pageInfo.getContent(), categories, brands,specs);
    }

    // 构建基本查询条件
    private QueryBuilder buildBasicQueryWithFilter(SearchRequest request) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        // 基本查询条件
        queryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND));
        // 过滤条件构建器
        BoolQueryBuilder filterQueryBuilder = QueryBuilders.boolQuery();
        // 整理过滤条件
        Map<String, String> filter = request.getFilter();
        for (Map.Entry<String, String> entry : filter.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            // 商品分类和品牌要特殊处理
            if (key != "cid3" && key != "brandId") {
                key = "specs." + key + ".keyword";
            }
            // 字符串类型，进行term查询
            filterQueryBuilder.must(QueryBuilders.termQuery(key, value));
        }
        // 添加过滤条件
        queryBuilder.filter(filterQueryBuilder);
        return queryBuilder;
    }
    /**
     * 聚合出规格参数
     *
     * @param cid
     * @param query
     * @return
     */
    private List<Map<String, Object>> getSpec(Long cid, QueryBuilder query) {
        try {
            // 不管是全局参数还是sku参数，只要是搜索参数，都根据分类id查询出来
            String specification = this.specificationClient.querySpecificationByCategoryId(cid).getBody();

            List<Map<String, Object>> specs = new ArrayList<>();

            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
            queryBuilder.withQuery(query);
            List<Object> params = JSON.parseArray(specification);
            // 聚合规格参数
            params.forEach(p -> {
                Map<String,Object> map = JSON.parseObject(p.toString(),HashMap.class);
                Set<String> sets = map.keySet();
                for(String key:sets){
                    if(key.equals("params")){
                        List<Object> ps = JSON.parseArray(map.get(key).toString());
                        if(ps.size()>0) {
                            for (Object op : ps) {
                                Map<String, Object> pm = JSON.parseObject(op.toString(), HashMap.class);
                                String k = pm.get("k").toString();
                                if(Boolean.valueOf(pm.get("searchable").toString())){
                                    queryBuilder.addAggregation(AggregationBuilders.terms(k).field("specs." + k + ".keyword"));
                                }
                            }
                        }
                    }
                }
            });

            // 查询
            Map<String, Aggregation> aggs = this.elasticsearchTemplate.query(queryBuilder.build(),
                    SearchResponse::getAggregations).asMap();

            // 解析聚合结果
            params.forEach(param -> {
                Map<String,Object> map = JSON.parseObject(param.toString(),HashMap.class);
                Set<String> sets = map.keySet();
                for(String key:sets){
                    if(key.equals("params")){
                        List<Object> ps = JSON.parseArray(map.get(key).toString());
                        if(ps.size()>0) {
                            for (Object op : ps) {
                                Map<String, Object> pm = JSON.parseObject(op.toString(), HashMap.class);
                                String k = pm.get("k").toString();
                                if(Boolean.valueOf(pm.get("searchable").toString())) {
                                    Map<String, Object> spec = new HashMap<>();
                                    spec.put("k", k);
                                    StringTerms terms = (StringTerms) aggs.get(k);
                                    spec.put("options", terms.getBuckets().stream().map(StringTerms.Bucket::getKeyAsString));
                                    specs.add(spec);
                                }
                            }
                        }
                    }
                }
            });
            return specs;
        } catch (Exception e)

        {
            e.printStackTrace();
            return null;
        }

    }

    // 解析品牌聚合结果
    private List<Brand> getBrandAggResult(Aggregation aggregation) {
        try {
            LongTerms brandAgg = (LongTerms) aggregation;
            List<Long> bids = new ArrayList<>();
            for (LongTerms.Bucket bucket : brandAgg.getBuckets()) {
                bids.add(bucket.getKeyAsNumber().longValue());
            }
            // 根据id查询品牌
            return this.brandClient.queryBrandByIds(bids);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    // 解析商品分类聚合结果
    private List<Category> getCategoryAggResult(Aggregation aggregation) {
        try{
            List<Category> categories = new ArrayList<>();
            LongTerms categoryAgg = (LongTerms) aggregation;
            List<Long> cids = new ArrayList<>();
            for (LongTerms.Bucket bucket : categoryAgg.getBuckets()) {
                cids.add(bucket.getKeyAsNumber().longValue());
            }
            // 根据id查询分类名称
            List<String> names = this.categoryClient.queryNameByIds(cids).getBody();

            for (int i = 0; i < names.size(); i++) {
                Category c = new Category();
                c.setId(cids.get(i));
                c.setName(names.get(i));
                categories.add(c);
            }
            return categories;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    // 构建基本查询条件
    private void searchWithPageAndSort(NativeSearchQueryBuilder queryBuilder, SearchRequest request) {
        // 准备分页参数
        int page = request.getPage();
        int size = request.getSize();

        // 1、分页
        queryBuilder.withPageable(PageRequest.of(page - 1, size));
        // 2、排序
        String sortBy = request.getSortBy();
        Boolean desc = request.getDescending();
        if (StringUtils.isNotBlank(sortBy)) {
            // 如果不为空，则进行排序
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(desc ? SortOrder.DESC : SortOrder.ASC));
        }
    }
    public void createIndex(Long id) throws IOException {

        Spu spu = this.goodsClient.querySpuById(id).getBody();
        // 构建商品
        Goods goods = this.buildGoods(spu);

        // 保存数据到索引库
        this.goodsRepository.save(goods);
    }

    public void deleteIndex(Long id) {
        this.goodsRepository.deleteById(id);
    }

}
