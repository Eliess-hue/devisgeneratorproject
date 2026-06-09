ALTER TABLE clients
    ADD COLUMN user_id BIGINT NOT NULL;

ALTER TABLE clients
    ADD CONSTRAINT fk_clients_app_user
        FOREIGN KEY (user_id)
            REFERENCES app_user(id);
