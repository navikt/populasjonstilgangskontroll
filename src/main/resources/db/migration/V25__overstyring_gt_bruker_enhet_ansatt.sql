ALTER TABLE overstyring
    ADD gt_bruker VARCHAR(6),
    ADD enhet_ansatt VARCHAR(6);

UPDATE overstyring
SET gt_bruker    = gt,
    enhet_ansatt = enhet;

-- Midlertidig toveis synk mens gamle og nye kolonner er i bruk samtidig. Fjernes når gamle kolonner droppes.
CREATE OR REPLACE FUNCTION overstyring_synk_kolonner()
    RETURNS TRIGGER AS
$$
BEGIN
    IF TG_OP = 'INSERT' THEN
        NEW.gt_bruker := COALESCE(NEW.gt_bruker, NEW.gt);
        NEW.gt := COALESCE(NEW.gt, NEW.gt_bruker);
        NEW.enhet_ansatt := COALESCE(NEW.enhet_ansatt, NEW.enhet);
        NEW.enhet := COALESCE(NEW.enhet, NEW.enhet_ansatt);
    ELSE
        IF NEW.gt IS DISTINCT FROM OLD.gt THEN
            NEW.gt_bruker := NEW.gt;
        ELSIF NEW.gt_bruker IS DISTINCT FROM OLD.gt_bruker THEN
            NEW.gt := NEW.gt_bruker;
        END IF;
        IF NEW.enhet IS DISTINCT FROM OLD.enhet THEN
            NEW.enhet_ansatt := NEW.enhet;
        ELSIF NEW.enhet_ansatt IS DISTINCT FROM OLD.enhet_ansatt THEN
            NEW.enhet := NEW.enhet_ansatt;
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER overstyring_synk_kolonner
    BEFORE INSERT OR UPDATE
    ON overstyring
    FOR EACH ROW
EXECUTE FUNCTION overstyring_synk_kolonner();
