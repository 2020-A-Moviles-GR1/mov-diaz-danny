<?php

$search_method = $json_input['search_method']?? null;
$search_value = $json_input['search_value']?? null;

if(isNotNullData([$search_method, $search_value])){
    $response = User::search($connection, $search_method, $search_value);  
}else{
    exit();
}