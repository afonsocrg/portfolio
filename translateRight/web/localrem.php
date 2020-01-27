<!DOCTYPE html>
<html>
    <head>
        <title>Remover Local</title>
    </head>
    <body>
    <h1>Remover Local</h1>
    <?php
        require 'db.php';
        try {
            $db = connect_db();

            if ($_SERVER["REQUEST_METHOD"] == 'POST') {

                $db->beginTransaction();

                $sql = "DELETE FROM local_publico WHERE latitude = :latitude AND longitude = :longitude;";
                $result = $db->prepare($sql);
                $result->execute([':latitude' => $_POST["latitude"], ':longitude' => $_POST['longitude']]);

                $db->commit();

                echo("<p>Local \"{$_POST["nome"]}\" removido!</p>");
            }

            $sql = "SELECT latitude, longitude, nome FROM local_publico;";
            $result = $db->prepare($sql);
            $result->execute();

            echo("<table>\n");
            echo('<th></th>');
            echo('<th scope="col">Latitude</th>');
            echo('<th scope="col">Longitude</th>');
            echo('<th scope="col">Nome</th>');
            foreach($result as $row) {
                echo("<tr>\n");
                echo("<td>");
                echo("<form action=\"\" method=\"POST\">");
                echo("<input type=\"hidden\" name=\"latitude\" value=\"{$row['latitude']}\">");
                echo("<input type=\"hidden\" name=\"longitude\" value=\"{$row['longitude']}\">");
                echo("<input type=\"hidden\" name=\"nome\" value=\"{$row['nome']}\">");
                echo("<input type=\"submit\" value=\"Remover\">");
                echo('</form>');
                echo("</td>");
                echo("<td>{$row['latitude']}</td>\n");
                echo("<td>{$row['longitude']}</td>\n");
                echo("<td>{$row['nome']}</td>\n");
                echo("</tr>\n");
            }
            echo("</table>");
        } catch (PDOException $e) { 
            $db->rollBack();
            echo("<p>ERROR: {$e->getMessage()}</p>");
        }
    ?>
    <br>
    <a href="index.html">Voltar</a>
    </body>
</html>
