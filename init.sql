drop database if exists golden;

create database golden;

create user golden_user createdb;

alter user golden_user with superuser;

\c golden;

grant all privileges on all tables in schema public to golden_user;

create table neighborhoods (
                               id serial primary key,
                               name text unique
);

create table businesses (
                            id serial primary key,
                            neighborhood_id integer references neighborhoods(id),
                            neighborhood_name text references neighborhoods(name),
                            name text,
                            street_address text,
                            location_start date,
                            location_end date,
                            coordinates text,
                            unique (name, neighborhood_id, location_start, location_end, coordinates)
);

create index idx_neighborhood_id on neighborhoods(id);
create index idx_neighborhood_name on neighborhoods(name);
create index idx_business_id on businesses(id);
create index idx_business_name on businesses(name);
create index idx_business_street_address on businesses(street_address);
create index idx_business_location_start on businesses(location_start);
create index idx_business_location_end on businesses(location_end);
create index idx_business_coordinates on businesses(coordinates);