DROP TABLE IF EXISTS authority CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users
(
    id         SERIAL,
    name       VARCHAR,
    age        INTEGER,
    birth_date DATE,
    PRIMARY KEY (id)
);

CREATE TABLE authority
(
    id       SERIAL,
    name     varchar(30),
    users_id bigint,
    FOREIGN KEY (users_id) REFERENCES users (id),
    PRIMARY KEY (id)
);