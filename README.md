# EUREKA SERVER (first start this project)
	eureka-service

	# Start single instance
		# SpringBoot
			$ mvn spring-boot:run

		# Java
			$ java -jar eureka-service-0.0.1-SNAPSHOT.jar

# SERVER
	spring-boot-webflux-apirest-functional_endpoints-router_function-tests

	# Start single instance
		# SpringBoot
			$ mvn spring-boot:run		
			
			# NOTE: If start with this will get the $ curl --location 'http://localhost:8081/api/client/68d88280bf0b783cdd0f5c74'
			# 	{ "host": "x.x.x.x:null"}
			# better use $ java -jar webflux-apirest.jar --server.port=xxxx

	# Start many instances
		# Java
			$ java -jar webflux-apirest.jar --server.port=6000
			$ java -jar webflux-apirest.jar --server.port=6001
			$ java -jar webflux-apirest.jar --server.port=6002

# CLIENT
	webflux-client

	# Start single instances
		# SpringBoot
			$ mvn spring-boot:run

		# Java
			$ java -jar webflux-client-0.0.1-SNAPSHOT.jar

# INFO 
	Use the Postman Collection "FunctionalEndpoints & RouterFunction - Client.postman_collection.json" the project
	webflux-client in src\main\resources\application.properties is configured to use the host of Eureka Server projects:
```
	$ cat src\main\resources\application.properties
		# Config using Eureka Server
		config.base.endpoint=http://spring-boot-webflux-apirest/api/v4/product

	$ curl --location 'http://localhost:8081/api/client' 	
		# from behind the Eureka Server point this http://spring-boot-webflux-apirest to http://localhost:8080
```
