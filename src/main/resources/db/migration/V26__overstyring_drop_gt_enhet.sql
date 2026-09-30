DROP TRIGGER overstyring_synk_kolonner ON overstyring;
DROP FUNCTION overstyring_synk_kolonner();

ALTER TABLE overstyring
    DROP COLUMN gt,
    DROP COLUMN enhet;
