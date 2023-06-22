CREATE TABLE IF NOT EXISTS  todos (
    id uuid PRIMARY KEY,
    name text NOT NULL,
    created_timestamp timestamp NOT NULL,
    tasks text[] NOT NULL
);