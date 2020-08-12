package com.hopeshop;

import com.hopeshop.client.CategoryClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;


/**
 * Created by silence on 2020/1/7.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class CategoryClientTest {

    @Autowired
    private CategoryClient categoryClient;

    @Test
    public void testQueryCategories() {
        ResponseEntity<List<String>> names = this.categoryClient.queryNameByIds(Arrays.asList(1L, 2L, 3L));
        System.out.println(names.toString());
    }
}
