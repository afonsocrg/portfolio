<!DOCTYPE html>
<html>
    <head>
        <title>Editar Proposta de Correção</title>
    </head>
    <body>
        <h2>Editar Proposta de Correção</h2>
        <?php
            require 'db.php';

            try {
                $db = connect_db();

                try {
                    if ($_SERVER['REQUEST_METHOD'] == 'POST') {
                        $email = $_POST['email'];
                        $nro = $_POST['nro'];
                        $datetime = urldecode($_POST['datetime']);
                        $text = $_POST['text'];

                        $db->beginTransaction();
                        $sql = "UPDATE proposta_de_correcao
                                SET data_hora = :datetime, texto = :text
                                WHERE email = :email AND nro = :nro;";
                        $result = $db->prepare($sql);
                        $ret = $result->execute([":email" => $email, ":nro" => $nro,
                                                ":datetime" => $datetime, ":text" => $text]);

                        if ($ret) {
                            $db->commit();
                            echo("<p>Proposta de Correção ${nro} de ${email} editada!</p>");
                        } else {
                            $db->rollBack();
                            echo("<p>Erro a editar Proposta de Correção!</p>");
                        }
                    }
                } catch (PDOException $e) {
                    $msg = $e->getMessage();
                    if (strstr($msg, "invalid input syntax for type timestamp")) {
                        echo('<p>Formato de data-hora inválido!</p>');
                    } else {
                        echo("<p>ERROR: {$msg}</p>");
                    }
                    $db->rollBack();
                }

                $sql = "SELECT * from proposta_de_correcao ORDER BY (email, nro);";
                $result = $db->query($sql);

                echo('<table style="border-spacing: 10px;">');
                echo('<th scope="col">Email</th>');
                echo('<th scope="col">Nro</th>');
                echo('<th scope="col">Data-hora</th>');
                echo('<th scope="col">Texto</th>');

                foreach ($result as $row) {
                    $email = $row['email'];
                    $datetime = $row['data_hora'];
                    $nro = $row['nro'];
                    $text = $row['texto'];

                    echo('<tr>');
                    echo('<form action="" method="POST">');

                    echo("<td>$email</td>");
                    echo("<input type=\"hidden\" name=\"email\" value=\"$email\">");
                    echo("<td>$nro</td>");
                    echo("<input type=\"hidden\" name=\"nro\" value=\"$nro\">");

                    echo("<td>");
                    echo("<input type=\"datetime-local\" name=\"datetime\" value=\"$datetime\">");
                    echo("</td>");

                    echo("<td>");
                    echo("<input type=\"datetime-local\" name=\"text\" value=\"$text\">");
                    echo("</td>");

                    echo("<td>");
                    echo("<input type=\"submit\" value=\"Guardar\">");
                    echo("</td>");

                    echo('</form>');
                    echo('</tr>');
                }

                echo('</table>');
            } catch(PDOException $e) {
                echo("<p>ERROR: {$e->getMessage()}</p>");
            }


        ?>
        <a href="index.html">Voltar</a>
    </body>
</html>
