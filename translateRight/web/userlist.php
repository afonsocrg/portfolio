<html>
<body>
    <h3>Users</h3>
    <?php
        require "db.php";
        try {
            $db = connect_db();

            $sql = "SELECT email, password FROM utilizador;";

            $result = $db->prepare($sql);
            $result->execute();

            echo("<table>\n");
            foreach($result as $row) {
                echo("<tr>\n");
                echo("<td>{$row['email']}</td>\n");
                echo("<td>{$row['password']}</td>\n");
                echo("</tr>\n");
            }
            echo("</table>\n");

            $db = null;
        } catch(PDOException $e) {
            echo("<p>ERROR: {$e->getMessage()}</p>");
        }

    ?>        
    <br>
    <a href="index.html">Voltar</a>
</body>
</html>
