<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>View ${username} Details</title>
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
<h2>Profile Details for ${username}</h2>
	<div id="main-content" class="container p-3 my-1 bg-secondary text-white">
	 	<table class="table table-dark table-striped"> 
	 	<tr>
		 	<th>Account ID</th>
		 	<th>UserName</th>
		 	<th>User Type</th>
		 	<th>User Status</th>
	 	</tr>
	 	<tr>
			<td>${userid}</td>
			<td>${username}</td>
			<td>${type}</td>
			<td>${status}</td>
		</tr>
		</table>
	</div>
 	<c:choose>
 	<c:when test="${type == 'customer'}">
 	<table class="table table-dark table-striped"> 
	 	<tr>
	 	<th>Bank Account Number</th>
	 	<th>Account Status</th>
	 	<th>Balance</th>
	 	</tr>
		<c:forEach items="${accts}" var="item">
		    <tr>
		      <td>Account #<c:out value="${item.account_id}" /></td>
		      <td><c:out value="${item.status}" /></td>
		      <td><c:set var="val" value="${item.balance}" />
        		<fmt:setLocale value="en_US"/>
        		<fmt:formatNumber value="${val}" type="currency"/>
        	  </td>
		    </tr>
	  	</c:forEach>
	</table>
	</c:when>
	</c:choose>
	<form id="return-view" action="welcome">
	  <button type="submit" class="btn btn-primary">Return</button>
	</form>
	</div>
</body>
</html>