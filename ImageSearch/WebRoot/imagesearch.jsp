<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
request.setCharacterEncoding("utf-8");
System.out.println(request.getCharacterEncoding());
response.setCharacterEncoding("utf-8");
System.out.println(response.getCharacterEncoding());
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
System.out.println(path);
System.out.println(basePath);
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>THU Search</title>

        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
        <script type="text/javascript" src="js/jquery.min.js"></script>
        <link type="text/css" href="css/font_google.css" rel="stylesheet" type="text/css"/> 
        <link type="text/css" href="css/style_engine.css" rel="stylesheet" /> 
        <link rel="shortcut icon" href="http://www.tsinghua.edu.cn/publish/newthu/images/favicon.ico" />
        <script type="text/javascript" src="js/animate.js"></script>
    </head>

    <body>
        <form  method="get" action="servlet/ImageServer">
            <div class="search-wrapper">
                <div class="input-holder">
                    <input type="text" class="search-input" name="query" id = "transcript" placeholder="Type to search" />
                    <!-- <img class="microphone" onclick="alert()" src="https://i.imgur.com/cHidSVu.gif"/> -->
                    <button class="search-icon" onclick="searchToggle(this, event);">
                        <span></span>
                        <img class="microphone" onclick="startDictation(this,event)" src="https://i.imgur.com/cHidSVu.gif"/>
                    </button>
                </div>
                <span class="close" onclick="searchToggle(this, event);"></span>
                <div class="result-container">

                </div>
            </div>
        </form>
    </body>

</html>
