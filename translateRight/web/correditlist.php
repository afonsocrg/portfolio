<!DOCTYPE html>
<html>
    <head>
        <title>Editar Correção</title>
        <style>
.top {
border: 1px solid black;

}
</style>
    </head>
    <body>
    <h2>Editar Correção</h2>
    <?php
        require 'db.php';
        try {
            $db = connect_db();

            if ($_SERVER['REQUEST_METHOD'] == 'POST') {
                $tmp = explode(':', $_POST['proposal']);

                if (count($tmp) != 2) {
                    throw new Exception("Erro a codificar proposta!");
                }

                $email = $tmp[0];
                $nro = $tmp[1];
                $id = $_POST['id'];
                $email_toedit = $_POST['email_toedit'];
                $nro_toedit = $_POST['nro_toedit'];
                $id_toedit = $_POST['id_toedit'];

                try {
                    $sql = "UPDATE correcao
                            SET email = :email, nro = :nro, anomalia_id = :id
                            WHERE email = :email_toedit
                                  AND nro = :nro_toedit
                                  AND anomalia_id = :id_toedit;";
                    $db->beginTransaction();
                    $result = $db->prepare($sql);
                    $ret = $result->execute([
                        ':email' => $email,
                        ':nro' => $nro,
                        ':id' => $id,
                        ':email_toedit' => $email_toedit,
                        ':nro_toedit' => $nro_toedit,
                        ':id_toedit' => $id_toedit ]);

                    // Verify full participation
                    $sql = "SELECT COUNT(*)
                            FROM correcao
                            WHERE email = :email AND nro = :nro;";
                    $result = $db->prepare($sql);
                    $result->execute([':email' => $email_toedit,
                                      ':nro' => $nro_toedit]);
                    $count = $result->fetch()[0];
                    if ($count == 0) {
                        $db->rollBack();
                        throw new Exception("Erro: A edição deixa a proposta de correção sem uma correção associada!");
                    }

                    if ($ret) {
                        $db->commit();
                        echo("<p>Correção (Email: $email_toedit, Nro: $nro_toedit, Anomalia_id: $id_toedit)</p>");
                        echo("<p>alterada para</p>");
                        echo("<p>Correção (Email: $email, Nro: $nro, Anomalia_id: $id)</p>");
                    } else {
                        $db->rollBack();
                    }
                    
                } catch (PDOException $e) {
                    $db->rollBack();
                    $msg = $e->getMessage();

                    if (strstr($msg, 'Unique violation')) {
                        echo("<p>Correção (Email: $email, Nro: $nro, Anomalia_id: $id) já existe!</p>");
                    } else {
                        echo("<p>ERROR: $msg</p>");
                    }
                } catch (Exception $e) {
                    echo("<p>{$e->getMessage()}</p>");
                }
            }

            $sql = 'SELECT C.email, C.nro, C.texto, A.id, A.descricao
                FROM (proposta_de_correcao natural join correcao) as C,
                     anomalia A
                WHERE C.anomalia_id = A.id
                ORDER BY (email, nro);';
            $result = $db->prepare($sql);
            $result->execute();


            echo('<table style="border-spacing: 10px;">');
            echo('<tr>');
            echo('<th class="top" colspan="3">Proposta de Correção</th>');
            echo('<th class="top" colspan="2">Incidência</th>');
            echo('</tr>');
            echo('<tr>');
            echo('<th scope="col">Email</th>');
            echo('<th scope="col">Nro</th>');
            echo('<th scope="col">Texto</th>');
            echo('<th scope="col">ID</th>');
            echo('<th scope="col">Descrição</th>');
            echo('</tr>');
            foreach($result as $row) {
                $email = $row['email'];
                $nro = $row['nro'];
                $text = $row['texto'];
                $id = $row['id'];
                $desc = $row['descricao'];

                echo('<form action="corredit.php" method="POST">');
                echo('<tr>');
                echo("<td>$email</td>");
                echo("<td>$nro</td>");
                echo("<td>$text</td>");
                echo("<td>$id</td>");
                echo("<td>$desc</td>");
                echo('<td>');
                echo("<input type=\"hidden\" name=\"email\" value=\"$email\">");
                echo("<input type=\"hidden\" name=\"nro\" value=\"$nro\">");
                echo("<input type=\"hidden\" name=\"id\" value=\"$id\">");
                echo('<input type="submit" value="Editar">');
                echo('</td>');
                echo('</tr>');
                echo('</form>');
            }
            echo('</table>');
        } catch (PDOException $e) {
            echo("<p>ERROR: {$e->getMessage()}</p>");
        }
    ?>
    <br>
    <a href="index.html">Voltar</a>
    </body>
</html>
