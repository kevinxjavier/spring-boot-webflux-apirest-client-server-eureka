package com.kevinpina.client.models;

import lombok.Data;

import java.util.Date;

@Data
public class Product {

    private String host;
    private String id;
    private String name;
    private Float price;
    private Date createAt;
    private String picture;
    private Category category;

}
