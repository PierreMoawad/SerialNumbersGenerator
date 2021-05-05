USE serial_numbers_repository;

CREATE TABLE IF NOT EXISTS serial_numbers_repository.serial_numbers_set
(
    `name`     VARCHAR(45) NOT NULL,
    `quantity` INT         NOT NULL,
    `created`  DATETIME    NOT NULL,
    PRIMARY KEY (name)
);

CREATE TABLE IF NOT EXISTS serial_numbers_repository.serial_number
(
    `number`          VARCHAR(20) NOT NULL,
    `serial_set_name` VARCHAR(45) NOT NULL,
    PRIMARY KEY (number)
);