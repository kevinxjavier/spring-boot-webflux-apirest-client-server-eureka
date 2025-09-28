package com.kevinpina.models.documents;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Document(collection = "category")
public class Category {

    // @Id
    @NotEmpty // Used formvalidation in @RestController @jakarta.validation.Valid;
              // and also in ProductHandler Validator;
    private String id;

    @NotEmpty // Used formvalidation in @RestController @jakarta.validation.Valid;
              // and also in ProductHandler Validator;
    @NonNull // Used for Constructor required @RequiredArgsConstructor
    private String name;

}
