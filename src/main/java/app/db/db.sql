-- customers personal info table
CREATE TABLE customers (
    customer_id NUMBER PRIMARY KEY,
    first_name VARCHAR2(50) NOT NULL,
    last_name VARCHAR2(50) NOT NULL,
    email VARCHAR2(100) NOT NULL UNIQUE,
    phone_number VARCHAR2(15) UNIQUE,
    address VARCHAR2(255),
    city VARCHAR2(50),
    state VARCHAR2(50),
    postal_code VARCHAR2(10),
    country VARCHAR2(50),
    date_of_birth DATE NOT NULL,
    gender CHAR(1) CHECK (gender IN ('M', 'F', 'O')) -- M - Male, F - Female, O - Others.
);

SELECT * FROM customers;


-- banks table
CREATE TABLE banks (
    bank_id NUMBER PRIMARY KEY,
    bank_name VARCHAR2(50) NOT NULL,
    branch_name VARCHAR2(50) NOT NULL
);

-- customers account info table
CREATE TABLE accounts (
    acc_no VARCHAR2(20) NOT NULL,
    acc_type VARCHAR2(20) NOT NULL CHECK (acc_type IN ('SAVINGS', 'CURRENT')), -- CURRENT / SAVINGS 
    customer_id NUMBER NOT NULL,
    ifsc_code VARCHAR2(11) NOT NULL,
    balance NUMBER(10, 3) NOT NULL,
    min_bal NUMBER(10, 2) NOT NULL,
    bank_id NUMBER NOT NULL,
    account_status VARCHAR2(20) NOT NULL CHECK (account_status IN ('Active', 'Closed', 'Suspended')),
    creation_date DATE NOT NULL,
    PRIMARY KEY (acc_no),
    CONSTRAINT fk_bank FOREIGN KEY (bank_id) REFERENCES banks(bank_id)
    CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

SELECT * FROM accounts;

-- table for UPI service
CREATE TABLE upi_service (
    acc_no VARCHAR2(20) NOT NULL PRIMARY KEY,
    upi_vpa VARCHAR2(20) NOT NULL,
    upi_limit NUMBER NOT NULL,
    upi_transaction_limit NUMBER(10, 3) NOT NULL,
    upi_pin NUMBER(4) NOT NULL,
    CONSTRAINT fk_account_upi FOREIGN KEY (acc_no) REFERENCES accounts(acc_no)
);
DROP TABLE upi_service;


-- login credentials table
CREATE TABLE login_credentials (
    acc_no VARCHAR2(20) NOT NULL PRIMARY KEY,
    username VARCHAR2(20),
    login_pwd VARCHAR2(20),
    transaction_pwd VARCHAR2(20),
    CONSTRAINT fk_account_credentials FOREIGN KEY (acc_no) REFERENCES accounts(acc_no)
);


-- cards table
CREATE TABLE cards (
    card_id NUMBER PRIMARY KEY,
    acc_no VARCHAR2(20) NOT NULL,
    card_type VARCHAR2(10) CHECK (card_type IN ('Credit', 'Debit')), -- 'Credit' or 'Debit'
    card_number VARCHAR2(16) UNIQUE,
    card_cvv NUMBER(3),
    card_expiry DATE,
    CONSTRAINT fk_account_card FOREIGN KEY (acc_no) REFERENCES accounts(acc_no)
);

-- transaction logs table
CREATE TABLE transactions (
    transaction_date DATE NOT NULL,
    transaction_time TIMESTAMP NOT NULL,
    from_account_number VARCHAR2(20) NOT NULL,
    transaction_status VARCHAR2(20) NOT NULL CHECK (transaction_status IN ('Failed', 'Success')),
    transaction_reference_number VARCHAR2(30) NOT NULL,
    transaction_type VARCHAR2(20) NOT NULL CHECK (transaction_type IN ('Credit', 'Debit')),
    transaction_amount NUMBER(19, 4) NOT NULL,
    to_account_number VARCHAR2(20) NOT NULL,
    transaction_mode VARCHAR2(20) NOT NULL CHECK (transaction_mode IN ('UPI', 'Net Banking', 'Cards')),
    PRIMARY KEY (transaction_reference_number)
);


-- test data credits: OPENAI GPT

INSERT INTO customers (customer_id, first_name, last_name, email, phone_number, address, city, state, postal_code, country, date_of_birth, gender)
VALUES 
(1, 'John', 'Doe', 'john.doe@example.com', '555-0101', '123 Elm Street', 'Springfield', 'Illinois', '62704', 'USA', TO_DATE('1980-02-15', 'YYYY-MM-DD'), 'M'),
(2, 'Jane', 'Smith', 'jane.smith@example.com', '555-0102', '456 Maple Avenue', 'Columbus', 'Ohio', '43085', 'USA', TO_DATE('1985-07-24', 'YYYY-MM-DD'), 'F'),
(3, 'Michael', 'Johnson', 'michael.johnson@example.com', '555-0103', '789 Oak Lane', 'Austin', 'Texas', '73301', 'USA', TO_DATE('1990-11-05', 'YYYY-MM-DD'), 'M'),

INSERT INTO banks (bank_id, bank_name, branch_name)
VALUES 
(1, 'Global Bank', 'Downtown'),
(2, 'City Bank', 'Northside'),
(3, 'Trust Bank', 'Southside');

INSERT INTO accounts (acc_no, acc_type, customer_id, ifsc_code, balance, min_bal, bank_id, account_status, creation_date)
VALUES 
('ACC00001', 'SAVINGS', 1, 'IFSC0001', 10000.00, 500.00, 1, 'Active', TO_DATE('2020-01-01', 'YYYY-MM-DD')),
('ACC00002', 'CURRENT', 2, 'IFSC0002', 15000.00, 1000.00, 2, 'Active', TO_DATE('2020-02-01', 'YYYY-MM-DD')),
('ACC00003', 'SAVINGS', 3, 'IFSC0003', 20000.00, 500.00, 3, 'Active', TO_DATE('2020-03-01', 'YYYY-MM-DD'));

INSERT INTO upi_service (acc_no, upi_vpa, upi_limit, upi_transaction_limit, upi_pin)
VALUES 
('ACC00001', 'john.doe@upi', 20, 10000, 1111),
('ACC00002', 'jane.smith@upi', 20, 15000,3333),
('ACC00003', 'michael.johnson@upi', 20, 20000, 4444);

INSERT INTO login_credentials (acc_no, username, login_pwd, transaction_pwd)
VALUES 
('ACC00001', 'john.doe', 'password123', 'txn123'),
('ACC00002', 'jane.smith', 'password123', 'txn123'),
('ACC00003', 'michael.johnson', 'password123', 'txn123');

INSERT INTO cards (card_id, acc_no, card_type, card_number, card_cvv, card_expiry)
VALUES 
(1, 'ACC00001', 'Credit', '4111111111111111', 123, TO_DATE('2025-12-31', 'YYYY-MM-DD')),
(2, 'ACC00002', 'Debit', '4222222222222222', 456, TO_DATE('2024-12-31', 'YYYY-MM-DD')),
(3, 'ACC00003', 'Credit', '4333333333333333', 789, TO_DATE('2023-12-31', 'YYYY-MM-DD'));

INSERT INTO transactions (transaction_date, transaction_time, from_account_number, transaction_status, transaction_reference_number, transaction_type, transaction_amount, to_account_number, transaction_mode)
VALUES 
(TO_DATE('2022-01-01', 'YYYY-MM-DD'), CURRENT_TIMESTAMP, 'ACC00001', 'Success', 'TXN00001', 'Credit', 100.00, 'ACC00002', 'UPI'),
(TO_DATE('2022-01-02', 'YYYY-MM-DD'), CURRENT_TIMESTAMP, 'ACC00002', 'Failed', 'TXN00002', 'Debit', 200.00, 'ACC00003', 'Net Banking'),
(TO_DATE('2022-01-03', 'YYYY-MM-DD'), CURRENT_TIMESTAMP, 'ACC00003', 'Success', 'TXN00003', 'Credit', 300.00, 'ACC00001', 'Cards');


SELECT * FROM customers;
SELECT * FROM accounts;
SELECT * FROM login_credentials;
SELECT * FROM UPI_SERVICE;

DELETE FROM customers
WHERE customer_id = 75229486;

DELETE FROM accounts
WHERE customer_id = 75229486;

DELETE FROM accounts
WHERE acc_no = (SELECT acc_no FROM accounts WHERE customer_id = 75229486);


