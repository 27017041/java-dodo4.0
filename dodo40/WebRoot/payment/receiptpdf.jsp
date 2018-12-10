
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	String root_path = System.getProperty("evan.webapp");
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" media="all" href="<%=basePath%>/css/paymentPdf/payment.css" />
</head>
<body>
	<div class="box1">
		<img src="<%=basePath%>images/pdf/embraiz_logo.jpg" />
		<div class="box2">
				<p style="font-size: 25px;">Receipt</p>			
			<table cellspacing="0" border="1" class="table">
				<tr>
					<td class="table_tr_td1">Contact</td>
					<td class="table_tr_td2">${paymentItem.get(0).getContactName()}</td>
				</tr>
				<tr>
					<td class="table_tr_td1">Company</td>
					<td class="table_tr_td2">${paymentItem.get(0).getCompanyName()}</td>
				</tr>
				<tr>
					<td class="table_tr_td1">Receipt Date</td>
					<td class="table_tr_td2">${paymentItem.get(0).getDate()}</td>
				</tr>
				<tr>
					<td class="table_tr_td1">Invoice No</td>
					<td class="table_tr_td2">${paymentItem.get(0).getInvoiceNo()}</td>
				</tr>
				<tr>
					<td class="table_tr_td1">Quotation No</td>
					<td class="table_tr_td2">${paymentItem.get(0).getQuotationNo()}</td>
				</tr>
				<tr>
					<td class="table_tr_td1">Payment No</td>
					<td class="table_tr_td2">${paymentItem.get(0).getPaymentNo()}</td>
				</tr>
				<tr>
					<td class="table_tr_td1">Currency</td>
					<td class="table_tr_td2">${paymentItem.get(0).getCurrencyName()}</td>
				</tr>
				<tr>
					<td class="table_tr_td1">Payment</td>
					<td class="table_tr_td2">${payment}</td>
				</tr>
			</table>


		</div>

		<div class="box3">
			<p>For and on behalf of</p>
			<p>By Embraiz Ltd</p>
			<img src="<%=basePath%>images/pdf/sig_new.jpg"  width="150px" height="75px" />
			<br />
			<p style="font-size: 20;">${paymentItem.get(0).getUserName()}</p>


			<p>Date:${date}</p>
		</div>
		<div class="page-break">
			<div class='footer'>
				<div class="page-count">
					Page <span id="pagenumber"></span> of <span id="pagecount"></span>
				</div>
			</div>
		</div>


	</div>
	
	
	

</body>
</html>