<?php

$id_function = $json_input['id_function']?? null;
$username_user = $json_input['username_user']?? null;
$is_augmented_data = $json_input['is_augmented_data']?? null;
$data_accelerometer_x = $json_input['data_accelerometer_x']?? null;
$data_accelerometer_y = $json_input['data_accelerometer_y']?? null;
$data_accelerometer_z = $json_input['data_accelerometer_z']?? null;
$data_gyroscope_x = $json_input['data_gyroscope_x']?? null;
$data_gyroscope_y = $json_input['data_gyroscope_y']?? null;
$data_gyroscope_z = $json_input['data_gyroscope_z']?? null;


if(isNotNullData([$id_function, $username_user, $is_augmented_data, $data_accelerometer_x, $data_accelerometer_y, $data_accelerometer_z, $data_gyroscope_x, $data_gyroscope_y, $data_gyroscope_z])){
    $sample = new Sample(-1, $id_function, $username_user, $is_augmented_data,
                            $data_accelerometer_x, $data_accelerometer_y, $data_accelerometer_z,
                            $data_gyroscope_x, $data_gyroscope_y, $data_gyroscope_z);
    $response = $sample->create($connection);  
}else{
    exit();
}