<?php
if($_SERVER['REQUEST_METHOD']=='POST'){

   $con = mysqli_connect("localhost", "id8546313_admin", "start123", "id8546313_gocarts");

    $userId = $_POST['id'];
    $price = $_POST['price'];
    $track = $_POST['track'];
    $startDate = $_POST['startDate'];
    $duration= $_POST['duration'];
    $numberOfParticipants = $_POST['numberOfParticipants'];

    $Sql_Query = "INSERT INTO orders (userId, price, track, startDate, duration, numberOfParticipants) values ('$userId','$price','$track', '$startDate', '$duration', '$numberOfParticipants')";

    if(mysqli_query($con,$Sql_Query))
    {
        echo 'Order Registered Successfully';
    }
    else
    {
        echo 'Something went wrong';
    }
}
mysqli_close($con);
?>