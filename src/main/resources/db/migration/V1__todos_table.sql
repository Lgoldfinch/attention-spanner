CREATE TABLE IF NOT EXISTS todo_list (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    expiry_date TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS index_todos_id ON todo_list(id);

CREATE TABLE IF NOT EXISTS todo (
    id UUID PRIMARY KEY,
    todo_list_id UUID
    name TEXT NOT NULL,
    is_completed BOOLEAN NOT NULL

CONSTRAINT fk_todo_list_id
    FOREIGN KEY (todo_list_id)
    REFERENCES todo_list(id)
    ON UPDATE NO ACTION ON DELETE DELETE
);

CREATE INDEX IF NOT EXISTS index_todo_id ON todo(id);