CREATE DATABASE rql;
\c rql;
CREATE TABLE users
(
  userid serial NOT NULL,
  username character varying,
  firstname character varying,
  hashpassword character varying,
  lastname character varying,
  isadmin boolean,
  CONSTRAINT user_pkey PRIMARY KEY (userid),
  CONSTRAINT user_username_key UNIQUE (username)
);
CREATE TABLE project
(
  projectid serial NOT NULL,
  projectname character varying,
  creationdate character varying,
  userid character varying,
  description character varying,
  CONSTRAINT project_pkey PRIMARY KEY (projectid),
  CONSTRAINT project_userid_fkey FOREIGN KEY (userid)
      REFERENCES public.users (username) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE TABLE database
(
  "databaseID" serial NOT NULL,
  host character varying,
  port integer,
  name character varying,
  userid character varying,
  password character varying,
  type character varying,
  projectid bigint,
  CONSTRAINT database_pkey PRIMARY KEY ("databaseID"),
  CONSTRAINT pro FOREIGN KEY (projectid)
      REFERENCES public.project (projectid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE TABLE collaborator
(
  collaboratorid serial NOT NULL,
  projectid bigint,
  userid character varying,
  CONSTRAINT collaborator_pkey PRIMARY KEY (collaboratorid),
  CONSTRAINT collaborator_projectid_fkey FOREIGN KEY (projectid)
      REFERENCES public.project (projectid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT collaborator_userid_fkey FOREIGN KEY (userid)
      REFERENCES public.users (username) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE TABLE favorite
(
  favoriteid serial NOT NULL,
  description character varying,
  query character varying,
  creationdate character varying,
  projectid bigint,
  favoritename character varying,
  type character varying,
  CONSTRAINT favorite_pkey PRIMARY KEY (favoriteid),
  CONSTRAINT favorite_projectid_fkey FOREIGN KEY (projectid)
      REFERENCES public.project (projectid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT favorite_favoritename_key UNIQUE (favoritename)
);

INSERT INTO users(
             username, firstname, hashpassword, lastname, isadmin)
    VALUES ('admin', 'admin', '$2a$12$Cq1GoUaBySCjWBHJJALYNOetItTezPXr1B9JdsLa8xqH5ARVVFplm', 'admin', TRUE);
-- login: admin
-- password: adminadmin
