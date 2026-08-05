ALTER TABLE ansatte
    ADD fnr VARCHAR(11);

ALTER TABLE ansatte
    ALTER COLUMN fnr SET NOT NULL;

ALTER TABLE ansatte
    DROP COLUMN brukerid;