CREATE DATABASE many2manyarraydata;
CREATE TABLE A_author ( A_id VARCHAR(120) ,author_id BIGINT  ); 
CREATE TABLE A ( pages INT ,_rev VARCHAR(120) ,language VARCHAR(34) ,A_PK VARCHAR(120) ,PRIMARY KEY(A_PK),title VARCHAR(120) ,published_date DATETIME  ); 
CREATE TABLE author ( author_value VARCHAR(34) ,author_PK BIGINT ,PRIMARY KEY(author_PK) ); 
