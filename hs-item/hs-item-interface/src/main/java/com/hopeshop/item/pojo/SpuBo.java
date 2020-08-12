package com.hopeshop.item.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import java.util.List;

/**
 * Created by silence on 2019/12/30.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpuBo extends Spu {

    @Transient
    String cname;// 商品分类名称
    @Transient
    String bname;// 品牌名称
    @Transient
    SpuDetail spuDetail;// 商品详情
    @Transient
    List<Sku> skus;// sku列表
}
