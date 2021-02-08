<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Welcome ${uname}</title>
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
    <li class="nav-item"><a class="nav-link" href="logout">Log Out</a></li>
  </ul>
</nav>


<div id="main-content" class="container-float p-3 my-1 bg-secondary text-white">
	<h2>Welcome back, ${uname} please make a selection to get started?</h2>
	<h6 class="bg-info text-white">${info}</h6>
    <h6 class="bg-success text-white">${success}</h6>
    <h6 class="bg-warning text-white">${warning}</h6>
	<h6 class="bg-danger text-white">${danger}</h6>
	<hr>
	<div id="employee-dropdown" class="dropdown">
	  <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
	    Select an Option
	  </button>
	<ul class="dropdown-menu" role="menu"> <!-- dropdowns are ul in Boostrap -->
		<li><a href="#view-user" role="menuitem" id="prof">View A  User Profile</a></li>
		<li><a href="#approve-user" role="menuitem" id="user">Approve Pending Customer Request</a></li>
		<li><a href="#approve-account" role="menuitem" id="acnt">Approve Banking Account Application</a></li>
		<li><a href="#view-transactions" role="menuitem" id="trnx">View Transaction Logs</a></li>
	</ul>
	</div><br/>
	
	<form id="#view-user" action="view" style="display: none">
	<h5>View A User Account</h5>
	  <div class="form-group">
	    <label for="view-user-account">Enter User Name:</label>
	    <input type="text" class="form-control" placeholder="Enter Username:" id="view-user-account" name="view-user-account">
	  </div>
	  <button type="submit" class="btn btn-primary">Submit</button>
	</form>
	
	<form id="#approve-user" action="approve-user" style="display: none">
	<h5>Approve User Request</h5>
	  <div class="form-group">
	  <label for="approve-user-account">Select User Account:</label>
	  <select class="form-control" id="approve-user-account" name="approve-user-account">
	    <c:forEach items="${clist}" var="citem">
			<option value="${citem.user_id}">
				<c:out value="${citem.user_id}" />
				:<c:out value="${citem.username}" />
			</option>
	  	</c:forEach>
	  </select>
	  </div>
	  <div class="form-group">
	  <label for="approve-reject-select">Select action for the account:</label>
	  <select class="form-control" id="approve-reject-select" name="approve-reject-select">
	    <option value="aprv">Approve</option>
	    <option value="rjct">Reject</option>
	  </select>
	  </div>
	  <button type="submit" class="btn btn-primary">Submit</button>
	</form>
	
	<form id="#approve-account" action="approve-account" style="display: none">
	<h5>Approve Bank Account</h5>
	  <div class="form-group">
	  <label for="approve-bank-select">Select Bank Account:</label>
	  <select class="form-control" id="approve-bank-select" name="approve-bank-select">
	    <c:forEach items="${ulist}" var="uitem">
			<option value="${uitem.account_id}">
				<c:out value="${uitem.account_id}" />
				:<c:set var="val" value="${uitem.balance}" />
	        		<fmt:setLocale value="en_US"/>
	        		<fmt:formatNumber value="${val}" type="currency"/>
			</option>
	  	</c:forEach>
	  </select>
	  </div>
	  <div class="form-group">
	  <label for="approve-reject-bank">Select action for the account:</label>
	  <select class="form-control" id="approve-reject-bank" name="approve-reject-bank">
	    <option value="aprv">Approve</option>
	    <option value="rjct">Reject</option>
	  </select>
	  </div>
	  <button type="submit" class="btn btn-primary">Submit</button>
	</form>
	
	<form class="container-fluid"  id="#view-transactions" action="logs" style="display: none">
	  <button type="submit" class="btn btn-primary">View Transactions Logs</button>     
	</form>
	
	<script>
$(".dropdown-menu li a").click( function() {
   var elements = this.parentNode.parentNode.getElementsByTagName("a");

    for (var i = 0, len = elements.length; i < len; i++ ) {
      var sel = document.getElementById($(elements[i]).attr("href"));
      if ($(this).attr("href") == sel.id) {
         sel.style.display = "block"; 
      } else {
         sel.style.display = "none"; 
      }
    }  
});
</script>
	
</div>
</body>
</html>