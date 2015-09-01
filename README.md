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

1. Configuring the Mysql properties. Transaction use Mysql database as the data store, first you should create the coins table:

```
USE test;

CREATE TABLE `coins` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_name` varchar(20) DEFAULT NULL,
  `coins` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

```

2. Configuring the Mysql as the data source in Transaction

create a property file named mysql.properties in root directory with the command:

    touch conf/mysql.properties

and copy the content into it:

```sh

connector.name=mysql
connection-url=jdbc:mysql://localhost:3306/test
connection-user=temp
connection-password=temp@1234
connection-table=coins

```
