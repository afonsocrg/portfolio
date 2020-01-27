<!DOCTYPE html>
<html>
<head>
    <title>Listar Anomalias</title>
</head>
<body>
    <h3>Listar Anomalias entre 2 locais</h3>
    <?php
        require 'db.php';

        try {

            $db = connect_db();

            $sql = "SELECT * FROM local_publico;";
            $result = $db->prepare($sql);
            $result->execute();

            echo("<table style=\"border-spacing: 10px;\">\n");
            echo('<th scope="col">Latitude</th>');
            echo('<th scope="col">Longitude</th>');
            echo('<th scope="col">Nome</th>');
            echo('<th scope="col">Local1</th>');
            echo('<th scope="col">Local2</th>');

            echo("<form action=\"anomaliashow.php\" method=\"POST\">");
            foreach($result as $row) {
                echo("<tr>\n");
                echo("<td>{$row['latitude']}</td>");
                echo("<td>{$row['longitude']}</td>");
                echo("<td>{$row['nome']}</td>");

                echo("<td>");
                echo("<input type=\"radio\" name=\"local1\" value=\"{$row['latitude']}:{$row['longitude']}\" required>");
                echo("</td>");
                echo("<td>");
                echo("<input type=\"radio\" name=\"local2\" value=\"{$row['latitude']}:{$row['longitude']}\" required>");
                echo("</td>");
                echo("</tr>\n");

            }
            echo("</table>");
            echo("<input type=\"submit\" value=\"Listar\">");
            echo("</form>");

        } catch(PDOException $e) {
            echo("<p>ERROR: {$e->getMessage()}</p>");
        }

    ?>
    <p><a href="index.html">Voltar</a></p>
</body>
</html>