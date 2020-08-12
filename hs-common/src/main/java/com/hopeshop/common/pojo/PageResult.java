package com.hopeshop.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * Created by silence on 2019/12/22.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PageResult<T> {
    private Long total;// 总条数
    private Long totalPage;// 总页数
    private List<T> items;// 当前页数据

    public PageResult(Long total, List<T> items) {
        this.total = total;
        this.items = items;
    }

}
