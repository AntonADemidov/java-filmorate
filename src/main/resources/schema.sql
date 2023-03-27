  DROP TABLE IF EXISTS mpa, films, genres, films_genres, users, likes, friends CASCADE;

  CREATE TABLE IF NOT EXISTS mpa (
      mpa_id integer PRIMARY KEY,
      name varchar(50) NOT NULL
  );

  CREATE TABLE IF NOT EXISTS films (
      film_id integer PRIMARY KEY,
      name varchar(50) NOT NULL,
      description varchar(200) NOT NULL,
      release_date date NOT NULL,
      duration integer NOT NULL,
      mpa_id integer NOT NULL REFERENCES mpa (mpa_id),
      CONSTRAINT "films_release_date" CHECK (release_date >= '1895-12-28'),
      CONSTRAINT "films_duration" CHECK (duration >= '0')
  );

  CREATE TABLE IF NOT EXISTS genres (
      genre_id integer PRIMARY KEY,
      name varchar(50) NOT NULL
  );

  CREATE TABLE IF NOT EXISTS films_genres (
      id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
      film_id integer NOT NULL REFERENCES films (film_id),
      genre_id integer NOT NULL REFERENCES genres (genre_id)
  );

  CREATE TABLE IF NOT EXISTS users (
      user_id integer PRIMARY KEY,
      name varchar(50) NOT NULL,
      email varchar(50) NOT NULL,
      login varchar(50) NOT NULL,
      birthday date NOT NULL
  );

  CREATE TABLE IF NOT EXISTS likes (
      id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
      film_id integer NOT NULL REFERENCES films (film_id),
      user_id integer NOT NULL REFERENCES users (user_id)
  );

  CREATE TABLE IF NOT EXISTS friends (
      id integer NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
      user_id integer NOT NULL REFERENCES users (user_id),
      friend_id integer NOT NULL REFERENCES users (user_id)
  );