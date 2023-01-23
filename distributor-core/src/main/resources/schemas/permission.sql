CREATE TABLE IF NOT EXISTS '{prefix}player' (
    'uuid'          VARCHAR(32)     NOT NULL,
    'parents'       VARCHAR(128)    NOT NULL,
    PRIMARY KEY ('uuid')
);

CREATE TABLE IF NOT EXISTS '{prefix}player_permission' (
    'player_uuid'   VARCHAR(32)     NOT NULL,
    'permission'    VARCHAR(64)     NOT NULL,
    'value'         BOOLEAN         NOT NULL,
    FOREIGN KEY ('player_uuid') REFERENCES '{prefix}player' ('uuid')
);

CREATE TABLE IF NOT EXISTS '{prefix}group' (
     'name'         VARCHAR(32)     NOT NULL,
     'parents'      VARCHAR(128)    NOT NULL,
     'weight'       INT             NOT NULL,
     PRIMARY KEY ('name')
);

CREATE TABLE IF NOT EXISTS '{prefix}group_permission' (
     'group_name'   VARCHAR(32)     NOT NULL,
     'permission'   VARCHAR(64)     NOT NULL,
     'value'        BOOLEAN NOT NULL,
     FOREIGN KEY ('group_name') REFERENCES '{prefix}group' ('name')
);

CREATE TABLE IF NOT EXISTS '{prefix}permission_option' (
    'key'           VARCHAR(32)     NOT NULL,
    'value'         VARCHAR(64)     NOT NULL,
    'type'          TINYINT         NOT NULL,
    PRIMARY KEY ('key')
);
