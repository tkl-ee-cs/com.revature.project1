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

<div id="main-content" class="container-float h-100 p-3 my-1 bg-secondary text-white">
	<h2>Welcome, ${uname} how may we be of assistance today?</h2>
	<h6 class="bg-info text-white">${info}</h6>
    <h6 class="bg-success text-white">${success}</h6>
    <h6 class="bg-warning text-white">${warning}</h6>
	<h6 class="bg-danger text-white">${danger}</h6>
	<hr>
		<h2>${titlename}</h2>
		<!-- https://stackoverflow.com/questions/18085476/how-to-display-a-list-in-a-jsp-file -->
 	<table class="table table-dark table-striped"> 
 	<tr>
 	<th>${headone}</th>
 	<th>${headtwo}</th>
 	<th>${headthree}</th>
 	</tr>
		<c:forEach items="${tlist}" var="item">
		    <tr><!-- https://www.zentut.com/jsp-tutorial/formatting-number-with-fmtformatnumber-action/ -->
		      <td>Account #<c:out value="${item.account_id}" /></td>
		      <td><c:out value="${item.status}" /></td>
		      <td><c:set var="val" value="${item.balance}" />
        		<fmt:setLocale value="en_US"/>
        		<fmt:formatNumber value="${val}" type="currency"/>
        	  </td>
		    </tr>
	  	</c:forEach>
	</table>
	<hr>
	<div id="customer-dropdown" class="dropdown">
	<button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
	    Select an Option
	</button> 
	<ul class="dropdown-menu" role="menu"> <!-- dropdowns are ul in Boostrap -->
		<li><a href="#apply-account" role="menuitem" id="appl">Apply For A Banking Account</a></li>
		<li><a href="#deposit-account" role="menuitem" id="dpst">Deposit Funds Into An Account</a></li>
		<li><a href="#withdraw-account" role="menuitem" id="wthd">Withdraw From An Account</a></li>
		<li><a href="#view-account" role="menuitem" id="view">View A Banking Account</a></li>
		<li><div class="dropdown-divider"></div><li>
		<li><a href="#post-transfer" role="menuitem" id="post">Post Transfer Request To An Account</a></li>
		<li><a href="#accept-transfer" role="menuitem" id="acpt">Accept Transfer Request From Account </a></li>
	</ul>
	</div><br/>

	<form id="#apply-account" action="apply" style="display: none">
	<h5>Apply For A Banking Account</h5>
	  <div class="form-group">
	    <label for="starting-balance">Balance:</label>
	    <input type="number" min="0.01" step="0.01" class="form-control" placeholder="Enter starting balance..." id="starting-balance" name="starting-balance">
	  </div>
	  <button type="submit" class="btn btn-primary">Submit</button>
	</form>
	
	<form id="#view-account" action="" style="display: none">
	<h5>View An Account</h5>
	  <div class="form-group">
	  <label for="view-account-select">Select an account:</label>
	  <select class="form-control" id="view-account-select" name="view-account-select" onchange="viewPopUp(this.value);">
	    <c:forEach items="${alist}" var="item">
			<option value="${item.account_id}"><c:out value="${item.account_id}" /></option>
	  	</c:forEach>
	  </select>
	  </div>
	  <button type="button" id="view-account" class="btn btn-info btn-lg" data-toggle="modal" data-target="#myModal">View</button>
	  
    <!-- Modal -->
	<div class="modal fade" id="myModal" role="dialog">
		<div class="modal-dialog">
			<div class="modal-content"><!-- Modal content-->
			        <div class="modal-header">
			          <h4 class="modal-title text-dark">Account Details</h4>
			        </div>
		        <div class="modal-body">
		        	<table class="table table-dark table-striped" id="selectbox">
		        	<thead>
		            	<tr>
			                <th>Account Number</th>
			                <th>Status</th>
			                <th>Balance</th>
		                </tr>
		        	</thead>
		        	<tbody>
						<c:forEach items="${alist}" var="item">
						    <tr id="${item.account_id}" class="popup" style="display: none">
						      <td ><c:out value="${item.account_id}" /></td>
						      <td><c:out value="${item.status}" /></td>
						      <td><c:set var="val" value="${item.balance}" />
				        		<fmt:setLocale value="en_US"/>
				        		<fmt:formatNumber value="${val}" type="currency"/>
				        	  </td>
						    </tr>
					  	</c:forEach>
					  	</tbody>
					</table>
				</div>
				<div class="modal-footer"><button type="button" class="btn btn-default" data-dismiss="modal">Close</button></div>
			</div>
		</div>
	</div>
	</form>
	
	<form id="#deposit-account" action="deposit" style="display: none">
	<h5>Deposit Funds Into Account</h5>
	  <div class="form-group">
	    <label for="deposit-account-select">Input Bank Account:</label>
	    <input type="number" min="0" step="1" class="form-control" placeholder="Enter bank account number..." id="deposit-account-select" name="deposit-account-select">
	  </div>
	  <div class="form-group">
	    <label for="deposit-amount">Deposit:</label>
	    <input type="number" min="0.01" step="0.01" class="form-control" placeholder="Enter an amount..." id="deposit-amount" name="deposit-amount">
	  </div>
	  <button type="submit" class="btn btn-primary">Submit</button>
	</form>
	
	<form id="#withdraw-account" action="withdraw" style="display: none">
	<h5>Withdraw Funds From Account</h5>
	  <div class="form-group">
	    <label for="withdraw-account-select">Input Bank Account:</label>
	    <input type="number" min="0" step="1" class="form-control" placeholder="Enter bank account number..." id="withdraw-account-select" name="withdraw-account-select">
	  </div>
	  <div class="form-group">
	    <label for="withdraw-amount">Balance:</label>
	    <input type="number" min="0.01" step="0.01" class="form-control" placeholder="Enter an amount..." id="withdraw-amount" name="withdraw-amount">
	  </div>
	  <button type="submit" class="btn btn-primary">Submit</button>
	</form>
	
	<form id="#post-transfer" action="transfer" style="display: none">
	<h5>Post Transfer Request</h5>
	  <div class="form-group">
	    <label for="transfer-dest-account">Destination Account: </label>
	    <input type="number" min=1 class="form-control" placeholder="Enter a destination account number..." id="transfer-dest-account" name="transfer-dest-account">
	  </div>
	  <div class="form-group">
	  <label for="post-transfer-select">Select an account:</label>
	  <select class="form-control" id="post-transfer-select" name="post-transfer-select">
	    <c:forEach items="${tlist}" var="item">
			<option value="${item.account_id}"><c:out value="${item.account_id}" /></option>
	  	</c:forEach>
	  </select>
	  </div>
	  <div class="form-group">
	    <label for="transfer-amount">Balance:</label>
	    <input type="number" min="0.01" step="0.01" class="form-control" placeholder="Enter an amount..." id="transfer-amount" name="transfer-amount">
	  </div>
	  <button type="submit" class="btn btn-primary">Submit</button>
	</form>
	
	<form id="#accept-transfer" action="accept" style="display: none">
	<h5>Accept Transfer Request</h5>
	  <div class="form-group">
	  <label for="accept-transfer-select">Select Request:</label>
	  <select class="form-control" id="accept-transfer-select" name="accept-transfer-select">
	    <c:forEach items="${plist}" var="item">
			<option value="${item.transaction_id}">
			${from} <c:out value="${item.src}" /> 
			${to} <c:out value="${item.dst}" /> 
			${pay} <c:set var="val" value="${item.amount}" /><fmt:setLocale value="en_US"/><fmt:formatNumber value="${val}" type="currency"/>
			</option>
	  	</c:forEach>
	  </select>
	  </div>
	  <div class="form-group">
	  <label for="accept-decline-select">Select action for the account:</label>
	  <select class="form-control" id="accept-decline-select" name="accept-decline-select">
	    <option value="accept">Accept</option>
	    <option value="decline">Decline</option>
	  </select>
	  </div>
	  <button type="submit" class="btn btn-primary">Submit</button>
	</form>
</div>
 
<script>
function viewPopUp(curEl){ // controls the rows of the View Accounts section
	   var elements = document.getElementsByClassName("popup");
		console.log(elements);
		console.log(curEl);
	    for (var i = 0, len = elements.length; i < len; i++ ) {
	      var sel = elements[i];
	      if (curEl == sel.id) {
	         sel.style.display = ""; 
	      } else {
	         sel.style.display = "none"; 
	      }
	    }  
};
</script>
<script>
$(".dropdown-menu li a").click( function() {
   var elements = this.parentNode.parentNode.getElementsByTagName("a"); 
   viewPopUp(document.getElementById("view-account-select").value); // first call to viewPopUp to initialize data
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
<script>
function() {
   var accept = this.parentNode.parentNode.getElementsByTagName("acpt"); 
   viewPopUp(document.getElementById("view-account-select").value); // first call to viewPopUp to initialize data
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
</body>
</html>