<?php

$id = $json_input['id']?? null;

if(isNotNullData([$id])){
    $function = new FunctionAssociated($id, "", "", -1);
    $response = $function->delete($connection);  
}else{
    exit();
}