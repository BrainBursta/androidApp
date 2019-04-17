<?php
require "connection.php";
$user_name = "j00nas";
$password = "salasana1";

$sql_query = "select nimi from users where tunnus like '$user_name' and salasana like '$password';";
$result = mysqli_query($conn,$sql_query);

if (mysqli_num_rows($result)>0) {
	$row = mysqli_fetch_assoc($result);
	$nimi = $row["nimi"];
	echo "<h4>Terve ".$nimi."</h4>";
}
else {
	echo "Tunnusta ei löydy..";
}
?>