<?php
require "connection.php";
$newPW = $_POST["uusiPW"];
$user_name = $_POST["tunnus"];
$oldPW = $_POST["vanhaPW"];

$sql_query = "update users set salasana='$newPW' where tunnus like '$user_name' and salasana like '$oldPW';";

if(mysqli_query($conn, $sql_query))
{
	echo "Salasana vaihdettu";
}
else {
	echo "Virhe: ". mysqli_error($conn);
}
