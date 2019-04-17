<?php 
require "connection.php";
$id = "";
$name = $_POST["nimi"];
$user_name = $_POST["tunnus"];
$password = $_POST["salasana"];
$info = $_POST["info"];


$sql_query = "insert into users values('$id','$name','$user_name','$password','$info'); ";

if(mysqli_query($conn, $sql_query))
{
	//echo "<h4>Tunnus luotu onnistuneesti..</h4>";
}
else {
	//echo "Virhe: ". mysqli_error($conn);
}


?>
