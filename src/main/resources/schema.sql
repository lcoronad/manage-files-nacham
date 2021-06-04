CREATE TABLE if NOT exists movements_files( 
    file_name varchar(100),
    file_date date,
    file_state varchar(100),
    financial_entity varchar(200),
    target varchar(100),
    error_description varchar(200)
);