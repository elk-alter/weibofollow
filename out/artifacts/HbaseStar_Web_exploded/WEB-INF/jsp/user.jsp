<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%
    String nickname = (String)request.getAttribute("nickname");
    String status = (String) request.getAttribute("status");
    String username = (String) request.getAttribute("username");
    int fan = (int)request.getAttribute("fan");
    int follow = (int)request.getAttribute("follow");
%>
<head>
    <meta charset="utf-8">
    <title>HBaseStar</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/layui/css/layui.css"  media="all">
    <style type="text/css">
        *{margin: 0;padding: 0}
        html,body{height: 100%}     /*这里很关键*/

        .outer-wrap{
            /*只有同时为html和body设置height: 100%时，这里的height才生效，
            并且随浏览器窗口变化始终保持和浏览器视窗等高*/
            height: 100%;
            position: relative;
            background: url("${pageContext.request.contextPath}/image/backgrund.jpg") no-repeat;
        }
        .login-panel{
            width: 400px;
            height: 300px;
            position: absolute;
            top: 50%;
            left: 50%;
            margin-top: -150px;
            margin-left: -200px;
        }
        .image{
            width:200px;
            height:200px;
            border-radius:200px;
        }
        <%=status%>
    </style>
</head>
<body>

<div class="outer-wrap">
    <div class="login-panel">
        <div class="layui-card">
            <div class="layui-card-body">
                <div align="center">
                    <img class="image" src="${pageContext.request.contextPath}/image/head.jpg" alt="头像"/>
                </div>
                <br>
                <h1 align="center"><%=nickname%></h1>
                <br>
                <div align="center">
                    <button type="button" class="layui-btn layui-btn-normal layui-btn-radius" onclick="show()">
                        <span id="true" ><a href="javascript:void(0)" onclick="t(this)">+关注</a></span>
                        <span id="false"><a href="javascript:void(0)" onclick="f(this)">已关注</a></span>
                    </button>
                </div>
                <br>
                <div>
                    <table cellpadding="0" cellspacing="0" width="100%">
                        <tbody align="center">
                        <tr>
                            <td>
                                <a href="javascript:void(0)" onclick="follow(this)">
                                    <strong><%=follow%></strong><br>
                                    <span>关注</span>
                                </a>
                            </td>
                            <td>
                                <a href="javascript:void(0)" onclick="fan(this)">
                                    <strong><%=fan%></strong><br>
                                    <span>粉丝</span>
                                </a>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
                <br>
                <div>
                    <h3>
                        <a href="javascript:void(0)" onclick="home(this)">我的主页</a>
                    </h3>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/jquery-3.5.1.js"></script>
<script src="${pageContext.request.contextPath}/layui/layui.js"></script>
<script>
    //一般直接写在一个js文件中
    layui.use(['layer', 'form'], function(){
        var layer = layui.layer
            , form = layui.form;
    });

    function show(){
        var aTrue = document.getElementById(true);
        var aFalse = document.getElementById(false);
        if ( aTrue.style.display!="none"){
            aTrue.style.display="none";
            aFalse.style.display="inline";
        }else{
            aTrue.style.display="inline";
            aFalse.style.display="none";
        }
    };

    function follow() {
        var parames = new Array();
        parames.push({ name: "rowKey", value: "<%=username%>"});

        Post("FollowListServlet", parames);
        return false;
    }

    function fan() {
        var parames = new Array();
        parames.push({ name: "rowKey", value: "<%=username%>"});

        Post("FanListServlet", parames);
        return false;
    }

    function t() {
        var parames = new Array();
        parames.push({ name: "rowKey", value: "<%=username%>"});
        parames.push({ name: "id", value: "true"});
        layer.msg('关注成功');
        Post("FollowServlet", parames);
        return false;
    }
    function f() {
        var parames = new Array();
        parames.push({ name: "rowKey", value: "<%=username%>"});
        parames.push({ name: "id", value: "false"});
        layer.msg('已取消关注');
        Post("FollowServlet", parames);
        return false;
    }
    function home() {
        var parames = new Array();
        parames.push({ name: "rowKey", value: "<%=username%>"});
        parames.push({ name: "id", value: "home"});
        Post("FollowServlet", parames);
        return false;
    }


    /*
        *功能： 模拟form表单的提交
        *参数： URL 跳转地址 PARAMTERS 参数
        */
    function Post(URL, PARAMTERS) {
        //创建form表单
        var temp_form = document.createElement("form");
        temp_form.action = URL;
        //如需打开新窗口，form的target属性要设置为'_blank'
        temp_form.target = "_self";
        temp_form.method = "post";
        temp_form.style.display = "none";
        //添加参数
        for (var item in PARAMTERS) {
            var opt = document.createElement("textarea");
            opt.name = PARAMTERS[item].name;
            opt.value = PARAMTERS[item].value;
            temp_form.appendChild(opt);
        }
        document.body.appendChild(temp_form);
        //提交数据
        temp_form.submit();
    }
</script>
</body>
</html>