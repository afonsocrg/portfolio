<!DOCTYPE html>
<html>
    <head>
        <title>Inserir Local</title>
    </head>
    <body>
        <?php
            require "db.php";
            try {

                if($_SERVER["REQUEST_METHOD"] == 'POST') {

                    $db = connect_db();
                    $latitude = $_POST['latitude'];
                    $longitude = $_POST['longitude'];

                    $local = $_POST['local'];

                    $db->beginTransaction();

                    $sql = "INSERT INTO local_publico (latitude, longitude, nome) VALUES (:latitude, :longitude, :nome);";
                    $result = $db->prepare($sql);
                    $result->execute([':latitude' => $latitude, ':longitude' => $longitude, ':nome' => $local]);
                    echo("<p>Local inserido!</p>");

                    $db->commit();
                    $db = null;
                }
            } catch (PDOException $e) {
                $msg = $e->getMessage();

                if(strstr($msg, "already exists")) {
                    echo("<p>Já existe um local em ({$latitude}, {$longitude})</p>");
                } else if (strstr($msg, "latitude_check")) {
                    echo("<p>Latitude tem de estar entre -90 and 90</p>");
                } else if(strstr($msg, "longitude_check")) {
                    echo("<p>Longitude tem de estar entre -180 and 180</p>");
                } else if(strstr($msg, "value too long")) {
                    echo("<p>Tamanho do input do nome do local demasiado longo. Max = 255 chars</p>");
                } else {
                    echo("ERROR: {$e->getMessage()}");
                }

                $db->rollBack();
            }
        ?>
        <form action="" method="post">
            <p>Latitude: <input type="number" step="0.000001" name="latitude" required></p>
            <p>Longitude: <input type="number" step="0.000001" name="longitude" required></p>
            <p>Nome do local: <input type="text" name="local" required></p>
            <p><input type="submit" value="Submit"></p>
        </form>
        <a href="index.html">Voltar</a>
    </body>
</html>
