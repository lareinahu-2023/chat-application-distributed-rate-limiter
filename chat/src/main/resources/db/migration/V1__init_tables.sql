

CREATE SCHEMA IF NOT EXISTS chat_app;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


CREATE TABLE IF NOT EXISTS chat_user (
    id UUID PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4(),
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);


CREATE TABLE IF NOT EXISTS chat_thread (
    id UUID PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS chat_participant (
    id UUID PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    chat_thread_id UUID NOT NULL,
    FOREIGN KEY (user_id) REFERENCES chat_user(id),
    FOREIGN KEY (chat_thread_id) REFERENCES chat_thread(id)
);
