CREATE TABLE transactions (
                              transaction_id VARCHAR(50) PRIMARY KEY,
                              customer_id VARCHAR(50),
                              customer_name VARCHAR(100),
                              amount DOUBLE,
                              transaction_date DATE
);