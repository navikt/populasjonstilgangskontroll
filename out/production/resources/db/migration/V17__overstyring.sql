
CREATE INDEX idx_gyldig ON ansatte (gyldigtil);

ALTER TABLE overstyring
    ALTER COLUMN begrunnelse TYPE VARCHAR(400) USING (begrunnelse::VARCHAR(400));
