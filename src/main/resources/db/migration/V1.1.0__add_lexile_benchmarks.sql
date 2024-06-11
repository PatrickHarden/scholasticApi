/*
 Adding lexile_benchmarks table to calculate proficiency
*/
DROP TABLE IF EXISTS lexile_benchmarks;

CREATE TABLE lexile_benchmarks (
 id INT NOT NULL AUTO_INCREMENT,
 time_period VARCHAR(225) NULL,
 grade VARCHAR(225) NULL,
 level VARCHAR(45) NULL,
 lower_range INT NULL,
 upper_range INT NULL,
 created_timestamp DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
 modified_timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 PRIMARY KEY (id));