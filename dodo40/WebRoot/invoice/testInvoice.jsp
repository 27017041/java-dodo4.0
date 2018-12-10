<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	String root_path = System.getProperty("evan.webapp");
%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta meta charset="UTF-8"">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" media="all" href="<%=basePath%>/css/invoicePdf/style.css" />
</head>
<body onload="out()">
	<img src="<%=basePath%>images/pdf/embraiz_logo.jpg" />
	<div class="box1">
		<h3>Invoice</h3>
		<table cellspacing="0" border="1" style="border-collapse:collapse; width: 600px;">
			<tr>
				<td>${title}</td>
				<td>Test</td>
				<td>Date</td>
				<td>Apr 16,2018</td>
			</tr>
			<tr>
				<td>Customer:</td>
				<td>Embraiz</td>
				<td>Invoice No</td>
				<td>I201804003</td>
			</tr>
			<tr>
				<td>Preoared By:</td>
				<td>admin</td>
				<td>Date</td>
				<td>2018</td>
			</tr>

			<tr>
				<td>Remarks By:</td>
				<td colspan="3">Remark:</td>
			</tr>
		</table>
	</div>

	<div class="box2">
		<h3>Test</h3>
		<table cellspacing="0" border="1"
			style="border-collapse:collapse; width: 600px;">
			<tr>
				<td style="background-color: greenyellow;">Item</td>
				<td style="background-color: greenyellow;">Description</td>
				<td style="background-color: greenyellow;">Unit Price</td>
				<td style="background-color: greenyellow;">Qty</td>
				<td style="background-color: greenyellow;">Discount</td>
				<td style="background-color: greenyellow;">Cost</td>
			</tr>

			<c:forEach var="quotationItem" items="${quotationItem}">
				<tr>
					<td>${quotationItem.getTitle()}</td>
					<td>${quotationItem.getDescription()}</td>
					<td>${quotationItem.getUnitPrice()}</td>
					<td>${quotationItem.getQty()}</td>
					<td>${quotationItem.getDiscount()}</td>
					<td>${quotationItem.getCost()}</td>
				</tr>
			</c:forEach>


			<tr>
				<td colspan="2">Original Cost: 2000.00</td>
				<td colspan="2">Discount: 5.50</td>
				<td colspan="2">Total Cost: SGD 1,994.50</td>
			</tr>
			<tr>
				<td colspan="6">100.00% of the total payment: 1,994.50</td>
			</tr>
		</table>
		<p>
			<b>Bank account information</b>
		</p>
		<p>Company name：EMBRAIZ BUSINESS SYSTEM LIMITED</p>
		<p>Bank account：239-403942-883</p>
		<p>Bank name: HANG SENG BANK</p>
		<p>Swift code: HASEHKHH</p>
		<p>Bank address: 83 Des Voeux Road Central (Head Office*)</p>
		<p>Embraiz address: Unit 1902, 19/F, Elite Centre, 22 Hung To
			Road, Kwun Tong, Kowloon, Hong Kong</p>

		<p>公司名称： 广州缪斯软件科技有限公司</p>
		<p>开户行： 招商银行股份有限公司广州盈隆广场支行</p>
		<p>开户行地址： 广州市天河区黄埔大道西76号富力盈隆广场首层</p>
		<p>帐号： 1209 0627 4110 101</p>

	</div>


	<!-- 第一个页脚 -->
	<div class="page-break">
		<div class='footer'>
			<hr></hr>
			<div class="page-count">
				Page <span id="pagenumber"></span> of <span id="pagecount"></span>
			</div>
		</div>
	</div>



	<!-- 下一个版面 -->
	<div class="next-page">
		<img src="<%=basePath%>/images/pdf/embraiz_logo.jpg" />
		<div class="box3">
			<p>For and on behalf of</p>
			<p>By EMBRAIZ BUSINESS SYSTEM LIMITED</p>
			<img src="<%=basePath%>/images/pdf/sig_new.jpg" width="200px" />
			<p>Shu Lok</p>
			<p>Date:Apr 20,2018</p>
		</div>
	</div>


</body>
</html>