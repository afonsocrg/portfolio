<?php
function connect_db() {
    $host = "localhost";
    $user = getenv("POSTGRES_USER");
    $password = getenv("POSTGRES_PASS");
    $dbname = "translateRight";
    $db = new PDO("pgsql:host=$host;dbname=$dbname", $user, $password);
    $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    return $db;
}
?>
