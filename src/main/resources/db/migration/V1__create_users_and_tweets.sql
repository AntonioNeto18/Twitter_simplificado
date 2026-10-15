CREATE TABLE tb_users (
    id        UUID         PRIMARY KEY,
    username  VARCHAR(15)  NOT NULL UNIQUE,
    biography VARCHAR(160),
    password  VARCHAR(255) NOT NULL,
    role      VARCHAR(20)  NOT NULL,
    status    VARCHAR(20)  NOT NULL
);

CREATE TABLE tb_tweets (
    id        UUID         PRIMARY KEY,
    content   VARCHAR(280) NOT NULL,
    author_id UUID         NOT NULL REFERENCES tb_users (id)
);

CREATE INDEX idx_tb_tweets_author_id ON tb_tweets (author_id);
