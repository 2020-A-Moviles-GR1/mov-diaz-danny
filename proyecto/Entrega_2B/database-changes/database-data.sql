-------------------function------------------------
--TEST
INSERT INTO `function` (`name`, `description`) VALUES 
('Accelerometer+x', 'Mover dispositivo a la derecha'), 
('Accelerometer-x', 'Mover dispositivo a la izquierda'), 
('Accelerometer+y', 'Mover dispositivo arriba'), 
('Accelerometer-y', 'Mover dispositivo abajo'), 
('Accelerometer+z', 'Mover dispositivo atras'), 
('Accelerometer-z', 'Mover dispositivo adelante'), 
('Gyroscope+x', 'Girar dispositivo en +x'), 
('Gyroscope-x', 'Girar dispositivo en -x'), 
('Gyroscope+y', 'Girar dispositivo en +y'), 
('Gyroscope-y', 'Girar dispositivo en -y'), 
('Gyroscope+z', 'Girar dispositivo en +z'), 
('Gyroscope-z', 'Girar dispositivo en -z'), 
('disparo', 'Mover 0. Girar en +z. Girar en -z de forma rápida'), 
('corte_izquierda', 'Con el teléfono boca abajo. Mover +x mientras se gira lentamente en -z'),
('corte_derecha', 'Con el teléfono boca abajo. Mover -x mientras se gira lentamente en +z');


-------------------user---------------------------
INSERT INTO `user` (`username`, `password`, `full_name`) VALUES ('user_test', '36ff39cc2290f65d1ca6a22a8780bc8648ce79aadd18b2a6174dd9401b2b1448', 'User Test');


-------------------function_type---------------------------
INSERT INTO `function_type` (`id`, `name`) VALUES (NULL, 'TEST'), (NULL, 'NAVIGATION'), (NULL, 'GENERAL_GAME_ROOM')


