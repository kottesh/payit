-- TODO: add a services table holding data related to UPI ID, Netbanking -> Username, Passwds, etc.., 

-- customers personal info table
CREATE TABLE customers (
    customer_id NUMBER PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
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

-- customers account info table
CREATE TABLE accounts (
    acc_no VARCHAR2(20) NOT NULL,
    ifsc_code VARCHAR2(11) NOT NULL,
    min_bal NUMBER(10, 2) NOT NULL,
    bank_name VARCHAR2(50) NOT NULL,
    branch_name VARCHAR2(50) NOT NULL,
    customer_id NUMBER NOT NULL,
    debit_card_no VARCHAR2(16) UNIQUE,
    cvv NUMBER(3), -- salted
    expiry_date DATE,
    card_limit NUMBER(10, 2),
    account_status VARCHAR2(20), -- 'Active', 'Closed', 'Suspended'
    creation_date DATE NOT NULL,

    PRIMARY KEY (acc_no),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

-- transaction logs table
CREATE TABLE transactions (
    transaction_date DATE NOT NULL,
    transaction_time TIMESTAMP NOT NULL,
    from_account_number VARCHAR2(20) NOT NULL,
    transaction_status VARCHAR2(20) NOT NULL, -- Failed / Success
    transaction_reference_number VARCHAR2(30) NOT NULL,
    transaction_type VARCHAR2(20) NOT NULL, -- Credit / Debit
    transaction_amount NUMBER(19, 4) NOT NULL,
    to_account_number VARCHAR2(20) NOT NULL,
    transaction_mode VARCHAR2(20) NOT NULL, -- UPI / Net Banking / Cards
    
    CONSTRAINT transaction_ref_pk PRIMARY KEY (transaction_reference_number)
);

/* DUMMMMMMMY DATA */

-- customer
INSERT INTO customers VALUES (1, 'Kaira', 'Kov', 'kaira.kov@example.com', '1234567890', '123 Main St', 'Anytown', 'Anystate', '12345', 'Countryland', TO_DATE('1990-01-01', 'YYYY-MM-DD'), 'F');
INSERT INTO customers VALUES (2, 'Banerjee', 'Patil', 'baner@example.com', '0987654321', '456 Elm St', 'Kolkata', 'West Bengal', '54321', 'India', TO_DATE('1985-05-15', 'YYYY-MM-DD'), 'F');
INSERT INTO customers VALUES (3, 'Alex', 'Smith', 'alex.smith@example.com', '1122334455', '789 Pine St', 'Sometown', 'Somestate', '67890', 'Countryland', TO_DATE('1995-07-22', 'YYYY-MM-DD'), 'M');

-- accounts
INSERT INTO accounts VALUES ('ACC00001', 'IFSC0001', 5000, 'BankName', 'MainBranch', 1, '1111222233334444', 123, TO_DATE('2030-12-31', 'YYYY-MM-DD'), 50000, 'Active', SYSDATE);
INSERT INTO accounts VALUES ('ACC00002', 'IFSC0002', 3000, 'BankName', 'BranchTwo', 2, '5555666677778888', 456, TO_DATE('2029-11-30', 'YYYY-MM-DD'), 30000, 'Active', SYSDATE);
INSERT INTO accounts VALUES ('ACC00003', 'IFSC0003', 7000, 'BankName', 'BranchThree', 3, '9999000011112222', 789, TO_DATE('2028-10-29', 'YYYY-MM-DD'), 70000, 'Active', SYSDATE);

-- transactions
INSERT INTO transactions VALUES (SYSDATE, CURRENT_TIMESTAMP, 'ACC00001', 'Success', 'TXN00001', 'Credit', 1000, 'ACC00002', 'Net Banking');
INSERT INTO transactions VALUES (SYSDATE, CURRENT_TIMESTAMP, 'ACC00002', 'Success', 'TXN00002', 'Debit', 500, 'ACC00003', 'UPI');
INSERT INTO transactions VALUES (SYSDATE, CURRENT_TIMESTAMP, 'ACC00003', 'Failed', 'TXN00003', 'Credit', 2000, 'ACC00001', 'Debit Card');



DROP TABLE CUSTOMERS;
DROP TABLE TRANSACTIONS;
DROP TABLE ACCOUNTS;

