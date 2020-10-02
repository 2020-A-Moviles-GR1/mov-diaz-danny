<?php

$BASE_DIR = str_replace("public_html\api", "", dirname(__FILE__));
// echo $BASE_DIR.'models/Response.php';
require_once $BASE_DIR.'parameters.php';
require_once $BASE_DIR.'models/Database.php';
require_once $BASE_DIR.'models/Response.php';
require_once $BASE_DIR.'public_html/api/utils.php';
