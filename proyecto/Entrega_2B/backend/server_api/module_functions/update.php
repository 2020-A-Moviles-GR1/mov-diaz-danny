<?php

$id = $json_input['id']?? null;
$name = $json_input['name']?? null;
$description = $json_input['description']?? null;
$function_type_id = $json_input['function_type_id']?? null;

if(isNotNullData([$id, $name, $description, $function_type_id])){
    $function = new FunctionAssociated($id, $name, $description, $function_type_id);
    $response = $function->update($connection);  
}else{
    exit();
}