DROP TABLE IF EXISTS authorities;
DROP TABLE IF EXISTS user_profiles;
DROP TABLE IF EXISTS users;

-- Users table with core authentication fields
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(500) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- business-specific data
CREATE TABLE user_profiles (
   id BIGSERIAL PRIMARY KEY,
   user_id BIGINT NOT NULL UNIQUE,
   salary DECIMAL(19,2),
   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   CONSTRAINT fk_user_profiles_users
       FOREIGN KEY(user_id)
           REFERENCES users(id)
           ON DELETE CASCADE
);

-- role-based access control
CREATE TABLE authorities (
                             id BIGSERIAL PRIMARY KEY,
                             user_id BIGINT NOT NULL,
                             authority VARCHAR(50) NOT NULL,
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             CONSTRAINT fk_authorities_users
                                 FOREIGN KEY(user_id)
                                     REFERENCES users(id)
                                     ON DELETE CASCADE
);

CREATE UNIQUE INDEX ix_auth_user_authority
    ON authorities (user_id, authority);
