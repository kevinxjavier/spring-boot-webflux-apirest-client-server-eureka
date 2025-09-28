# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [ReactiveX](https://reactivex.io)
* [Project Reactor](https://projectreactor.io)

### Create Maven SpringBoot Reactor Project
```
	$ curl https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.5.6&packaging=jar&jvmVersion=17&groupId=com.kevinpina&artifactId=eureka-service&name=eureka-service&description=Demo%20project%20for%20Spring%20Boot%20-%20Eureka%20Server%2C%20Service%20Discovery&packageName=com.kevinpina.eureka&dependencies=devtools,cloud-eureka-server,webflux
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
    $ curl http://localhost:8761 
```
