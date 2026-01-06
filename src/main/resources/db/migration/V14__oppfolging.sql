CREATE TABLE oppfolging
(
    id              UUID        NOT NULL,
    brukerid        VARCHAR(11) NOT NULL,
    aktoerid        VARCHAR(13) NOT NULL,
    start_tidspunkt TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    kontor          VARCHAR(4)  NOT NULL,
    slutt_tidspunkt TIMESTAMP WITHOUT TIME ZONE,
    created         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_oppfolging PRIMARY KEY (id)
);

ALTER TABLE oppfolging
    ADD CONSTRAINT uc_oppfolging_id UNIQUE (id);