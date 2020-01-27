<!DOCTYPE html>
<html>
    <head>
        <title>Inserir Proposta de Correção</title>
    </head>
    <body>
        <h2>Inserir Proposta de Correção</h2>
        <?php
            require 'db.php';
            try {
                $db = connect_db();

                try {
                    if ($_SERVER['REQUEST_METHOD'] == 'POST') {
                        $email = $_POST['email'];
                        $text = $_POST['text'];
                        $datetime = $_POST['datetime'];
                        $id = $_POST['id'];

                        $sql = "SELECT MAX(nro) from proposta_de_correcao where email = :email;";
                        $result = $db->prepare($sql);
                        $result->execute([':email' => $email]);

                        $nro = $result->fetch()[0];

                        if (is_null($nro)) {
                            $nro = 1;
                        } else {
                            $nro++;
                        }

                        $sql = "INSERT INTO proposta_de_correcao (email, nro, data_hora, texto)
                            VALUES (:email, :nro, :data_hora, :texto);";
                        $db->beginTransaction();
                        $ok = True;

                        $result = $db->prepare($sql);
                        $ret = $result->execute([":email" => $email, ":nro" => $nro,
                                                ":data_hora" => $datetime, ":texto" => $text]);
                        if (!$ret) $ok = False;

                        $sql = "INSERT INTO correcao (email, nro, anomalia_id) VALUES (:email, :nro, :anomalia_id);";
                        $result = $db->prepare($sql);
                        $ret = $result->execute([':email' => $email, ':nro' => $nro,
                                                 ':anomalia_id' => $id]);
                        if (!$ret) $ok = False;

                        if ($ok) {
                            $db->commit();
                            echo("<p>Proposta de correção de {$email} inserida!<p>");
                        } else {
                            echo("<p>Erro a inserir proposta de correção!</p>");
                            $db->rollback();
                        }
                    }
                } catch (PDOException $e) {
                    $msg = $e->getMessage();
                    if (strstr($msg, "invalid input syntax for type timestamp")) {
                        echo('<p>Formato de data-hora inválido!</p>');
                    } else {
                        echo("<p>ERROR: {$e->getMessage()}</p>");
                    }
                    $db->rollBack();
                }

                echo('<form action="" method="POST">');

                $sql = "SELECT email from utilizador_qualificado;";
                $result = $db->prepare($sql);
                $result->execute();

                echo('Utilizador Qualificado: <select name="email">');
                foreach($result as $row) {
                    $email = $row['email'];
                    echo("<option value=\"{$email}\">{$email}</option>");
                }
                echo('</select>');
                echo('<br>');

                echo('Data-hora: <input type="datetime-local" name="datetime">');

                echo('<br>');

                $sql = "SELECT id, descricao, tem_anomalia_redacao FROM anomalia;";
                $result = $db->prepare($sql);
                $result->execute();

                echo("<table style=\"border-spacing: 10px;\">\n");
                echo('<tr><th colspan="3">Anomalia</th></tr>');
                echo('<th scope="col">ID</th>');
                echo('<th scope="col">Tipo</th>');
                echo('<th scope="col">Descrição</th>');
                echo('<th></th>');
                foreach($result as $row) {
                    $id = $row['id'];
                    $type = $row['tem_anomalia_redacao'] ? 'Redação' : 'Tradução';
                    $description = $row['descricao'];

                    echo("<tr>\n");
                    echo("<td>{$id}</td>\n");
                    echo("<td>{$type}</td>");
                    echo("<td>{$description}</td>\n");
                    echo("<td>");
                    echo("<input type=\"radio\" name=\"id\" value=\"{$id}\">");
                    echo("</td>");
                    echo("</tr>\n");
                }
                echo("</table>");

                echo('<textarea name="text" rows="10" cols="45" placeholder="Texto da proposta..."></textarea>');
                echo('<br>');

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
