ALTER TABLE A_author ADD FOREIGN KEY(A_id) REFERENCES A(A_PK); 
ALTER TABLE A_author ADD FOREIGN KEY(author_id) REFERENCES author(author_PK); 
