<?php

$skip = $json_input['skip']?? null;
$step = $json_input['step']?? null;

if(isNotNullData([$skip, $step])){
    $response = FunctionAssociated::getAllFunctionsBySkipStep($connection, $skip, $step);  
}else{
    exit();
}