DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS requests CASCADE;

CREATE TABLE IF NOT EXISTS users (id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL, name VARCHAR(200) NOT NULL, email VARCHAR(100) NOT NULL, CONSTRAINT pk_user PRIMARY KEY (id), CONSTRAINT UQ_USER_EMAIL UNIQUE (email));

CREATE TABLE IF NOT EXISTS requests (id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,description VARCHAR(200) NOT NULL,requestor_id INTEGER REFERENCES users(id) ON DELETE CASCADE NOT NULL, created TIMESTAMP WITHOUT TIME ZONE NOT NULL);

CREATE TABLE IF NOT EXISTS items (id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,name VARCHAR(200) NOT NULL,description VARCHAR(200) NOT NULL,is_available BOOLEAN NOT NULL,owner_id INTEGER REFERENCES users(id) ON DELETE CASCADE NOT NULL,request_id INTEGER REFERENCES requests(id) NULL);

CREATE TABLE IF NOT EXISTS bookings (id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,start_date TIMESTAMP WITHOUT TIME ZONE,end_date TIMESTAMP WITHOUT TIME ZONE,item_id INTEGER REFERENCES items(id) ON DELETE CASCADE NOT NULL,booker_id INTEGER REFERENCES users(id) ON DELETE CASCADE NOT NULL,status VARCHAR(50) NOT NULL);

CREATE TABLE IF NOT EXISTS comments (id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,text VARCHAR(200) NOT NULL,item_id INTEGER REFERENCES items(id) ON DELETE CASCADE NOT NULL,author_id INTEGER REFERENCES users(id) ON DELETE CASCADE NOT NULL,created TIMESTAMP WITHOUT TIME ZONE NOT NULL);

