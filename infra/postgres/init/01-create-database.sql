CREATE USER taskmanager_user WITH PASSWORD 'taskmanager';
CREATE DATABASE taskmanager_db OWNER taskmanager_user;

CREATE USER keycloak_user WITH PASSWORD 'keycloak';
CREATE DATABASE keycloak_db OWNER keycloak_user;