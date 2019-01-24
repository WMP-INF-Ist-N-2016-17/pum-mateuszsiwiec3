<?php

if($_SERVER['REQUEST_METHOD']=='POST'){

    $con = mysqli_connect("localhost", "id8546313_admin", "start123", "id8546313_gocarts");

    $orderId = $_POST['id'];
    $price = $_POST['price'];
    $track = $_POST['track'];
    $startDate = $_POST['startDate'];
    $duration = $_POST['duration'];
	$numberOfParticipants = $_POST['numberOfParticipants'];

    $Sql_Query = "UPDATE orders SET price = '$price' , track = '$track', startDate = '$startDate', duration = '$duration', numberOfParticipants = '$numberOfParticipants' WHERE id = $orderId";

    if(mysqli_query($con,$Sql_Query))
    {
        echo 'Order updated successfully!';
    }
    else
    {
        echo 'Something went wrong';
    }
}
mysqli_close($con);
?>