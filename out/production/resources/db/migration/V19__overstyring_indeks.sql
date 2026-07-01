-- Replaces idx_overstyringentity_navid (navid, fnr) with a composite index that also
-- covers created DESC and expires, making MAX(created) lookups in both
-- gjeldendeOverstyring and gjeldendeOverstyringer index-only operations.
DROP INDEX idx_overstyringentity_navid;

CREATE INDEX idx_overstyring_navid_fnr_created ON overstyring (navid, fnr, created DESC, expires);

