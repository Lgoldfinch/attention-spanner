create table if not exists todos (
    id uuid primary key,
    name text not null,
    created_timestamp timestamp not null
    tasks text[]
);