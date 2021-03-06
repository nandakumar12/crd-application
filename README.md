

# Simple key value data store with CRD operations
Build a file-based key-value data store that supports the basic CRD (create, read, and delete) operations. This data store is meant to be used as a local storage for one single process on one laptop. The data store must be exposed as a library to clients that can instantiate a class and work with the data store.

## Configuration
 Configuring the data store file path<br>
Edit the following file to set an custom file path

`/src/main/resources/application.yaml`<br>
Example: 
> file-path: C:/custom-folder/

## How to run this project
**Note :** Don't run this project with any IDE, either use Terminal or CMD
because, the data will be moved into the datastore while stopping the application by utilizing JVM hooks. Due to the limitation that most of IDE's wont invoke JVM hooks, the application may fail to persist the data. So, execute this application in terminal.

### Using docker
Pull the image from docker hub
> $ docker pull nandakumar12/crd-app:latest

or Use docker to build an image
> $ docker build -t crd-app .

> $ docker run -p 8000:8000 --name crd crd-app

### Using maven
Execute with maven directly
> $ ./mvnw spring-boot:run

or Package it as an jar file then execute

>$  ./mvnw package

>$  java -jar CRD-0.0.1-SNAPSHOT.jar
## How to run the Unit tests
**All the Unit test cases are present in**
> /src/test/java/com/nandakumar12/crd/app

### Using maven

    $ ./mvnw test

## REST Endpoints
### Import the postman collection  

**crd-application.postman_collection.json** file

or

Fire the below requests using any API Client
#### Create Data
> **POST** /api/crd/data?**key=123**

request body 
	

    {
    	"key1":"value",
    	"timeToLive":"50"
    }
### Read Data

> **GET** /api/crd/data?**key=123**

### Delete Data

>  **DELETE**  /api/crd/data?**key=123**

