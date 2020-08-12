package com.hopeshop.item.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

public interface SpecificationApi {
    @GetMapping("/spec/{id}")
    public ResponseEntity<String> querySpecificationByCategoryId(@PathVariable("id") Long id);
}
