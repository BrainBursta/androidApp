<?php
require "connection.php";
$user_name = $_POST["tunnus"];//"j00nas";
$password = $_POST["salasana"];//"salasana1";
error_reporting(0);

$sql_query = "select `nimi`,`info` from users where tunnus like '$user_name' and salasana like '$password';";
$result = mysqli_query($conn,$sql_query);

if (mysqli_num_rows($result)>0) {
	$row = mysqli_fetch_assoc($result);
	$nimi = $row["nimi"];
	$info = $row["info"];
	echo $nimi.":".$info;
	
}
else {
	
	$msg = "ERROR";
	print $msg;
}

?>

