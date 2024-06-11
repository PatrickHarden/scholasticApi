# lit-platform-ba-api
API collection for Batch Assignment

## Dependencies
* [Java 17](https://www.oracle.com/java/technologies/downloads/)
* [Maven 3.8+](https://maven.apache.org/)
* [MySQL 8](https://dev.mysql.com/downloads/mysql/)

## First time setup

### Cloning
```
git clone git@bitbucket.org:scholastic/lit-platform-ba-api.git
cd lit-platform-ba-api
git checkout develop
```

### Database setup

Login with `root` user in MySQL and run:

```
CREATE DATABASE ba CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER 'ba_dev'@'localhost' IDENTIFIED BY 'ba_dev';
GRANT ALL ON ba.* TO 'ba_dev'@'localhost';
FLUSH PRIVILEGES;
```

Confirm user has been created successfully by running:

```
mysql -u ba_dev -p
```

Enter password `ba_dev` when prompted.

### Building/Installing
#### Mac
```
cd lit-platform-ba-api
./mvnw clean install
```
#### Windows
```
cd lit-platform-ba-api
mvnw clean install
```

## Running on your local
1. Command line
```
java -jar target/lit-platform-ba-api-1.0.0.jar
```
2. IDE (e.g. [IntelliJ IDEA](https://www.jetbrains.com/idea/download/#section=mac))
```
1. File > open
2. Browse for the lit-platform-ba-api directory, select pom.xml file and select 'Open as project'
3. Right click on LitPlatformBaApiApplication.java file in Project Explorer and select `LitPlatformBaApiApplication.main()`
```

## Running Tests + Coverage
#### Mac
```
cd lit-platform-ba-api
./mvnw clean install
```
#### Windows
```
cd lit-platform-ba-api
mvnw clean install
```
Coverage results can be viewed by opening `target/site/jacoco/index.html` in a browser.

## Linting
If you are using an IDE(e.g. [IntelliJ IDEA](https://www.jetbrains.com/idea/download/#section=mac)), install the respective [SonarLint plugin](https://www.sonarlint.org/).
```
Right click on `lit-platform-ba-api`, select `SonarLint` and select `Analyze with SonarLint`
```