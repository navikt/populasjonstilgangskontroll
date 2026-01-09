ALTER TABLE overstyring
    ADD CONSTRAINT pk_overstyring PRIMARY KEY (id);

CREATE INDEX idx_gyldig ON ansatte (gyldigtil);

DROP SEQUENCE ansatte_id_seq CASCADE;

DROP SEQUENCE overstyring_id_seq CASCADE;

ALTER TABLE overstyring
    ALTER COLUMN begrunnelse TYPE VARCHAR(400) USING (begrunnelse::VARCHAR(400));

CREATE INDEX idx_overstyringentity_navid ON overstyring (navid, fnr);