<!DOCTYPE html>
<html>
<head>
    <title>Registar Item Duplicado</title>
</head>
<body>
    <h1>Registar Item Duplicado</h1>
    <?php
        require 'db.php';

        try {

            $db = connect_db();

            if ($_SERVER["REQUEST_METHOD"] == 'POST') {

                $i1 = $_POST['i1'];
                $i2 = $_POST['i2'];

                if ($i2 < $i1) {
                    $tmp = $i1;
                    $i1 = $i2;
                    $i2 = $tmp;
                }

                try {

                $db->beginTransaction();
                $sql = "INSERT INTO duplicado (item1, item2) values (:i1, :i2)";
                $result = $db->prepare($sql);
                $ret = $result->execute([':i1' => $i1,
                                         ':i2' => $i2]);
                if ($ret) {
                    echo("<p>Duplicado ({$i1},{$i2}) registado!</p>");
                    $db->commit();
                } else {
                    echo("<p>Erro a registar duplicado!</p>");
                    $db->rollBack();
                }

                } catch(PDOException $e) {
                    $msg = $e->getMessage();
                    if (strstr($msg, "already exists")) {
                        echo("<p>Duplicado ({$i1},{$i2}) já existe!</p>");
                    } else if ($i1 == $i2) {
                        echo("<p>Item não pode ser duplicado de si próprio!</p>");
                    } else {
                        echo("<p>Erro a registar duplicado!</p>");
                    }

                    $db->rollback();
                }
            }

            $sql = "SELECT id, descricao FROM item;";
            $result = $db->prepare($sql);
            $result->execute();

            echo("<table style=\"border-spacing: 10px;\">\n");
            echo('<th scope="col">ID</th>');
            echo('<th scope="col">Descricao</th>');
            echo('<th scope="col">Dup1</th>');
            echo('<th scope="col">Dup2</th>');

            echo("<form action=\"\" method=\"POST\">");
            foreach($result as $row) {
                echo("<tr>\n");
                echo("<td>{$row['id']}</td>\n");
                echo("<td>{$row['descricao']}</td>\n");
                echo("<td>");
                echo("<input type=\"radio\" name=\"i1\" value=\"{$row['id']}\">");
                echo("</td>");
                echo("<td>");
                echo("<input type=\"radio\" name=\"i2\" value=\"{$row['id']}\">");
                echo("</td>");
                echo("</tr>\n");
            }
            echo("</table>");
            echo("<input type=\"submit\" value=\"Registar\">");
            echo("</form>");
        } catch(PDOException $e) {
            echo("<p>ERROR: {$e->getMessage()}</p>");
        }
    ?>
<p><a href="index.html">Voltar</a></p>
</body>
</html>
