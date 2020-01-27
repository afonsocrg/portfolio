<!DOCTYPE html>
<html>
    <head>
        <title>Adicionar Item</title>
    </head>
    <body>
    <h1>Adicionar Item</h1>
    <?php
        $STRMAX = 255;
        require 'db.php';

        function coordsExist($lat, $lon, $locations) {
            # echo "Searching: {$lat}, {$lon}<br>";
            foreach($locations as $row) {
                # echo "{$row['latitude']} {$row['longitude']}<br>";
                if($lat === $row['latitude'] and $lon === $row['longitude']) {
                    # echo 'Found!';
                    return True;
                }
            }
            return False;
        }

        try {
            $db = connect_db();

            # existing locations
            $xLocationsQr = $db->prepare("select * from local_publico order by nome");
            $xLocationsQr->execute();
            $xLocations = $xLocationsQr->fetchAll();

            if ($_SERVER["REQUEST_METHOD"] == 'POST') {
                $desc = isset($_POST["desc"]) ? trim($_POST["desc"]) : NULL;
                $loc = isset($_POST["loc"]) ? trim($_POST["loc"]) : NULL;
                $coords = isset($_POST["coords"]) ? $_POST["coords"] : NULL;
                parse_str($coords, $coordinates);
                $lat = $coordinates['lat'];
                $lon = $coordinates['lon'];

                # check values
                if(is_string($desc) and 0 < strlen($desc) and strlen($desc) < $STRMAX and
                   is_string($loc)  and 0 < strlen($loc)  and strlen($loc)  < $STRMAX and
                   coordsExist($lat, $lon, $xLocations)
                ) {
                    $db->beginTransaction();
                    $sql = "insert into item (descricao, localizacao, latitude, longitude) values(:desc, :loc, :lat, :lon);";
                    $result = $db->prepare($sql);
                    if($result->execute([':desc' => $desc, ':loc' => $loc, ':lat' => $lat, ':lon' => $lon])) {
                        $db->commit();
                        echo("<p>Item adicionado!</p>");
                    } else {
                        echo("<p>Erro ao adicionar item</p>");
                    }
                    
                    
                } else {
                    echo("ERROR: Could not add item. Invalid fields\n");
                }
            }

            echo "<form action=\"\" method=\"POST\">";
            echo "<p>Descrição: <input type=\"text\" name=\"desc\" required></p>";
            echo("<table>\n");
            echo('<th></th>');
            # the user doesn't really care about the coordinates. How can we hide them?
            echo('<th scope="col">Local</th>');
            echo('<th scope="col">Latitude</th>');
            echo('<th scope="col">Longitude</th>');
            foreach($xLocations as $row) {
                echo("<tr>\n");
                echo("<td>");
                echo("<input type=\"radio\" name=\"coords\" value=\"lat={$row['latitude']}&lon={$row['longitude']}\" required>");
                echo("</td>");
                echo("<td>{$row['nome']}</td>\n");
                echo("<td>{$row['latitude']}</td>\n");
                echo("<td>{$row['longitude']}</td>\n");
                echo("</tr>\n");
            }
            echo("</table>");
            echo "<p>Localização: <input type=\"text\" name=\"loc\" required></p>";
            echo("<input type=\"submit\" value=\"Adicionar\">");
            echo('</form>');
        } catch (PDOException $e) { 
            echo("<p>ERROR: {$e->getMessage()}</p>");
            $db->rollBack();
        }
    ?>
    <br>
    <a href="index.html">Voltar</a>
    </body>
</html>
