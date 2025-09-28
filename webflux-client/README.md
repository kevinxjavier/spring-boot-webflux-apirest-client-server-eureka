# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [ReactiveX](https://reactivex.io)
* [Project Reactor](https://projectreactor.io)

### Create Maven SpringBoot Reactor Project
```
	$ curl https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.5.6&packaging=jar&jvmVersion=17&groupId=com.kevinpina&artifactId=webflux-client&name=webflux-client&description=Demo%20project%20for%20Spring%20Boot&packageName=com.kevinpina.client&dependencies=devtools,webflux
```

### Run Compile
```
    $ mvn clean compile package
```

### Start
```
    $ mvn spring-boot:run
```

### Run
```
    [GET] 
        $ curl --location 'http://localhost:8081/api/client'
        
        $ curl --location 'http://localhost:8081/api/client/68d7e56294e4d7069759d4f2'
        
    [POST]
        $ curl --location 'http://localhost:8081/api/client' \
            --header 'Content-Type: application/json' \
            --data '{
                "name": "Maxton 600X",
                "price": 129.0,
                "category": {
                    "id": "68d7e56294e4d7069759d4ed",
                    "name": "Informatic"
                }
            }'
            
        $ curl --location 'http://localhost:8081/api/client/upload/68d7e56294e4d7069759d4f2' \
            --form 'file=@"/C:/Users/aufwa/Downloads/raspberry.jpg"'

    [PUT]
        $ curl --location --request PUT 'http://localhost:8081/api/client/68d7ed1f94e4d7069759d4f9' \
            --header 'Content-Type: application/json' \
            --data '{
                "name": "Maxton 5000",
                "price": 129.0,
                "category": {
                    "id": "68d31720898f8b8948de1d97",
                    "name": "Informatic"
                }
            }'
    
    [DELETE]
        $ curl --location --request DELETE 'http://localhost:8081/api/client/68d7ed1f94e4d7069759d4f9'
```