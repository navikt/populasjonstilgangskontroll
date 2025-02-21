ALTER TABLE overstyring
    ADD begrunnelse VARCHAR(255);

ALTER TABLE overstyring
    ALTER COLUMN begrunnelse SET NOT NULL;

ALTER TABLE overstyring
    ALTER COLUMN created SET NOT NULL;

ALTER TABLE overstyring
    ALTER COLUMN expires SET NOT NULL;

ALTER TABLE overstyring
    ALTER COLUMN fnr SET NOT NULL;

ALTER TABLE overstyring
    ALTER COLUMN navid SET NOT NULL;

ALTER TABLE overstyring
    ALTER COLUMN updated SET NOT NULL;