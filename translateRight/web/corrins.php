<!DOCTYPE html>
<html>
    <head>
        <title>Inserir Correção</title>
    </head>
    <body>
    <h2>Inserir Correção</h2>
    <?php
        require 'db.php';
        try {
            $db = connect_db();

            try {
                if ($_SERVER['REQUEST_METHOD'] == 'POST') {
                    $id = $_POST['id'];
                    $exp = explode(":", $_POST['proposal']);
                    if (count($exp) != 2) {
                        throw new Exception("Erro a codificar proposta!");
                    }
                    $email = $exp[0];
                    $nro = $exp[1];

                    $db->beginTransaction();

                    $sql = 'INSERT INTO correcao (email, nro, anomalia_id) VALUES (:email, :nro, :id);';
                    $result = $db->prepare($sql);
                    $ret = $result->execute([':email' => $email, ':nro' => $nro, ':id' => $id]);

                    if ($ret) {
                        echo("<p>Proposta de Correção $nro de $email corrige anomalia $id!</p>");
                        $db->commit();
                    } else {
                        echo("<p>Erro a inserir correção!</p>");
                        $db->rollBack();
                    }
                }
            } catch (PDOException $e) {
                $db->rollBack();
                $msg = $e->getMessage();
                if (strstr($msg, 'duplicate key')) {
                    echo("<p>Erro: Correção já existe!</p>");
                } else if (strstr($msg, 'null value in column')) {
                    echo("<p>Erro: Valores em falta!</p>");
                } else {
                    echo("<p>{$e->getMessage()}</p>");
                }
            } catch (Exception $e) {
                echo("<p>{$e->getMessage()}</p>");
            }

            $sql = 'SELECT email, nro, texto FROM proposta_de_correcao ORDER BY (email, nro);';
            $result = $db->prepare($sql);
            $result->execute();

            echo('<form action="" method="POST">');

            echo('<table style="border-spacing: 10px;">');
            echo('<tr><th colspan="3">Proposta de Correção</th></tr>');
            echo('<tr>');
            echo('<th scope="col">Email</th>');
            echo('<th scope="col">Nro</th>');
            echo('<th scope="col">Texto</th>');
            echo('</tr>');
            foreach($result as $row) {
                $email = $row['email'];
                $nro = $row['nro'];
                $text = $row['texto'];

                echo('<tr>');
                echo("<td>$email</td>");
                echo("<td>$nro</td>");
                echo("<td>$text</td>");
                echo('<td>');
                echo("<input type=\"radio\" name=\"proposal\" value=\"$email:$nro\">");
                echo('</td>');
                echo('</tr>');
            }
            echo('</table>');

            $sql = 'SELECT id, ts, descricao FROM anomalia;';
            $result = $db->prepare($sql);
            $result->execute();

            echo('<table style="border-spacing: 10px;">');
            echo('<tr><th colspan="3">Anomalia</th></tr>');
            echo('<tr>');
            echo('<th scope="col">ID</th>');
            echo('<th scope="col">Timestamp</th>');
            echo('<th scope="col">Descrição</th>');
            echo('</tr>');
            foreach($result as $row) {
                $id = $row['id'];
                $ts = $row['ts'];
                $desc = $row['descricao'];

                echo('<tr>');
                echo("<td>$id</td>");
                echo("<td>$ts</td>");
                echo("<td>$desc</td>");
                echo('<td>');
                echo("<input type=\"radio\" name=\"id\" value=\"$id\">");
                echo('</td>');
                echo('</tr>');
            }

            echo('</table>');

            echo('<input type="submit" value="Inserir">');
            echo('</form>');
        } catch (PDOException $e) {
            echo("<p>ERROR: {$e->getMessage()}</p>");
        }
    ?>
    <br>
    <a href="index.html">Voltar</a>
    </body>
</html>
