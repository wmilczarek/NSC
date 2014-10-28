CREATE DATABASE arrarefid;
CREATE TABLE bigEntity ( _rev VARCHAR(120) ,nameField VARCHAR(34) ,bigEntity_PK VARCHAR(34) ,PRIMARY KEY(bigEntity_PK) ); 
CREATE TABLE smallEntity ( _rev VARCHAR(120) ,nameField VARCHAR(34) ,smallEntity_PK VARCHAR(34) ,PRIMARY KEY(smallEntity_PK) bigEntity_id VARCHAR(34)  ); 
