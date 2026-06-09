ALTER TABLE quote
    ADD COLUMN number VARCHAR NOT NULL,
    ADD COLUMN user_id BIGINT NOT NULL,
    ADD COLUMN total_ht NUMERIC,
    ADD COLUMN total_tva NUMERIC,
    ADD COLUMN total_ttc NUMERIC;
ALTER TABLE quote
    ADD CONSTRAINT fk_quote_user
        FOREIGN KEY (user_id)
            REFERENCES app_user(id);