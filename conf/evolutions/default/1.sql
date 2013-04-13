# Articles schema

# --- !Ups

CREATE SEQUENCE article_id_seq;
CREATE TABLE article (
    id integer NOT NULL DEFAULT nextval('article_id_seq'),
    caption varchar(255),
    body varchar(255)
);

# --- !Downs

DROP TABLE article;
DROP SEQUENCE article_id_seq;