ALTER TABLE A ADD FOREIGN KEY(publisher_id) REFERENCES publisher(publisher_PK); 
ALTER TABLE publisher ADD FOREIGN KEY(publisher2_id) REFERENCES publisher2(publisher2_PK); 
ALTER TABLE author ADD FOREIGN KEY(A_id) REFERENCES A(A_PK); 
ALTER TABLE author ADD FOREIGN KEY(B_id) REFERENCES B(B_PK); 