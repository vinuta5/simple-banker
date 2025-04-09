-- Initial account data
INSERT INTO accounts (account_number, account_type, balance, customer_id) VALUES
                                                                              ('1234567890', 'SAVINGS', 1000.00, 1),
                                                                              ('0987654321', 'CHECKING', 500.00, 1);

-- Initial transaction data
INSERT INTO transactions (from_account_id, to_account_id, amount, date) VALUES
                                                                            (1, 2, 100.00, CURRENT_TIMESTAMP()),
                                                                            (2, 1, 50.00, CURRENT_TIMESTAMP());