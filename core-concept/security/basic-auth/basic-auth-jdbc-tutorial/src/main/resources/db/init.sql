CREATE TABLE tavern_user (
  id INT PRIMARY KEY,
  username VARCHAR(255),
  password VARCHAR(255),
  role VARCHAR(255)
);

INSERT INTO tavern_user (id, username, password, role) VALUES (1, 'keeper', 'keeper123', 'keeper');
INSERT INTO tavern_user (id, username, password, role) VALUES (2, 'supplier', 'supplier123', 'supplier');
