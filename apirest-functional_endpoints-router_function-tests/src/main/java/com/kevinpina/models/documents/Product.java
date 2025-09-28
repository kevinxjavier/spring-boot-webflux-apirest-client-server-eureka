package com.kevinpina.models.documents;

import java.util.Date;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Document(collection = "product", collation = "en") // SQL @Entity, NoSQL @Document
public class Product {

    @Id
    private String id;

    @NotEmpty // Used formvalidation in @RestController @jakarta.validation.Valid;
    @NonNull // Used for Constructor required @RequiredArgsConstructor
    private String name;

    @NotNull // Used formvalidation in @RestController @jakarta.validation.Valid;
    @NonNull // Used for Constructor required @RequiredArgsConstructor
    private Float price;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createAt;

    @Valid // Used formvalidation in @RestController @jakarta.validation.Valid; and also in ProductHandler Validator;
           // in combination with -> class Category { @NotEmpty id and  @NotEmpty name; }
    @NonNull // Used for Constructor required @RequiredArgsConstructor
    private Category category;
    
    private String picture;

    private String host;

}
