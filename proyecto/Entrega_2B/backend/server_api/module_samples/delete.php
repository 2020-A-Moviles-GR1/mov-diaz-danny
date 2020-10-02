<?php

$id = $json_input['id']?? null;

if(isNotNullData([$id])){
    $response = Sample::delete($connection, $id);
}else{
    exit();
}