DROP TABLE IF EXISTS transfer;
DROP TABLE IF EXISTS tenmo_user;
DROP TABLE IF EXISTS account;

CREATE SEQUENCE seq_transfer_id
  INCREMENT BY 1
  START WITH 3001
  NO MAXVALUE;

CREATE TABLE transfer (
	transfer_id int NOT NULL DEFAULT nextval('seq_transfer_id'),
	sender int NOT NULL,
	recipient int NOT NULL,
	amount decimal NOT NULL,
	transfer_type varchar(50) NOT NULL,
	request_approved boolean DEFAULT NULL,
	request_status varchar(50) DEFAULT 'Pending',
	CONSTRAINT PK_transfer PRIMARY KEY (transfer_id),
	CONSTRAINT FK_transfer_sender FOREIGN KEY (sender) REFERENCES account (account_id),
	CONSTRAINT FK_transfer_recipient FOREIGN KEY (recipient) REFERENCES account (account_id),
	CHECK (amount > 0)
);


SELECT * FROM tenmo_user;
SELECT * FROM account;
SELECT * FROM transfer;

INSERT INTO tenmo_user (username, password_hash)
VALUES 
('Derik Nguyen', '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC'),
('Josh Baker', '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC'),
('Donnie Darko', 'timetravel'),
('Test', 'testing');

INSERT INTO account (user_id, balance)
VAlUES
((SELECT user_id
 FROM tenmo_user
 WHERE username = 'Derik Nguyen'), 1000);
 
INSERT INTO account (user_id, balance)
VAlUES
((SELECT user_id
 FROM tenmo_user
 WHERE username = 'Josh Baker'), 1000);
 
 INSERT INTO transfer (sender, recipient, amount, transfer_type)
 VALUES
 ( (SELECT account_id
    FROM account as a
    JOIN tenmo_user as tu
    ON tu.user_id = a.user_id
    WHERE tu.username = 'Derik Nguyen'), 
   (SELECT account_id
    FROM account as a
    JOIN tenmo_user as tu
    ON tu.user_id = a.user_id
    WHERE tu.username = 'Josh Baker'),
  	200,
    'Transfer'
 ) RETURNING transfer_id; 
 
 UPDATE account
 SET balance = 10000;
 
 UPDATE account 
 SET balance = balance + 200
 WHERE username = 'Josh Baker';
 
 
 
 
 
 



