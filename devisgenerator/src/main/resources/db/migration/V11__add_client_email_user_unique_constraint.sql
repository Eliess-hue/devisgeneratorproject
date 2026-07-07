ALTER TABLE clients
DROP CONSTRAINT clients_email_key;
ALTER TABLE clients
    ALTER COLUMN email SET NOT NULL;
ALTER TABLE clients
    ADD CONSTRAINT uk_clients_email_user
        UNIQUE (email, user_id);