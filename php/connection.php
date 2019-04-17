<?php 
$servername = "127.0.0.1";
$username = "admin";
$password = "admin";
$dbname = "mobiiliohjelmointi";

error_reporting(0); //php errorit pois
$conn = mysqli_connect($servername, $username, $password, $dbname);

if (!$conn)
{
	//die("Virhe: ". mysqli_connect_error());
}
else {
	//echo "Tietokanta yhteys luotu..";
}

// TOIMIIII: http://webd.savonia.fi/www/kt53860/test/connection.php
?>