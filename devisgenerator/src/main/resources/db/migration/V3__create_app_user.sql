CREATE TABLE app_user (
                          id BIGSERIAL PRIMARY KEY,
                          username VARCHAR(255) NOT NULL UNIQUE,
                          password TEXT NOT NULL,
                          role TEXT NOT NULL
);