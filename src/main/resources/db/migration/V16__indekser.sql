ALTER TABLE oppfolging
    ADD CONSTRAINT "uc_oppfølgingentity_aktoerid" UNIQUE (aktoerid);

ALTER TABLE oppfolging
    ADD CONSTRAINT "uc_oppfølgingentity_brukerid" UNIQUE (brukerid);