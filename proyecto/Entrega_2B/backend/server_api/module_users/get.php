<?php

$skip = $json_input['skip']?? null;
$step = $json_input['step']?? null;

if(isNotNullData([$skip, $step])){
    $response = User::getAllBySkipStep($connection, $skip, $step);  
}else{
    exit();
}