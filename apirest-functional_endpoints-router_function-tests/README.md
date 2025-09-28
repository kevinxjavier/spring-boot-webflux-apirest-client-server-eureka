# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [ReactiveX](https://reactivex.io)
* [Project Reactor](https://projectreactor.io)

### Create Maven SpringBoot Reactor Project
```
	$ curl https://start.spring.io/starter.zip?name=spring-boot-webflux-apirest-functional_endpoints-router_function-tests&groupId=com.kevinpina&artifactId=spring-boot-webflux-apirest-functional_endpoints-router_function-testst&version=0.0.1-SNAPSHOT&description=Webflux+demo+project+for+Spring+Boot&packageName=com.kevinpina&type=maven-project&packaging=jar&javaVersion=17&language=java&bootVersion=3.5.5&dependencies=devtools&dependencies=data-mongodb-reactive&dependencies=webflux
```

### Install MongoDB
* [MongoDB Community Edition](https://www.mongodb.com/try/download/community)
* [Robo 3T](https://robomongo.org)

```
	# Install Tools
		$ sudo apt install gnupg curl
		$ curl -fsSL https://pgp.mongodb.com/server-7.0.asc |sudo gpg  --dearmor -o /etc/apt/trusted.gpg.d/mongodb-server-7.0.gpg
	
	# Add Repo MongoDB
		$ echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu jammy/mongodb-org/7.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-7.0.list
		$ sudo apt update
	
	# Install MongoDB
		$ sudo apt install -y mongodb-org
	
	# Start MongoDB
		$ sudo systemctl start mongod
	
	# Verify MongoDB
		$ sudo systemctl status mongod
	
	# Start MongoDB on system startup (optional)
		$ sudo systemctl enable mongod
	
	# Run MongoDB
		$ mongosh
		$ mongosh --host 127.0.0.1 --port 27017
	
	# Enable MongoDB Remotely
		$ sudo vi /etc/mongod.conf
			# Current
				net:
				  port: 27017
				  bindIp: 127.0.0.1
		  	# Change for
		  		net:
				  port: 27017
				  bindIp: 0.0.0.0
				  
	# Enabled MongoDB User/Password
		## Create User admin:
			$ mongosh
				> use admin
				> db.createUser({
				  user: "admin",
					pwd: "MyPassword",
					roles: [ { role: "userAdminAnyDatabase", db: "admin" }, "readWriteAnyDatabase" ]
				})
	
		## Create UserApp "kevin"
			$ mongosh
				> use enterprise
				> db.createUser({
				  user: "kevin",
				  pwd: "MyPassword",
				  roles: [ { role: "readWrite", db: "enterprise" } ]
				})

		## Show Users
			> use admin
			> db.system.users.find();

		## Activar autenticacion 
			$ vi /etc/mongod.conf
				security:
					authorization: enabled

		## Reinicia MongoDB
			$ sudo systemctl restart mongod
			
		## Login as admin
			$ mongosh -u "admin" -p "MyPassword" --authenticationDatabase "admin"

		## Loin as kevin
			$ mongosh -u kevin -p --authenticationDatabase enterprise

		## Add Access to Other Database  
			# Log as admin
			$ mongosh -u "admin" -p "MyPassword" --authenticationDatabase "admin"

				> use newDataBase
				
				> db.grantRolesToUser("myUser1", [
					{ role: "readWrite", db: "newDataBase" }
				])

				> db.grantRolesToUser("myUser2", [
					{ role: "readWrite", db: "newDataBase" },
					{ role: "dbAdmin", db: "otherDataBase" }
				])

				> db.getUsers();
				
				> db.getUser("myUser1");    # Log as myUser1 (or as admin then > use newDataBase, then execute this command will show the permissions.

				-- Roles: 
					* readWrite: Allows reading and writing to the database.
					* read: Allows only reading data from the database.
					* dbAdmin: Allows managing the database (e.g., creating indexes).
					* userAdmin: Allows managing users and roles within a database.

				-- Global Roles: 
					* readWriteAnyDatabase
					* dbAdminAnyDatabase
					* userAdminAnyDatabase
```

### Examples MongoDB
```
	# Show Current Database
		$ db
		
    # Show Database
		$ show databases
		$ show dbs
		
	# Change/Create db
		$ use mydb	# This switches to mydb, and automatically creates it when you insert data. 
						# Now insert something to actually create the DB.

	# Show Collections "like Tables"
		$ show collections
	
	# Insert/Create Collection (if no exists will create users)
		$ db.users.insertMany([{"id":1, "name": "kevin", "surname": "pina"}, {"id":2, "name": "javier", "surname": "calatrava"}]);
		
    # Rename Collection
        $ db.getCollection("users").renameCollection("product"); 
        
    # Drop Collection
        $ db.getCollection("product").drop();

	# Select
		$ db.users.find({})
		$ db.users.find({"name": "kevin"})
		$ db.users.find().limit(1)
		$ db.getSiblingDB("mydb").getCollection("users")
              .find({})
              .limit(21)
```

### Run Compile
```
    $ mvn clean compile package -DskipTests
```

### Run Tests
```
    $ mvn test
```

### Start
```
    $ mvn spring-boot:run
```

### Run
```
    [GET] 
        $ curl --location 'http://localhost:8080/api/product'       # /v1

        $ curl --location 'http://localhost:8080/api/product/v2'

        $ curl --location 'http://localhost:8080/api/product/v3'
        
        $ curl --location 'http://localhost:8080/api/product/68bf15192a842b0e6dc2fcfd'

    [POST]
        $ curl --location 'http://localhost:8080/api/product' \     # /v1
            --header 'Content-Type: application/json' \
            --data '{
                "id": "68bf15192a842b0e6dc2fcfd",
                "name": "Kevin Sweet Software",
                "price": 999.0,
                "createAt": "2025-09-08T17:40:41.930+00:00",
                "category": {
                    "id": "68bf15192a842b0e6dc2fcf8",
                    "name": "Informatic"
                },
                "picture": null
            }'

        $ curl --location 'http://localhost:8080/api/product/v2' \
            --form 'file=@"/C:/Users/kevin/Downloads/raspberry.jpg"' \
            --form 'name="Kevin"' \
            --form 'price="36"' \
            --form 'category.id="68bf1f2c055daf9ebd70100c"' \
            --form 'category.name="Informatic"'

        $ curl --location 'http://localhost:8080/api/product/upload/68bf1b974e5da75551c673ca' \
            --form 'file=@"/C:/Users/kevin/Downloads/raspberry.jpg"'

    [PUT]
        $ curl --location --request PUT 'http://localhost:8080/api/product/68bf15192a842b0e6dc2fcfd' \
            --header 'Content-Type: application/json' \
            --data '{
                    "id": "68bf15192a842b0e6dc2fcfd",
                    "name": "Kevin Sweet Software",
                    "price": 999.0,
                    "createAt": "2025-09-08T17:40:41.930+00:00",
                    "category": {
                        "id": "68bf15192a842b0e6dc2fcf8",
                        "name": "Informatic"
                    },
                    "picture": null
                }'
                
    [DELETE]
        $ curl --location --request DELETE 'http://localhost:8080/api/product/68bf15192a842b0e6dc2fcfd'
    
    #############################################    
    ## FUNCTIONAL ENDPOINTS & ROUTER FUNCTION
    #############################################
    
    [GET]
        $ curl -s http://localhost:8080/api/v2/product/v1
        $ curl -s http://localhost:8080/api/test/product/v5
        
        $ curl http://localhost:8080/api/v3/product
        $ curl http://localhost:8080/api/v3/products
        
        $ curl http://localhost:8080/api/v4/product
        $ curl http://localhost:8080/api/v4/products
        $ curl http://localhost:8080/api/v4/product/68c47e301d6675680cfde918
        $ curl http://localhost:8080/api/v4/product/get/68c47e301d6675680cfde918

    [POST]
        $ curl --location 'http://localhost:8080/api/v4/product' \
            --header 'Content-Type: application/json' \
            --data '{
                "name": "Kevin Sweet Software",
                "price": 999.0,
                "category": {
                    "id": "68c48700d742a098fb847fe5",
                    "name": "Informatic"
                }
            }'

        $ curl --location 'http://localhost:8080/api/v4/product/create_and_upload' \
            --form 'fileName=@"/C:/Users/aufwa/Downloads/raspberry.jpg"' \
            --form 'name="kevin"' \
            --form 'price="155000.99"' \
            --form 'category.id="68c4a4a7bc5578d874ced6f5"' \
            --form 'category.name="Informatic"'

        $ curl --location 'http://localhost:8080/api/v4/product/upload/68c4a5ee2eabb6c78b5df81f' \
            --form 'fileName=@"/C:/Users/aufwa/Downloads/raspberry.jpg"'

    [PUT]
        $ curl --location --request PUT 'http://localhost:8080/api/v4/product/68c4900e9b0f85096ddb2496' \
            --header 'Content-Type: application/json' \
            --data '{
                    "name": "Baldur",
                    "price": 69.0,
                    "createAt": "2025-09-08T17:40:41.930+00:00",
                    "category": {
                        "id": "68c4900e9b0f85096ddb2492",
                        "name": "Informatic"
                    },
                    "picture": null
                }'

    [DELETE]
        $ curl --location --request DELETE 'http://localhost:8080/api/v4/product/68bf15192a842b0e6dc2fcfd'
```
