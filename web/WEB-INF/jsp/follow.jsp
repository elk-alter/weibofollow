<%@ page import="java.util.List" %>
<%@ page import="com.elk.bean.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%
    List<User> list = (List<User>) request.getAttribute("followlist");
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
    </style>
</head>
<body>
<div class="outer-wrap">
    <div class="login-panel">
        <div class="layui-card">
            <div class="layui-card-body">
                <fieldset class="layui-elem-field layui-field-title" style="margin-top: 50px;">
                    <legend>关注的人</legend>
                </fieldset>
                <div style="height:300px;overflow-y:auto">
                    <table class="layui-table" lay-skin="line">
                        <colgroup>
                            <col width="150">
                            <col width="150">
                        </colgroup>
                        <thead>
                        <tr>
                            <th>账号</th>
                            <th>昵称</th>
                        </tr>
                        </thead>
                        <tbody id="MyTbody">
                        <%
                            for (User user : list) {
                                String str = "<tr id=\""+ user.getRowKey() +"\"><td>"+ user.getRowKey() +"</td><td>"+ user.getNickname() +"</td></tr>";
                                out.println(str);
                            }
                        %>
                        </tbody>
                    </table>
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

    $('#MyTbody').on('click','tr', function() {
        var parames = new Array();
        parames.push({ name: "rowKey", value: $(this).attr('id')});
        Post("UserServlet", parames);
        return false;
    });

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