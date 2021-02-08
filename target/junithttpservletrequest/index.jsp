<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Welcome to Revature Bank.</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</head>
<body>

  <!-- Links -->
<nav class="navbar navbar-expand-sm bg-secondary navbar-dark">
  <!-- Brand/logo -->
  <img src="img/logo.png" alt="" style="width:40px;">
  <ul class="navbar-nav">
    <li class="nav-item"><a class="nav-link" href="#contact">Contact</a></li>
    <li class="nav-item"><a class="nav-link" href="#about">About</a></li>
  </ul>
</nav>

<div id="carousel-images" class="carousel carousel-fade bg-dark h-100" data-ride="carousel">
	<!-- The slideshow -->
	<div class="carousel-inner">
	  <div class="carousel-item active">
	    <img src="img/revature.jpg" alt="revature" class="img-fluid mx-auto d-block" style="max-height:300px !important;">
	  </div>
	  <div class="carousel-item">
	    <img src="img/handshake.jpg" alt="handshake" class="img-fluid mx-auto d-block" style="max-height:300px !important;">
	  </div>
	</div>
</div>

<div id="main-content" class="container-float p-3 bg-secondary text-white">
	<div id="title-for-page">
		<h1>Welcome to Revature Bank</h1>
		<h2>Located in the city of Trenton, NJ.</h2>
	</div>
	
	<h6 class="bg-info text-white">${info}</h6>
    <h6 class="bg-success text-white">${success}</h6>
    <h6 class="bg-warning text-white">${warning}</h6>
	<h6 class="bg-danger text-white">${danger}</h6>
	    
	<form id="login-form" action="login" method="post">
	  <h4>Log In</h4>
	  <div class="form-group">
	    <label for="login-name">Username:</label>
	    <input type="text" class="form-control" placeholder="Enter Username" id="login-name" name="login-name" autocomplete="on" required>
	  </div>
	  <div class="form-group">
	    <label for="login-pw">Password:</label>
	    <input type="password" class="form-control" placeholder="Enter password" id="login-pw" name="login-pw" required>
	  </div>
	  <button type="submit" class="btn btn-primary" value="Submit">Submit</button>
	</form>
	
	<br/>
	<button type="submit" class="btn btn-primary" onclick="showHide()">Create New Account</button>
	<hr>
	
	<form style="display: none" id="new-user-form" action="newaccount" method="post">
	<h4>New Account</h4>
	  <div class="form-group">
	    <label for="new-name">Username:</label>
	    <input type="text" class="form-control" placeholder="Enter Username" id="new-name" name="new-name" autocomplete="on" required>
	  </div>
	  <div class="form-group">
	    <label for="new-pw">Password:</label>
	    <input type="password" class="form-control" placeholder="Enter password" id="new-pw" name="new-pw" required>
	  </div>
  	  <div class="form-group">
	    <label for=new-bal>Balance:</label>
	    <input type="number" min="0.01" step="0.01" class="form-control" placeholder="Enter an amount..." id="new-bal" name="new-bal">
	  </div>
	  <button type="submit" class="btn btn-primary">Submit</button>
	</form>
	
	<script>
		function showHide() {
		  var x = document.getElementById("new-user-form");
		  if (x.style.display === "none") {
		    x.style.display = "block";
		  } else {
		    x.style.display = "none";
		  }
		}
	</script>
</div>
</body>
</html>