<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
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

<div id="main-content" class="container-float h-100 p-3 my-1 bg-secondary text-white">
	<h2>Welcome, ${uname} how may we be of assistance today?</h2>
	<div id="customer-dropdown" class="dropdown">
	  <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
	    Select an Option
	  </button>
	  <div class="dropdown-menu">
	    <a class="dropdown-item" href="#">Apply For New Banking Account</a>
	    <a class="dropdown-item" href="#">View An Account</a>
	    <a class="dropdown-item" href="#">Deposit Funds Into An Account</a>
	    <a class="dropdown-item" href="#">Withdraw From An Account</a>
	    <a class="dropdown-item" href="#">Post Transfer Request To An Account</a>
	    <a class="dropdown-item" href="#">Accept Transfer Request From An Account</a>
	  </div>
	</div>
	
	<hr>
	
		<h2>${tname}</h2>
		<!-- https://stackoverflow.com/questions/18085476/how-to-display-a-list-in-a-jsp-file -->
 	<table class="table table-dark table-striped"> 
 	<tr>
 	<th>${headone}</th>
 	<th>${headtwo}</th>
 	</tr>
		<c:forEach items="${tlist}" var="item">
		    <tr><!-- https://www.zentut.com/jsp-tutorial/formatting-number-with-fmtformatnumber-action/ -->
		      <td>Account #<c:out value="${item.account_id}" /></td>
		      <td><c:set var="val" value="${item.balance}" />
        		<fmt:setLocale value="en_US"/>
        		<fmt:formatNumber value="${val}" type="currency"/>
        	  </td>
		    </tr>
	  	</c:forEach>
	</table>
	 
	
	<form id="apply-user" action="" style="display: none">
	<h5>Apply To Be a Customer</h5>
	  <div class="form-group">
	    <label for="apply-user-account">User Name:</label>
	    <input type="text" class="form-control" placeholder="Enter Username:" id="apply-user-account" name="apply-user-account">
	  </div>
	  <div class="form-group">
	    <label for="apply-user-pasword">Password:</label>
	    <input type="text" class="form-control" placeholder="Enter Username:" id="apply-user-pasword" name="apply-user-pasword">
	  </div>
	  <button type="submit" class="btn btn-primary">Submit</button>
	</form>
	
	<form id="apply-account" action="" style="display: none">
	<h5>Apply For A Banking Account</h5>
	  <div class="form-group">
	    <label for="starting-balance">Balance:</label>
	    <input type="number" min="0.01" step="0.01" class="form-control" placeholder="Enter starting balance..." id="starting-balance" name="starting-balance">
	  </div>
	  <button type="submit" class="btn btn-primary">Submit</button>
	</form>
	
	<form id="view-account" action="" style="display: none">
	<h5>View An Account</h5>
	  <div class="form-group">
	  <label for="view-account-select">Select an account:</label>
	  <select class="form-control" id="view-account-select" name="view-account-select">
	    <option>Account 1</option>
	    <option>Account 2</option>
	    <option>Account 3</option>
	    <option>...</option>
	  </select>
	  </div>
	  <button type="submit" class="btn btn-primary">Submit</button>
	</form>
	
	<form id="deposit-account" action="" style="display: none">
	<h5>Deposit Funds Into Account</h5>
	  <div class="form-group">
	  <label for="deposit-account-select">Select an account:</label>
	  <select class="form-control" id="deposit-account-select" name="deposit-account-select">
	    <option>Account 1</option>
	    <option>Account 2</option>
	    <option>Account 3</option>
	    <option>...</option>
	  </select>
	  </div>
	  <div class="form-group">
	    <label for="deposit-amount">Balance:</label>
	    <input type="number" min="0.01" step="0.01" class="form-control" placeholder="Enter an amount..." id="deposit-amount" name="deposit-amount">
	  </div>
	  <button type="submit" class="btn btn-primary">Submit</button>
	</form>
	
	<form id="withdraw-account" action="" style="display: none">
	<h5>Withdraw Funds From Account</h5>
	  <div class="form-group">
	  <label for="withdraw-account-select">Select an account:</label>
	  <select class="form-control" id="withdraw-account-select" name="withdraw-account-select">
	    <option>Account 1</option>
	    <option>Account 2</option>
	    <option>Account 3</option>
	    <option>...</option>
	  </select>
	  </div>
	  <div class="form-group">
	    <label for="withdraw-amount">Balance:</label>
	    <input type="number" min="0.01" step="0.01" class="form-control" placeholder="Enter an amount..." id="withdraw-amount" name="withdraw-amount">
	  </div>
	  <button type="submit" class="btn btn-primary">Submit</button>
	</form>
	
	<form id="post-transfer" action="" style="display: none">
	<h5>Post Transfer Request</h5>
	  <div class="form-group">
	    <label for="transfer-user">User Name:</label>
	    <input type="text" class="form-control" placeholder="Enter Username:" id="transfer-user" name="transfer-user">
	  </div>
	  <div class="form-group">
	  <label for="post-transfer-select">Select an account from User:</label>
	  <select class="form-control" id="post-transfer-select" name="post-transfer-select">
	    <option>Account 1</option>
	    <option>Account 2</option>
	    <option>Account 3</option>
	    <option>...</option>
	  </select>
	  </div>
	  <div class="form-group">
	    <label for="transfer-amount">Balance:</label>
	    <input type="number" min="0.01" step="0.01" class="form-control" placeholder="Enter an amount..." id="transfer-amount" name="transfer-amount">
	  </div>
	  <button type="submit" class="btn btn-primary">Submit</button>
	</form>
	
	<form id="accept-transfer" action="" style="display: none">
	<h5>Accept Transfer Request</h5>
	  <div class="form-group">
	  <label for="accept-transfer-select">Select an account:</label>
	  <select class="form-control" id="accept-transfer-select" name="accept-transfer-select">
	    <option>Account 1</option>
	    <option>Account 2</option>
	    <option>Account 3</option>
	    <option>...</option>
	  </select>
	  </div>
	  <div class="form-group">
	  <label for="accept-decline-select">Select action for the account:</label>
	  <select class="form-control" id="accept-decline-select" name="accept-decline-select">
	    <option>Accept</option>
	    <option>Decline</option>
	  </select>
	  </div>
	  <button type="submit" class="btn btn-primary">Submit</button>
	</form>
</div>

<!--  
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
-->

</body>
</html>