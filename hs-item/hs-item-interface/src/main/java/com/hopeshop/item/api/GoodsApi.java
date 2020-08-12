package com.hopeshop.item.api;

import com.hopeshop.common.pojo.PageResult;
import com.hopeshop.item.pojo.Sku;
import com.hopeshop.item.pojo.Spu;
import com.hopeshop.item.pojo.SpuBo;
import com.hopeshop.item.pojo.SpuDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Created by silence on 2020/1/7.
 */
public interface GoodsApi {

    /**
     * 分页查询SPU
     * @param page
     * @param rows
     * @param
     * @return
     */
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<SpuBo>> querySpuByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "desc", required = false) Boolean desc);

    @GetMapping("/spu/detail/{id}")
    ResponseEntity<SpuDetail> querySpuDetailById(@PathVariable("id") Long id);

    @GetMapping("/sku/list")
    ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id") Long id);

    /**
     * 根据spu的id查询spu
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id);

}
