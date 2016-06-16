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
        <link type="text/css" href="css/font_google.css" rel="stylesheet"/> 
        <link type="text/css" href="css/style_engine.css" rel="stylesheet" /> 
        <link type="text/css" href="css/autocomplete.css" rel="stylesheet" /> 
        <link rel="shortcut icon" href="http://www.tsinghua.edu.cn/publish/newthu/images/favicon.ico" />
        <script type="text/javascript" src="js/animate.js"></script>
        <script type="text/javascript" src="js/jquery.autocomplete.js"></script>
    </head>

    <body>
        <form id="Query" method="get" action="servlet/ImageServer">
            <div class="search-wrapper" id = "search_wrapper">
                <div class="input-holder">
                    <input type="text" class="search-input" autocomplete="off" name="query" id = "transcript" placeholder="Type to search" />
                    <button class="search-icon" onclick="searchToggle(this, event);">
                        <span></span>
                        <img class="microphone" onclick="startDictation(this,event,'transcript')" src="https://i.imgur.com/cHidSVu.gif"/>
                    </button>
                </div>
                <span class="close" onclick="searchToggle(this, event);"></span>
                <div class="result-container fade" id = "result_container">
                    <ul id="results">
                    </ul>
                </div>
            </div>
        </form>
    </body>
    <script type="text/javascript" src="js/autocomplete.js"></script>

</html>
