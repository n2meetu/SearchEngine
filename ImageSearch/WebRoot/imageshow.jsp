<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	request.setCharacterEncoding("utf-8");
	response.setCharacterEncoding("utf-8");
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	String imagePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<script type="text/javascript" src="<%=basePath%>js/animate.js"></script>
	<title>无标题文档</title>
	<style type="text/css">
		<!--
		#Layer1 {
			position:absolute;
			left:28px;
			top:26px;
			width:649px;
			height:32px;
			z-index:1;
		}
		#Layer2 {
			position:absolute;
			left:29px;
			top:82px;
			width:648px;
			height:602px;
			z-index:2;
		}
		#Layer3 {
			position:absolute;
			left:28px;
			top:697px;
			width:652px;
			height:67px;
			z-index:3;
		}
		-->
	</style>
</head>

<body>
<%
	String currentQuery=(String) request.getAttribute("currentQuery");
	int currentPage=(Integer) request.getAttribute("currentPage");
%>
<img height="100" src="<%=basePath%>sources/logo.png" width="175" style="margin-top: 5px;vertical-align: middle;">
<div id="Layer1" style="padding-left: 150px;margin-top:20px;">
	<form id="form1" name="form1" method="get" action="ImageServer">
		<label>
			<input name="query" value="<%=currentQuery%>" id="query" autocomplete="off" type="text" style="-webkit-box-sizing: border-box;height: 30px;width: 400px;overflow: hidden;padding: 5px 0px 0;vertical-align: middle"/>
			<button type="submit" name="Submit" style="display: none"></button>
			<img class="microphone" onclick="startDictation(this,event,'query')" src="https://i.imgur.com/cHidSVu.gif" style="width: 30px; height: 30px; padding-right: 9px;vertical-align: middle;right:-30px;right: 240px;position: absolute;"/>
			<button type="submit" name="Submit" style="margin-left:10px;font-family: arial,sans-serif;border: 1px solid #c6c6c6;box-shadow: 0 1px 1px rgba(0,0,0,0.1);height: 30px;width: 100px;color: #222;vertical-align: middle"><a>Search IT!</a></button>
		</label>
	</form>
</div>

<div id="Layer2" style="padding-left: 150px;top: 82px; height: 585px;">

	<div id = "SuggestionField">
		<%


			String[] suggestions = (String[])request.getAttribute("suggestions");
			if(suggestions!=null && suggestions.length>0)
			{
		%><p>您要找的是不是:<%
		for(int i=0;i < suggestions.length; ++i)
		{
	%>
		<a href="<%=basePath%>servlet/ImageServer?query=<%=suggestions[i] %>"><%=suggestions[i] %></a>
		<%
				};
			}

		%></p><%

	%>
	</div>

	<div id="imagediv">结果显示如下：
		<br>
		<Table style="left: 0px; width: 594px;">
			<%

				String[] imgTags=(String[]) request.getAttribute("imgTags");
				String[] imgPaths=(String[]) request.getAttribute("imgPaths");
				String[] highlightTags = (String[]) request.getAttribute("highlightTags");

				if(imgTags!=null && imgTags.length>0)
				{
					for(int i=0;i<imgTags.length;i++)
					{
			%>
			<p>
				<tr><h3><%=(currentPage-1)*10+i+1%>. <%=highlightTags[i] %></h3></tr>
				<tr><img src="<%=imagePath+imgPaths[i]%>" alt="<%=imagePath+imgPaths[i]%>" width=200 height=100 /></tr>
			</p>
			<%
				};
			%>
			<%}else{ %>
			<p><tr><h3>no such result</h3></tr></p>
			<%}; %>
		</Table>
	</div>
	<div>
		<p>
			<%if(currentPage>1){ %>
			<a href="ImageServer?query=<%=currentQuery%>&page=<%=currentPage-1%>">上一页</a>
			<%}; %>
			<%for (int i=Math.max(1,currentPage-5);i<currentPage;i++){%>
			<a href="ImageServer?query=<%=currentQuery%>&page=<%=i%>"><%=i%></a>
			<%}; %>
			<strong><%=currentPage%></strong>
			<%for (int i=currentPage+1;i<=currentPage+5;i++){ %>
			<a href="ImageServer?query=<%=currentQuery%>&page=<%=i%>"><%=i%></a>
			<%}; %>
			<a href="ImageServer?query=<%=currentQuery%>&page=<%=currentPage+1%>">下一页</a>
		</p>
	</div>
</div>
<div id="Layer3" style="top: 839px; left: 27px;">

</div>
<div>
</div>
</body>
