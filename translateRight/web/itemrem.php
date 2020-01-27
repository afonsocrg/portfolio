<!DOCTYPE html>
<html>
    <head>
        <title>Remover Item</title>
    </head>
    <body>
    <h1>Remover Item</h1>
    <?php
        require 'db.php';
        try {
            $db = connect_db();

            if ($_SERVER["REQUEST_METHOD"] == 'POST') {

                $db->beginTransaction();
                $sql = "DELETE FROM item WHERE id = :id";
                $result = $db->prepare($sql);
                $result->execute([':id' => $_POST["id"]]);
                $db->commit();

                echo("<p>Item {$_POST["id"]} removido!</p>");
            }

            $sql = "SELECT id, descricao FROM item;";
            $result = $db->prepare($sql);
            $result->execute();

            echo("<table>\n");
            echo('<th scope="col">ID</th>');
            echo('<th scope="col">Descrição</th>');
            echo('<th></th>');
            foreach($result as $row) {
                echo("<tr>\n");
                echo("<td>{$row['id']}</td>\n");
                echo("<td>{$row['descricao']}</td>\n");
                echo("<td>");
                echo("<form action=\"\" method=\"POST\">");
                echo("<input type=\"hidden\" name=\"id\" value=\"{$row['id']}\">");
                echo("<input type=\"submit\" value=\"Remover\">");
                echo('</form>');
                echo("</td>");
                echo("</tr>\n");
            }
            echo("</table>");
        } catch (PDOException $e) { 
            echo("<p>ERROR: {$e->getMessage()}</p>");
            $db->rollback();
        }
    ?>
    <a href="index.html">Voltar</a>
    </body>
</html>
