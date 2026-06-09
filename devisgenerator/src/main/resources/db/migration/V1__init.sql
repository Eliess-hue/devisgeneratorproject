CREATE TABLE clients (
                         id BIGSERIAL PRIMARY KEY,
                         name TEXT NOT NULL,
                         email TEXT UNIQUE
);

CREATE TABLE quote (
                       id BIGSERIAL PRIMARY KEY,
                       client_id BIGINT REFERENCES clients(id),
                       created_at TIMESTAMP DEFAULT now(),
                       status TEXT NOT NULL
);

CREATE TABLE quote_line (
                            id BIGSERIAL PRIMARY KEY,
                            quote_id BIGINT REFERENCES quote(id),
                            description TEXT NOT NULL,
                            quantity INTEGER NOT NULL,
                            unit_price NUMERIC(10,2) NOT NULL
);