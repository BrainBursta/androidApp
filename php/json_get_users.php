<?php 
require "connection.php"
//$host = "127.0.0.1";
//$username = "admin";
//$password = "admin";
//$dbname = "mobiiliohjelmointi";

$sql ="select * from users;";

//$conn = mysqli_connect($host,$user,$password,$dbname);
$result = mysqli_query($conn,$sql);
$response = array();

while($row = mysqli_fetch_array($result))
{
	array_push($response,array("id"=>$row[0],"nimi"=>$row[1],"tunnus"=>$row[2],"salasana"=>$row[3],"info"=>$row[4]));
}
echo json_encode(array("server_response"=>$response));
mysqli_close($conn);

?>