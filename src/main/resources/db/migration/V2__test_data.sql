DO $$
DECLARE
    user1_id BIGINT;
    user2_id BIGINT;
    user3_id BIGINT;
    user4_id BIGINT;
    user5_id BIGINT;
BEGIN
    -- User 1: Иван Иванов password 'SecurePass123!'
    INSERT INTO users (name, date_of_birth, password)
    VALUES ('Иван Иванов', '1990-05-15', '$2a$12$eiaQ1Jxdp7QNltBNNp4kF.YBF9uRFcDAT1tu6fUeQGP23fGscDDTi')
    RETURNING id INTO user1_id;

    INSERT INTO email_data (user_id, email) VALUES (user1_id, 'ivanov@bank.com');
    INSERT INTO phone_data (user_id, phone) VALUES (user1_id, '79101234567');
    INSERT INTO account (user_id, balance, initial_balance)
    VALUES (user1_id, 10000.00, 10000.00);

    -- User 2: Мария Петрова password 'SecurePass123!'
    INSERT INTO users (name, date_of_birth, password)
    VALUES ('Мария Петрова', '1995-08-22', '$2a$12$/sAkLcNTsWX9UK5hKQsYl.E6RXeyW3xCpsIC/L7F7jU48pBZAAT6.')
    RETURNING id INTO user2_id;

    INSERT INTO email_data (user_id, email) VALUES (user2_id, 'petrova@bank.com');
    INSERT INTO phone_data (user_id, phone) VALUES (user2_id, '79207654321');
    INSERT INTO account (user_id, balance, initial_balance)
    VALUES (user2_id, 50000.50, 50000.50);

    -- User 3: Алексей Смирнов password 'SecurePass123!'
    INSERT INTO users (name, date_of_birth, password)
    VALUES ('Алексей Смирнов', '1988-12-03', '$2a$12$K2zToday1C.n/ANIlO1MmekmlUYWJxtpiMNQlEUwLkotlSx3QbVEC')
    RETURNING id INTO user3_id;

    INSERT INTO email_data (user_id, email) VALUES (user3_id, 'smirnov@bank.com');
    INSERT INTO phone_data (user_id, phone) VALUES (user3_id, '79305554433');
    INSERT INTO account (user_id, balance, initial_balance)
    VALUES (user3_id, 250000.00, 250000.00);

    -- User 4: Ольга Новикова password 'SecurePass123!'
    INSERT INTO users (name, date_of_birth, password)
    VALUES ('Ольга Новикова', '1993-04-18', '$2a$12$MeSzTsU3LQlNYx2pmt80Tufhh1DaSw8hjg1SBKg6pLtAhp07gwezC')
    RETURNING id INTO user4_id;

    INSERT INTO email_data (user_id, email) VALUES (user4_id, 'novikova@bank.com');
    INSERT INTO phone_data (user_id, phone) VALUES (user4_id, '79401231234');
    INSERT INTO account (user_id, balance, initial_balance)
    VALUES (user4_id, 75000.75, 75000.75);

    -- User 5: Дмитрий Волков password 'SecurePass123!'
    INSERT INTO users (name, date_of_birth, password)
    VALUES ('Дмитрий Волков', '1985-10-29', '$2a$12$JADGh0P/fULUxvW4Vig8LOWIoefEBXbTliW4WuZQcsrjfm/Dz5HUS')
    RETURNING id INTO user5_id;

    INSERT INTO email_data (user_id, email) VALUES (user5_id, 'volkov@bank.com');
    INSERT INTO phone_data (user_id, phone) VALUES (user5_id, '79509876543');
    INSERT INTO account (user_id, balance, initial_balance)
    VALUES (user5_id, 300000.00, 300000.00);
END $$;
