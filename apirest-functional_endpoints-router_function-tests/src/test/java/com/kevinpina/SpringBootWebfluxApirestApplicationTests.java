package com.kevinpina;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kevinpina.models.documents.Category;
import com.kevinpina.models.documents.Product;
import com.kevinpina.services.ProductService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // Does not load the context of a real class SpringBootWebfluxApirestApplication
																	 // in combination with this annotation @AutoConfigureWebTestClient will load an auto context configuration and will not start a server
																	 // Faster!

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // Load the context for the real class SpringBootWebfluxApirestApplication and Start the server in a Random Port
class SpringBootWebfluxApirestApplicationTests {

	@Autowired
	private WebTestClient webTestClient;

	@Autowired
	private ProductService productService;

	@Value("${config.base.endpoint}")
	private String url;

	@Test
	void listTest() {
		webTestClient.get().uri(url)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBodyList(Product.class)
				//.hasSize(8); // more or less elements will fail
				.consumeWith(response -> {
					List<Product> products = response.getResponseBody();
					products.forEach(p -> System.out.println(p.getName())); // We can validate each element of the list

					Assertions.assertThat(products.size() > 0).isTrue();
				});
	}

	@Test
	void get1Test() {
		// Mono<Product> productMono = productService.findByName("ABC"); // findByName() and getByName() are methods created for Tests
		Product productMono = productService.findByName("ABC").block(); // Used .block() for Unit Test is necessary that be Synchronous not Asynchronous like subscriber()

		webTestClient.get().uri(url + "/{id}", Collections.singletonMap("id", productMono.getId()))
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.name").isEqualTo("ABC")
				.jsonPath("$.id").isNotEmpty();
	}

	@Test
	void get2Test() {
		Product productMono = productService.findByName("ABC").block(); // Used .block() for Unit Test is necessary that be Synchronous not Asynchronous like subscriber()

		webTestClient.get().uri(url + "/{id}", Collections.singletonMap("id", productMono.getId()))
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody(Product.class)
				.consumeWith(response -> {
					Product product = response.getResponseBody();

					Assertions.assertThat(product.getName()).isEqualTo("ABC");
					Assertions.assertThat(product.getId()).isNotEmpty();
					Assertions.assertThat(product.getId().length() > 0).isTrue();
				});
	}

	@Test
	public void create1Test() {
		Product productMono = productService.findByName("ABC").block(); // Used .block() for Unit Test is necessary that be Synchronous not Asynchronous like subscriber()

		Product product = Product.builder().name("Gulf ball").price(27.99f).category(productMono.getCategory()).build();

		webTestClient.post().uri(url)
				.contentType(MediaType.APPLICATION_JSON) // for Request
				.accept(MediaType.APPLICATION_JSON) // for Response
				.body(Mono.just(product), Product.class)
				.exchange()
				.expectStatus().isCreated()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()

				// RouterFunctionConfig.java
				//.jsonPath("$.id").isNotEmpty()
				//.jsonPath("$.name").isEqualTo("Gulf ball")
				//.jsonPath("$.category.name").isEqualTo(productMono.getCategory().getName());

				// ProductController.java
				.jsonPath("$.product.id").isNotEmpty()
				.jsonPath("$.product.name").isEqualTo("Gulf ball")
				.jsonPath("$.product.category.name").isEqualTo(productMono.getCategory().getName());
	}

	@Test
	public void create2Test() {
		Product productMono = productService.findByName("ABC").block(); // Used .block() for Unit Test is necessary that be Synchronous not Asynchronous like subscriber()

		Product product = Product.builder().name("Gulf ball").price(27.99f).category(productMono.getCategory()).build();

		webTestClient.post().uri(url)
				.contentType(MediaType.APPLICATION_JSON) // for Request
				.accept(MediaType.APPLICATION_JSON) // for Response
				.body(Mono.just(product), Product.class)
				.exchange()
				.expectStatus().isCreated()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)

				// RouterFunctionConfig.java
				/*.expectBody(Product.class)
				.consumeWith(response -> {
					Product p = response.getResponseBody();

					Assertions.assertThat(p.getId()).isNotEmpty();
					Assertions.assertThat(p.getName()).isEqualTo("Gulf ball");
					Assertions.assertThat(p.getCategory().getName()).isEqualTo(productMono.getCategory().getName());
				});*/

				// ProductController.java
				.expectBody(new ParameterizedTypeReference<LinkedHashMap<String, Object>>() {})

				.consumeWith(response -> {
					Object o = response.getResponseBody().get("product");
					Product p = new ObjectMapper().convertValue(o, Product.class);

					Assertions.assertThat(p.getId()).isNotEmpty();
					Assertions.assertThat(p.getName()).isEqualTo("Gulf ball");
					Assertions.assertThat(p.getCategory().getName()).isEqualTo(productMono.getCategory().getName());
				});
	}

	@Test
	public void updateTest() {
		Product productMono = productService.findByName("Listering").block(); // Used .block() for Unit Test is necessary that be Synchronous not Asynchronous like subscriber()
		Category categoryMono = productService.findCategoryByName("Cleaning").block(); // Used .block() for Unit Test is necessary that be Synchronous not Asynchronous like subscriber()

		Product productEdited = Product.builder().name("Ariel").price(5.99F).category(categoryMono).build();

		webTestClient.put().uri(url + "/{id}", Collections.singletonMap("id", productMono.getId()))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(productEdited), Product.class)
				.exchange()
				.expectStatus().isCreated()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.id").isNotEmpty()
				.jsonPath("$.name").isEqualTo("Ariel")
				.jsonPath("$.category.name").isEqualTo("Cleaning");
	}

	@Test
	public void deleteTest() {
		Product productMono = productService.findByName("Asus Thinker").block();

		webTestClient.delete().uri(url + "/{id}", Collections.singletonMap("id", productMono.getId()))
				.exchange()
				.expectStatus().isNoContent()
				.expectBody().isEmpty();

		// Optional test
		webTestClient.get().uri(url + "/{id}", Collections.singletonMap("id", productMono.getId()))
				.exchange()
				.expectStatus().isNotFound()
				.expectBody().isEmpty();
	}

}
