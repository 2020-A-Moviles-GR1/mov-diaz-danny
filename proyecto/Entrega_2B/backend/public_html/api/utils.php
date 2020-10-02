<?php


function leerArchivo($archivo){
    if(file_exists($archivo)){
        $file = file_get_contents($archivo);
        return $file;

    }
    return null;
}

function readAllCookies(){
    echo "<br>Cookies almacenadas<br>";
    foreach ($_COOKIE as $key=>$val)
    {
        echo '<br>Cookie:'.$key.' Value:'.$val."<br>\n";
    }
}


function setACookie($cookie_name, $cookie_value, $days_expiration){
    setcookie($cookie_name, $cookie_value, time() + (86400 * $days_expiration), "/");
}

function deleteACookie($cookie_name){
    setcookie($cookie_name, "", time() - 3600, "/");
}



function sonDatosValidos($arreglo_datos){
    $palabras_baneadas = ['drop', 'delete', 'create', 'update', 'database', ';', 'select'];


    $validacion = [true, "correcto"];

    for($i=0; $i < count($arreglo_datos); $i++){
        $dato = $arreglo_datos[$i];



        if(gettype($dato) == "NULL"){
            $validacion[0] = false;
            $validacion[1] =  "Se ha enviado almenos un dato nulo , posicion: $i";
            break;

        }else if(gettype($dato) == 'string'){
            $frase  = strtolower($dato);

            for($j=0; $j<count($palabras_baneadas); $j++){
                $palabra = $palabras_baneadas[$j];

                if (strpos($frase, $palabra) !== false) {
                    $reporte .= '<>'.$frase;
                    $validacion[0] = false;
                    $validacion[1] =  "Se ha intentado inyectar código: $frase";
                    $i = count($arreglo_datos);

                    break;
                }


            }
        }


    }

    return $validacion;

}

function is_in_array($array, $key){
    $within_array = false;
    foreach( $array as $k=>$v ){

        if($k == $key ){
            $within_array = true;
            break;
        }
    }
    return $within_array;
}


function esDatoNulo($dato){
    return ($dato === null)? true: false;
}

function getUserIpAddr(){
    if(!empty($_SERVER['HTTP_CLIENT_IP'])){
        //ip from share internet
        $ip = $_SERVER['HTTP_CLIENT_IP'];
    }elseif(!empty($_SERVER['HTTP_X_FORWARDED_FOR'])){
        //ip pass from proxy
        $ip = $_SERVER['HTTP_X_FORWARDED_FOR'];
    }else{
        $ip = $_SERVER['REMOTE_ADDR'];
    }
    return $ip;
}
