package com.hopeshop.item.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="tb_specification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Specification {
    @Id
    public Long categoryId;
    private String specifications;
}
