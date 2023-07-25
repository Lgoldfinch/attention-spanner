CREATE TABLE IF NOT EXISTS todo_list (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    tasks TEXT[] NOT NULL
);

create index if not exists index_todos_id on todo_list(id);

CREATE TABLE IF NOT EXISTS todo (
    id UUID PRIMARY KEY,
    todo_list_id UUID
    name TEXT NOT NULL,
    is_completed BOOLEAN NOT NULL

constraint fk_todo_list_id
    foreign key (todo_list_id)
    references todo_list(id)
    on update no action on delete cascade
);

create index if not exists index_todo_id on todo(id);