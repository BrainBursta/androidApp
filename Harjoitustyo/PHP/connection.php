<?php 
$servername = "localhost";
$username = "root";
$password = "root";
$dbname = "mobiiliohjelmointi";

$conn = mysqli_connect($servername, $username, $password, $dbname);

if (!$conn)
{
	//die("Virhe: ". mysqli_connect_error());
}
else {
	//echo "<h4>Tietokanta yhteys luotu!</h4>";
}
?>