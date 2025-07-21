<%@page import="in.co.rays.controller.ORSView"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Welcome Page</title>
</head>
<body>

	<form action="<%=ORSView.WELCOME_CTL%>">
		<%@ include file="Header.jsp"%>
		<br> <br> <br>
		<h1 align="center">
			<font size="10px" color="navy">WELCOME TO ORS</font>
		</h1>

		<%
			UserBean beanUserBean = (UserBean) session.getAttribute("user");
			if (beanUserBean != null) {
				if (beanUserBean.getRoleId() == RoleBean.STUDENT) {
		%>

		<h2 align="Center">
			<a style="color: maroon" href="<%=ORSView.GET_MARKSHEET_CTL%>">Click
				here to see your Marksheet </a>
		</h2>

		<%
			}
			}
		%>

	</form>

	<%@ include file="Footer.jsp"%>
</body>
</html>