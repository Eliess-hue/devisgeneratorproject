UPDATE quote
SET status = 'ACCEPTED'
WHERE status = 'accepted';

UPDATE quote
SET status = 'REFUSED'
WHERE status = 'refused';

UPDATE quote
SET status = 'DRAFT'
WHERE status = 'draft';

UPDATE quote
SET status = 'PENDING'
WHERE status = 'pending';

ALTER TABLE quote
    ADD CONSTRAINT ck_quote_status
        CHECK (
            status IN ('DRAFT', 'ACCEPTED', 'REFUSED', 'PENDING', 'EXPIRED')
            );