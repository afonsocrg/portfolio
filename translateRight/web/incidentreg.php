<!DOCTYPE html>
<html>
    <head>
        <title>Registar Incidência</title>
    </head>
    <body>

        <h2>Registar Incidência:</h2>
        <?php
        require 'db.php';

        try {

            $db = connect_db();
            try {
                if($_SERVER["REQUEST_METHOD"] == 'POST') {
                    

                    $anomalia = $_POST['anomalia'];
                    $item = $_POST['item'];
                    $email = $_POST['email'];

                    $db->beginTransaction();

                    $sql = "INSERT INTO incidencia(anomalia_id, item_id, email) VALUES (:anomalia, :item, :email);";
                    $result = $db->prepare($sql);

                    $result->execute([':anomalia' => $anomalia, ':item' => $item, ':email' => $email]);

                    echo("<p>Incidência inserida!</p>");
                    $db->commit();

                }

            } catch (PDOException $e) {
                $msg = $e->getMessage();

                if(strstr($msg, "duplicate key")) {
                    echo("<p>Anomalia {$anomalia} já registada numa incidência!</p>");
                } else if(strstr($msg, "not present in table \"anomalia\"")) {
                    echo("<p>Anomalia {$anomalia} não existe!</p>");
                } else if(strstr($msg, "not present in table \"item\"")) {
                    echo("<p>Item {$item} não existe!</p>");
                } else if(strstr($msg, "not present in table \"utilizador\"")) {
                    echo("<p>Utilizador {$email} não existe!</p>");
                }

                $db->rollback();
            }

            $sql = "SELECT id, ts, descricao FROM anomalia;";
            $result = $db->prepare($sql);

            $result->execute();

            echo("<h3>Anomalia</h3>\n");
            
            echo("<form action=\"\" method=\"POST\">");

            echo("<table style=\"border-spacing: 10px;\">\n");
            echo('<th scope="col"></th>');
            echo('<th scope="col">ID</th>');
            echo('<th scope="col">Timestamp</th>');
            echo('<th scope="col">Descrição</th>');

            foreach($result as $row) {
                echo("<tr>\n");
                echo("<td>");
                echo("<input type=\"radio\" name=\"anomalia\" value=\"{$row['id']}\" required>");
                echo("</td>");
                echo("<td>{$row['id']}</td>\n");
                echo("<td>{$row['ts']}</td>\n");
                echo("<td>{$row['descricao']}</td>\n");
                echo("</tr>\n");
            }

            echo("</table>\n");

            $sql = "SELECT id, descricao, latitude, longitude FROM item;";
            $result = $db->prepare($sql);

            $result->execute();

            echo("<h3>Item</h3>\n");

            echo("<table style=\"border-spacing: 10px;\">\n");
            echo('<th scope="col"></th>');
            echo('<th scope="col">ID</th>');
            echo('<th scope="col">Descrição</th>');
            echo('<th scope="col">Latitude</th>');
            echo('<th scope="col">Longitude</th>');

           
            foreach($result as $row) {
                echo("<tr>\n");
                echo("<td>");
                echo("<input type=\"radio\" name=\"item\" value=\"{$row['id']}\" required>");
                echo("</td>");
                echo("<td>{$row['id']}</td>\n");
                echo("<td>{$row['descricao']}</td>\n");
                echo("<td>{$row['latitude']}</td>\n");
                echo("<td>{$row['longitude']}</td>\n");
                echo("</tr>\n");
            }

            echo("</table>\n");

            $sql = "SELECT email FROM utilizador;";
            $result = $db->prepare($sql);
            $result->execute();

            echo("<h3>Utilizador</h3>\n");

            echo("<select name=\"email\" required>\n");

            foreach($result as $row) {
                echo("<option value=\"{$row['email']}\">");
                echo("{$row['email']}");
                echo("</option>");
            }
            echo("</select>\n");

            echo("<p><input type=\"submit\" value=\"Registar\"></p>\n");
            echo("</form>\n");

        } catch (PDOException $e) {
            $msg = $e->getMessage();
            echo("<p>ERROR: {$msg}</p>");
        }
        
        
        ?>
        
        <a href="index.html">Voltar</a>
    </body>
</html>
