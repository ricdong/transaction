# Transaction

This is a simple project to demonstrate Account Transaction

## Requirements

*   Mac OS X or Linux
*   Java 7 or higher, 64-bit
*   Maven 3.3+ (for building)

## Building Transaction

Transaction is a standard Maven project, just simply run the following command from the project root directory:

    mvn clean install

on the first build, Maven will download all the dependencies from the internet or your local repository, which will take some time.

## Deploying Transaction on a Linux Server

To deploy Transaction on a server, do the following:

*   Configuring the Mysql properties. Transaction use Mysql database as the data store, first you should create the coins table:

```
USE test;

CREATE TABLE `coins` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_name` varchar(20) DEFAULT NULL,
  `coins` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

```

*   Configuring the Mysql as the data source in Transaction, create a property file named mysql.properties in root directory with the command:

```sh  
    touch conf/mysql.properties
``` 

and copy the content into it, note that replace the host and database name with your own: 

```sh

connector.name=mysql
connection-url=jdbc:mysql://localhost:3306/test
connection-user=temp
connection-password=temp@1234
connection-table=coins

```

*   Set the Java Home to Transaction, just configuring the file conf/service-env.sh and set the __JAVA_HOME__ 

```sh  
# The java implementation to use. Required. 
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home
```

*   Start the Transaction, simply type the command in root directory with 

```sh  
    bin/start-service.sh or bin/service-daemon.sh start 
```  
and then open the address http://localhost:8080, you will see the hello world. 

*   Shutdown the Transaction

```sh
    bin/stop-service.sh or bin/service-daemon.sh start 
```

## APIs 

Transaction provides the following features:  
- Add or update the user with coins 
- Transfer the coins from an user to another user 
- Dump the threads information of current process

### Add or update the user with coins

This is post method that allow you to add or update(if exists) the user with the coins

Post body with the header `Content-Type:application/json` on `http://localhost:8080/user/add`
```json  
{"transaction": {
    "useradd": [
      {"user_id":"lyn zhang", "coins":"12"},
      {"user_id":"lynn dong", "coins":"12"} ]
}}
```
And the response will like this:

```json  
{  
   "user/add":[  
      {  
         "lynn dong":"OK",
         "lyn zhang":"OK"
      }
   ]
}
```

### Transfer the coins from an user to another user 

Post body with the header `Content-Type:text/plain` on `http://localhost:8080/transaction/transfer`
```json  
from_user_name=ricdong&to_user_name=liulishuo&coins=15 
```
If the transaction successfully you will get the response with `OK`, otherwise you will get the following response:

1. User not found 
2. Coin insufficient 

### Dump the threads information of current process 

`http://localhost:8080/jstack` will dump all threads status of transaction process. 

