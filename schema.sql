CREATE DATABASE 'Testowa';CREATE TABLE A ( A_PK VARCHAR(24) ,PRIMARY KEY(A_PK), title VARCHAR(120) ,author VARCHAR(24) ,published_date DATETIME ,pages BIGINT ,language VARCHAR(24) ,publisher_id BIGINT  ); 
CREATE TABLE publisher ( publisher2_id BIGINT ,publisher_PK BIGINT ,PRIMARY KEY(publisher_PK), name VARCHAR(24) ,founded INT ,location VARCHAR(24)  ); 
CREATE TABLE publisher2 ( publisher2_PK BIGINT ,PRIMARY KEY(publisher2_PK), name VARCHAR(24) ,founded INT ,location VARCHAR(24)  ); 
CREATE TABLE B ( B_PK INT ,PRIMARY KEY(B_PK), title VARCHAR(120) ,author VARCHAR(24) ,published_date DATETIME ,pages INT ,language VARCHAR(24)  ); 
CREATE TABLE POLA ( POLA_PK VARCHAR(24) ,PRIMARY KEY(POLA_PK), test INT  ); 
CREATE TABLE test ( test_PK VARCHAR(24) ,PRIMARY KEY(test_PK), content BLOB  ); 
CREATE TABLE author ( A_id VARCHAR(24) ,B_id INT ,authorvalue VARCHAR(24)  ); 
