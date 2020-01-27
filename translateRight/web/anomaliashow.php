<!DOCTYPE html>
<html>
<head>
    <title>Listar Anomalias</title>
</head>
<body>
    <h3>Anomalias:</h3>
    <?php 
        require 'db.php';

        try {

            $db = connect_db();

            $local1 = $_POST['local1'];
            $local2 = $_POST['local2'];

            $latitudes = array(strtok($local1, ':'));
            $longitudes = array(strtok(':'));

            array_push($latitudes, strtok($local2, ':'));
            array_push($longitudes, strtok(':'));

            sort($latitudes, SORT_NUMERIC);
            sort($longitudes, SORT_NUMERIC);


            $sql = "SELECT A.id, A.zona, A.imagem, A.lingua, A.ts, A.descricao, A.tem_anomalia_redacao
            FROM item AS I, incidencia AS B, anomalia AS A
            WHERE A.id = B.anomalia_id AND B.item_id = I.id AND I.latitude >= :latitude1 AND 
            I.latitude <= :latitude2 AND I.longitude >= :longitude1 AND 
            I.longitude <= :longitude2";

            $result = $db->prepare($sql);
            $result->execute([':latitude1' => $latitudes[0], ':latitude2' => $latitudes[1], ':longitude1' => $longitudes[0], ':longitude2' => $longitudes[1]]);

            $count = 0;


            echo("<table style=\"border-spacing: 10px;\">\n");
            echo('<th scope="col">Id</th>');
            echo('<th scope="col">Zona</th>');
            echo('<th scope="col">Imagem</th>');
            echo('<th scope="col">Lingua</th>');
            echo('<th scope="col">TS</th>');
            echo('<th scope="col">Descricao</th>');
            echo('<th scope="col">Anomalia Redação?</th>');
            foreach($result as $row) {
                echo("<tr>\n");
                echo("<td>{$row['id']}</td>");
                echo("<td>{$row['zona']}</td>");
                echo("<td>{$row['imagem']}</td>");
                echo("<td>{$row['lingua']}</td>");
                echo("<td>{$row['ts']}</td>");
                echo("<td>{$row['descricao']}</td>");
                if ($row['tem_anomalia_redacao']) {
                    echo("<td>Sim</td>");
                } else {
                    echo("<td>Não</td>");
                }
                echo("</tr>\n");
                $count++;

            }
            echo("</table>");

            echo("<p>Encontradas {$count} anomalias</p>");

            $db = null;

        } catch (PDOException $e) {
            echo("<p>ERROR: {$e->getMessage()}</p>");
        }  
    ?>
    <p><a href="anomalialist.php">Voltar</a></p>
</body>
</html>
