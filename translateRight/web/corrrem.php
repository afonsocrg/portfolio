<!DOCTYPE html>
<html>
    <head>
        <title>Remover Correção</title>
    </head>
    <body>
    <h1>Remover Correção</h1>
    <?php
        require 'db.php';
        try {
            $db = connect_db();

            if ($_SERVER["REQUEST_METHOD"] == 'POST') {

                try {

                    $db->beginTransaction();

                    $sql = "DELETE FROM correcao WHERE email = :email AND nro = :nro AND anomalia_id = :anomalia_id;";
                    $result = $db->prepare($sql);
                    
                    
                    
                    if($result->execute([':email' => $_POST["email"], ':nro' => $_POST['nro'], ':anomalia_id' => $_POST['anomalia_id']])){

                        //Full participation
                        $sql = "SELECT COUNT(*)
                                FROM correcao
                                WHERE email = :email AND nro = :nro;";
                        $result = $db->prepare($sql);
                        $result->execute([':email' => $_POST["email"],
                                        ':nro' => $_POST['nro']]);
                        $count = $result->fetch()[0];
                        if ($count == 0) {
                            $db->rollBack();
                            throw new Exception("Erro: A remoção deixa a proposta de correção sem uma correção associada!");
                        }
                        
                        echo("<p>Correção de {$_POST["email"]} número {$_POST["nro"]} removida!</p>");
                        
                        $db->commit();

                    } else {
                        echo("<p>Erro ao remover correção</p>");
                        $db->rollBack();
                    }
                } catch(Exception $e) {
                    echo("<p>{$e->getMessage()}</p>");
                }


            }

            $sql = "SELECT email, nro, anomalia_id FROM correcao;";
            $result = $db->prepare($sql);
            $result->execute();

            echo("<table>\n");
            echo('<th></th>');
            echo('<th scope="col">Email</th>');
            echo('<th scope="col">Número</th>');
            echo('<th scope="col">Id Anomalia</th>');
            foreach($result as $row) {
                echo("<tr>\n");
                echo("<td>");
                echo("<form action=\"\" method=\"POST\">");
                echo("<input type=\"hidden\" name=\"email\" value=\"{$row['email']}\">");
                echo("<input type=\"hidden\" name=\"nro\" value=\"{$row['nro']}\">");
                echo("<input type=\"hidden\" name=\"anomalia_id\" value=\"{$row['anomalia_id']}\">");
                echo("<input type=\"submit\" value=\"Remover\">");
                echo('</form>');
                echo("</td>");
                echo("<td>{$row['email']}</td>\n");
                echo("<td>{$row['nro']}</td>\n");
                echo("<td>{$row['anomalia_id']}</td>\n");
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