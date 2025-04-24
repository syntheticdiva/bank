CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(500) NOT NULL,
    date_of_birth DATE NOT NULL,
    password VARCHAR(500) NOT NULL CHECK (LENGTH(password) >= 8)
);

CREATE TABLE account (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    balance DECIMAL(19,4) NOT NULL CHECK (balance >= 0),
    initial_balance DECIMAL(19,4) NOT NULL CHECK (initial_balance > 0)
);

CREATE TABLE email_data (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    email VARCHAR(200) UNIQUE NOT NULL,
    is_primary BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE phone_data (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    phone VARCHAR(13) UNIQUE NOT NULL CHECK (phone ~ '^7\d{10}$'),
    is_primary BOOLEAN NOT NULL DEFAULT false
);


