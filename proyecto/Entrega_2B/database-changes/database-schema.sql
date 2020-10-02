/*
Cambios hechos el: 21/08/2020
Por: Danny Díaz
Nota: Creación de la base
*/

CREATE DATABASE mov_gesture;
CREATE TABLE `mov_gesture`.`role` ( `id` INT UNSIGNED NOT NULL AUTO_INCREMENT , `name` VARCHAR(50) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;
CREATE TABLE `mov_gesture`.`user` ( `username` VARCHAR(10) NOT NULL , `password` VARCHAR(64) NOT NULL , `full_name` VARCHAR(100) NOT NULL , PRIMARY KEY (`username`)) ENGINE = InnoDB;
CREATE TABLE `mov_gesture`.`user_role` ( `id_role` INT UNSIGNED NOT NULL , `username_user` VARCHAR(10) NOT NULL , INDEX (`id_role`), INDEX (`username_user`)) ENGINE = InnoDB;

ALTER TABLE `user_role` ADD CONSTRAINT `fk_user_role_role` FOREIGN KEY (`id_role`) REFERENCES `role`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE; ALTER TABLE `user_role` ADD CONSTRAINT `fk_user_role_user` FOREIGN KEY (`username_user`) REFERENCES `user`(`username`) ON DELETE RESTRICT ON UPDATE CASCADE;


CREATE TABLE `mov_gesture`.`function` ( `id` INT UNSIGNED NOT NULL AUTO_INCREMENT , `name` VARCHAR(30) NOT NULL , `description` TEXT NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;


CREATE TABLE `mov_gesture`.`gesture` ( `id` INT UNSIGNED NOT NULL AUTO_INCREMENT , `id_function` INT UNSIGNED NOT NULL , `username_user` VARCHAR(10) NOT NULL , `name` VARCHAR(30) NOT NULL , PRIMARY KEY (`id`), INDEX (`id_function`)) ENGINE = InnoDB;

ALTER TABLE `gesture` ADD CONSTRAINT `fk_gesture_function` FOREIGN KEY (`id_function`) REFERENCES `function`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

CREATE TABLE `mov_gesture`.`sample` ( `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Discriminant' , `id_gesture` INT UNSIGNED NOT NULL , `is_augmented_data` BOOLEAN NOT NULL DEFAULT FALSE , `data_accelerometer_x` TEXT NOT NULL , `data_accelerometer_y` TEXT NOT NULL , `data_accelerometer_z` TEXT NOT NULL , `data_gyroscope_x` TEXT NOT NULL , `data_gyroscope_y` TEXT NOT NULL , `data_gyroscope_z` TEXT NOT NULL , PRIMARY KEY (`id`, `id_gesture`)) ENGINE = InnoDB;

ALTER TABLE `sample` ADD INDEX(`id_gesture`);

ALTER TABLE `sample` ADD CONSTRAINT `fk_sample_gesture` FOREIGN KEY (`id_gesture`) REFERENCES `gesture`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE `gesture` ADD UNIQUE(`id`);

ALTER TABLE gesture DROP PRIMARY KEY, ADD PRIMARY KEY(id_function, username_user);

ALTER TABLE `gesture` ADD INDEX(`username_user`);

ALTER TABLE `gesture` ADD CONSTRAINT `fk_gesture_user` FOREIGN KEY (`username_user`) REFERENCES `user`(`username`) ON DELETE RESTRICT ON UPDATE CASCADE;



ALTER TABLE `sample` ADD `id_function` INT UNSIGNED NOT NULL AFTER `id_gesture`, ADD `username_user` VARCHAR(10) NOT NULL AFTER `id_function`, ADD INDEX (`id_function`), ADD INDEX (`username_user`);

ALTER TABLE mov_gesture.sample DROP FOREIGN KEY fk_sample_gesture;

ALTER TABLE `sample` DROP `id_gesture`;

ALTER TABLE `sample` ADD CONSTRAINT `fk_sample_gesture` FOREIGN KEY (`id_function`, `username_user`) REFERENCES `gesture`(`id_function`, `username_user`) ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE sample DROP PRIMARY KEY, ADD PRIMARY KEY(id, id_function, username_user);

ALTER TABLE `gesture` DROP `id`;


ALTER TABLE `sample` DROP FOREIGN KEY `fk_sample_gesture`; ALTER TABLE `sample` ADD CONSTRAINT `fk_sample_gesture` FOREIGN KEY (`id_function`, `username_user`) REFERENCES `gesture`(`id_function`, `username_user`) ON DELETE CASCADE ON UPDATE CASCADE;


/*
Cambios hechos el: 04/09/2020
Por: Danny Díaz
Nota: 
*/

ALTER TABLE `sample` ADD `datetime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `username_user`;

/*
Cambios hechos el: 09/09/2020
Por: Danny Díaz
Nota: 
*/
CREATE TABLE `mov_gesture`.`function_type` ( `id` INT UNSIGNED NOT NULL AUTO_INCREMENT , `name` VARCHAR(30) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;

ALTER TABLE `function` ADD `function_type_id` INT UNSIGNED NOT NULL AFTER `description`, ADD INDEX (`function_type_id`);

ALTER TABLE `function` CHANGE `function_type_id` `function_type_id` INT(10) UNSIGNED NOT NULL DEFAULT '1';

UPDATE `function` SET function_type_id=1

--Disparo, corte izquierda, corte derecha
UPDATE `function` SET `function_type_id` = '3' WHERE `function`.`id` = 15;
UPDATE `function` SET `function_type_id` = '3' WHERE `function`.`id` = 16;
UPDATE `function` SET `function_type_id` = '3' WHERE `function`.`id` = 17;
