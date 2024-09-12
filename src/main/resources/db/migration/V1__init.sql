DROP TABLE IF EXISTS notes;

CREATE TABLE notes
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    title         VARCHAR(255)          NULL,
    content       VARCHAR(255)          NULL,
    author        VARCHAR(255)          NULL,
    creation_date datetime              NOT NULL,
    CONSTRAINT pk_notes PRIMARY KEY (id)
);