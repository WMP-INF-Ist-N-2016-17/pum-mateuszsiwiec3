<?php

if($_SERVER['REQUEST_METHOD']=='POST'){

    $con = mysqli_connect("localhost", "id8546313_admin", "start123", "id8546313_gocarts");

    $orderId = $_POST['id'];

    $Sql_Query = "DELETE FROM orders WHERE id = '$orderId'";

    if(mysqli_query($con,$Sql_Query))
    {
        echo 'Order deleted successfully!';
    }
    else
    {
        echo 'Something went wrong';
    }
}
mysqli_close($con);