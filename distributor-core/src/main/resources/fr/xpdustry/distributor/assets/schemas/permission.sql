CREATE TABLE IF NOT EXISTS '{prefix}player' (
    'uuid'              VARCHAR(24)     NOT NULL,
    PRIMARY KEY ('uuid')
);

CREATE TABLE IF NOT EXISTS '{prefix}player_parent_group' (
    'player_uuid'       VARCHAR(24)     NOT NULL,
    'parent_group'      VARCHAR(63)     NOT NULL,
    FOREIGN KEY ('player_uuid')         REFERENCES '{prefix}player' ('uuid'),
    PRIMARY KEY ('player_uuid', 'parent_group')
);

CREATE TABLE IF NOT EXISTS '{prefix}player_permission' (
    'player_uuid'       VARCHAR(24)     NOT NULL,
    'permission'        VARCHAR(255)    NOT NULL,
    'value'             BOOLEAN         NOT NULL,
    FOREIGN KEY ('player_uuid')         REFERENCES '{prefix}player' ('uuid'),
    PRIMARY KEY ('player_uuid', 'permission')
);

CREATE TABLE IF NOT EXISTS '{prefix}group' (
     'name'             VARCHAR(63)     NOT NULL,
     'weight'           INT             NOT NULL,
     PRIMARY KEY ('name')
);

CREATE TABLE IF NOT EXISTS '{prefix}group_parent_group' (
    'group_name'        VARCHAR(63)     NOT NULL,
    'parent_group'      VARCHAR(63)     NOT NULL,
    FOREIGN KEY ('group_name')          REFERENCES '{prefix}group' ('name'),
    PRIMARY KEY ('group_name', 'parent_group')
);

CREATE TABLE IF NOT EXISTS '{prefix}group_permission' (
     'group_name'       VARCHAR(63)     NOT NULL,
     'permission'       VARCHAR(255)    NOT NULL,
     'value'            BOOLEAN         NOT NULL,
     FOREIGN KEY ('group_name') REFERENCES '{prefix}group' ('name'),
     PRIMARY KEY ('group_name', 'permission')
);
