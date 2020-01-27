<!DOCTYPE html>
<html>
    <head>
        <title>Remover Anomalia</title>
    </head>
    <body>
    <h1>Remover Anomalia Redação</h1>
    <?php
        require 'db.php';
        try {
            $db = connect_db();

            if ($_SERVER["REQUEST_METHOD"] == 'POST') {

                $db->beginTransaction();

                $id = $_POST["id"];

                $sql = "SELECT COUNT(*) FROM anomalia_traducao WHERE id = :id; ";
                $result = $db->prepare($sql);
                if($result->execute([':id' => $id])) {
                    if($result->fetchColumn() > 0) {
                        $sql =  "UPDATE anomalia SET tem_anomalia_redacao = False WHERE id = :id;";
                        $result = $db->prepare($sql);
                        if($result->execute([':id' => $id])) {
                            echo("<p>Anomalia de redação eliminada</p>");
                            $db->commit();
                        } else {
                            echo("<p>Erro a remover anomalia!</p>");
                            $db->rollBack();
                        }
                        
                    } else {
                        $sql = "DELETE FROM anomalia WHERE id = :id;";
                        $result = $db->prepare($sql);
                        if($result->execute([':id' => $id])) {
                            echo("<p>Anomalia de redação eliminada</p>");
                            $db->commit();
                        } else {
                            echo("<p>Erro a remover anomalia!</p>");
                            $db->rollBack();
                        }
                        
                    }
                
                
                } else {
                    echo("<p>Erro a remover anomalia!</p>");
                    $db->rollBack();
                }
            }

            $sql = "SELECT id, ts, descricao FROM anomalia WHERE tem_anomalia_redacao = TRUE;";
            $result = $db->prepare($sql);
            $result->execute();

            echo('<table style="border-spacing: 10px;">');
            echo('<th scope="col">ID</th>');
            echo('<th scope="col">Timestamp</th>');
            echo('<th scope="col">Descrição</th>');
            echo('<th></th>');
            foreach($result as $row) {
                $id = $row['id'];
                $ts = $row['ts'];
                $desc = $row['descricao'];

                echo("<tr>\n");
                echo("<td>{$id}</td>\n");
                echo("<td>{$ts}</td>\n");
                echo("<td>{$desc}</td>\n");
                echo("<td>");
                echo("<form action=\"\" method=\"POST\">");
                echo("<input type=\"hidden\" name=\"id\" value=\"{$id}\">");
                echo("<input type=\"submit\" value=\"Remover\">");
                echo('</form>');
                echo("</td>");
                echo("</tr>\n");
            
            }
            echo("</table>");
        } catch (PDOException $e) { 
            echo("<p>ERROR: {$e->getMessage()}</p>");
        }
    ?>
    <a href="index.html">Voltar</a>
    </body>
</html>
