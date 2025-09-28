package com.kevinpina;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.kevinpina.models.dao.CategoryDao;
import com.kevinpina.models.dao.ProductDao;
import com.kevinpina.models.documents.Category;
import com.kevinpina.models.documents.Product;
import com.kevinpina.services.ProductService;

import reactor.core.publisher.Flux;

// @EnabledEurekaClient // No need it, Auto Enabled with the dependency spring-cloud-starter-netflix-eureka-client
@SpringBootApplication
public class SpringBootWebfluxApirestApplication implements CommandLineRunner {
	
	private static final Logger log = LoggerFactory.getLogger(SpringBootWebfluxApirestApplication.class);

	@Autowired
	private ProductDao productDao;

	@Autowired
	private CategoryDao categoryDao;

	@Autowired
	private ProductService productService;

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluxApirestApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Dropping collection product
				reactiveMongoTemplate.dropCollection("product")
					.subscribe();

				// Dropping collection category
				reactiveMongoTemplate.dropCollection("category")
						.subscribe();

				Category category1 = new Category("Informatic");
				Category category2 = new Category("Sport");
				Category category3 = new Category("Cleaning");
				Category category4 = new Category("Cloth");

				/* WAY 1

				// Inserting data category
				Flux.just(category1, category2, category3, category4)
						//.flatMap(category -> categoryDao.save(category))
						.flatMap(categoryDao::save)
						.doOnNext(category -> {
							log.info("category saved: " + category);
						})
						//.subscribe();
						.subscribe(category -> log.info("category saved: " + category));

				// Inserting data product
				Flux.just(new Product("Raspberry Pi 3", 84.99f, category1),
						new Product("Asus Thinker", 129f, category1),
						new Product("Baseball bat", 112.99f, category2),
						new Product("ABC", 3.99f, category3),
						new Product("Listering", 4.99f, category3),
						new Product("Nipogi 16 Ram, Ryzen 7", 385.99f, category1),
						new Product("White shirt for men", 4.99f, category4),
						new Product("HP Tower 64 GB, i7", 270.99f, category1))
					// .flatMap(product -> productDao.save(product)) // .flatMap(productDao::save)
					.flatMap(product -> {
							product.setCreateAt(new Date());
							return productDao.save(product);
						})
					.subscribe(product ->  log.info("product saved: " + product));
				*/

				/* WAY 2 */
				// Inserting data category
				Flux.just(category1, category2, category3, category4)
						//.flatMap(category -> productService::saveCategory(category))
						.flatMap(productService::saveCategory)
						.doOnNext(category -> {
							log.info("category saved: " + category);
						})
						//.then(...) // .then() for Mono<> and .thenMany() for Flux<>
						.thenMany(
							// Inserting data product
							Flux.just(new Product("Raspberry Pi 3", 84.99f, category1),
								new Product("Asus Thinker", 129f, category1),
								new Product("Baseball bat", 112.99f, category2),
								new Product("ABC", 3.99f, category3),
								new Product("Listering", 4.99f, category3),
								new Product("Nipogi 16 Ram, Ryzen 7", 385.99f, category1),
								new Product("White shirt for men", 4.99f, category4),
								new Product("HP Tower 64 GB, i7", 270.99f, category1))
							// .flatMap(product -> productService.save(product)) // .flatMap(productDao::save)
							.flatMap(product -> {
								product.setCreateAt(new Date());
								return productService.save(product);
							})
						).subscribe(product ->  log.info("product saved: " + product));

	}
	
}
