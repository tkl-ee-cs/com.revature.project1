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
  <a class="" href="#">
    <img src="img/logo.png" alt="" style="width:40px;">
  </a>
  <ul class="navbar-nav">
    <li class="nav-item"><a class="nav-link" href="#contact">Contact</a></li>
    <li class="nav-item"><a class="nav-link" href="#about">About</a></li>
  </ul>
    <ul class="navbar-nav ml-auto">
    <li class="nav-item"><a class="nav-link" href="/Project01">Log Out</a></li>
  </ul>
</nav>


<div id="main-content" class="container-float p-3 my-1 bg-secondary text-white">
	<h2>Welcome back, ${uname} please make a selection to get started?</h2>
	<div id="customer-dropdown" class="dropdown">
	  <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
	    Select an Option
	  </button>
	  <div class="dropdown-menu">
	    <a class="dropdown-item" href="#">View A Full User Profile</a>
	    <a class="dropdown-item" href="#">Approve Pending User Request</a>
	    <a class="dropdown-item" href="#">Approve Banking Account Application</a>
	    <a class="dropdown-item" href="#">View Transaction Logs</a>
	  </div>
	</div>
	
	<hr>
	
	<form id="view-user" action="" style="display: none">
	<h5>View A User Account</h5>
	  <div class="form-group">
	    <label for="view-user-account">Enter User Name:</label>
	    <input type="text" class="form-control" placeholder="Enter Username:" id="view-user-account" name="view-user-account">
	  </div>
	  <button type="submit" class="btn btn-primary">Submit</button>
	</form>
	
	<form id="approve-user" action="" style="display: none">
	<h5>Approve User Request</h5>
	  <div class="form-group">
	    <label for="approve-user-account">Enter User Name:</label>
	    <input type="text" class="form-control" placeholder="Enter Username:" id="approve-user-account" name="approve-user-account">
	  </div>
	  <div class="form-group">
	  <label for="approve-reject-select">Select action for the account:</label>
	  <select class="form-control" id="approve-reject-select" name="approve-reject-select">
	    <option>Approve</option>
	    <option>Reject</option>
	  </select>
	  </div>
	  <button type="submit" class="btn btn-primary">Submit</button>
	</form>
	
	<form id="approve-account" action="" style="display: none">
	<h5>Approve Bank Account</h5>
	  <div class="form-group">
	    <label for="approve-bank-account">Enter User Name:</label>
	    <input type="text" class="form-control" placeholder="Enter Username:" id="approve-bank-account" name="approve-bank-account">
	  </div>
	  <div class="form-group">
	  <div class="form-group">
	  <label for="approve-bank-account-select">Select an account:</label>
	  <select class="form-control" id="approve-bank-account-select" name="approve-bank-account-select">
	    <option>Account 1</option>
	    <option>Account 2</option>
	    <option>Account 3</option>
	    <option>...</option>
	  </select>
	  </div>
	  <label for="approve-reject-bank">Select action for the account:</label>
	  <select class="form-control" id="approve-reject-bank" name="approve-reject-bank">
	    <option>Approve</option>
	    <option>Reject</option>
	  </select>
	  </div>
	  <button type="submit" class="btn btn-primary">Submit</button>
	</form>
	
	<div class="container-fluid"  id="view-transactions" name="view-transactions" style="display: none">
	  <p>Transactions</p>            
	  <table class="table table-dark table-hover">
	    <thead>
	      <tr>
	        <th>Timestamp</th>
	        <th>Source Account</th>
	        <th>Destination Account</th>
	        <th>Action</th>
	        <th>Amount</th>
	        <th>Status</th>
	      </tr>
	    </thead>
	    <tbody>
	      <tr>
	       	<td>Timestamp</td>
	        <td>Source Account</td>
	        <td>Destination Account</td>
	        <td>Action</td>
	        <td>Amount</td>
	        <td>Status</td>
	      </tr>
	      <tr>
	       	<td>Timestamp</td>
	        <td>Source Account</td>
	        <td>Destination Account</td>
	        <td>Action</td>
	        <td>Amount</td>
	        <td>Status</td>
	      </tr>
	      <tr>
	       	<td>Timestamp</td>
	        <td>Source Account</td>
	        <td>Destination Account</td>
	        <td>Action</td>
	        <td>Amount</td>
	        <td>Status</td>
	      </tr>
	    </tbody>
	  </table>
	</div>
	
</div>
</body>
</html>