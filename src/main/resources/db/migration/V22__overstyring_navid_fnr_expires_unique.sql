DELETE FROM overstyring o
USING overstyring nyere
WHERE o.id < nyere.id
  AND o.navid = nyere.navid
  AND o.fnr = nyere.fnr
  AND o.expires = nyere.expires;

ALTER TABLE overstyring
    ADD CONSTRAINT uq_overstyring_navid_fnr_expires UNIQUE (navid, fnr, expires);
