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
            if ($_SERVER['REQUEST_METHOD'] == 'POST') {
                $db = connect_db();

                $email_selected = $_POST['email'];
                $nro_selected = $_POST['nro'];
                $id_selected = $_POST['id'];

                echo("<p>Proposta de Correção <b>$nro_selected</b> de <b>$email_selected</b> para incidência <b>$id_selected</b></p>");

                echo('<form action="correditlist.php" method="POST">');
                echo("<input type=\"hidden\" name=\"email_toedit\" value=\"$email_selected\">");
                echo("<input type=\"hidden\" name=\"nro_toedit\" value=\"$nro_selected\">");
                echo("<input type=\"hidden\" name=\"id_toedit\" value=\"$id_selected\">");

                $sql = 'SELECT email, nro, texto FROM proposta_de_correcao ORDER BY (email, nro);';
                $result = $db->prepare($sql);
                $result->execute();

                echo('<table style="border-spacing: 10px;">');
                echo('<tr><th scope="col" colspan="3">Proposta de Correção</th></tr>');
                echo('<tr>');
                echo('<th scope="col" colspan="1">Email</th>');
                echo('<th scope="col" colspan="1">Nro</th>');
                echo('<th scope="col" colspan="1">Texto</th>');
                echo('</tr>');
                foreach ($result as $row) {
                    $email = $row['email'];
                    $nro = $row['nro'];
                    $text = $row['texto'];

                    echo('<tr>');
                    echo("<td>$email</td>");
                    echo("<td>$nro</td>");
                    echo("<td>$text</td>");
                    if ($email == $email_selected &&
                        $nro == $nro_selected) {
                        echo("<td><input type=\"radio\" checked name=\"proposal\" value=\"$email:$nro\"></td>");
                    } else {
                        echo("<td><input type=\"radio\" name=\"proposal\" value=\"$email:$nro\"></td>");
                    }
                    echo('</tr>');
                }
                echo('</table>');

                $sql = 'SELECT A.id, A.descricao
                        FROM incidencia I, anomalia A
                        WHERE I.anomalia_id = A.id
                        ORDER BY A.id;';
                $result = $db->prepare($sql);
                $result->execute();

                echo('<table style="border-spacing: 10px;">');
                echo('<tr><th scope="col" colspan="2">Incidência</th></tr>');
                echo('<tr>');
                echo('<th scope="col" colspan="1">ID</th>');
                echo('<th scope="col" colspan="1">Descrição</th>');
                echo('</tr>');
                foreach ($result as $row) {
                    $id = $row['id'];
                    $desc = $row['descricao'];

                    echo('<tr>');
                    echo("<td>$id</td>");
                    echo("<td>$desc</td>");
                    if ($id == $id_selected) {
                        echo("<td><input type=\"radio\" checked name=\"id\" value=\"$id\"></td>");
                    } else {
                        echo("<td><input type=\"radio\" name=\"id\" value=\"$id\"></td>");
                    }
                    echo('</tr>');
                }
                echo('</table>');

                echo('<input type="submit" value="Guardar">');
            } else {
                header('Location: /correditlist.php');
            }
        } catch (PDOException $e) {
            echo("<p>ERROR: {$e->getMessage()}</p>");
        }
    ?>
    <br><br>
    <a href="correditlist.php">Voltar</a>
    </body>
</html>
