CREATE TABLE config_table1 (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50),
    value VARCHAR(50),
    version_id INT REFERENCES config_version_history(version_id),
    is_active BOOLEAN
);

CREATE TABLE config_table2 (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50),
    value VARCHAR(50),
    version_id INT REFERENCES config_version_history(version_id),
    is_active BOOLEAN
);

CREATE TABLE config_table3 (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50),
    value VARCHAR(50),
    version_id INT REFERENCES config_version_history(version_id),
    is_active BOOLEAN
);

CREATE TABLE config_version_history (
    version_id SERIAL PRIMARY KEY,
    version_date TIMESTAMP,
    version_notes VARCHAR(200)
);
