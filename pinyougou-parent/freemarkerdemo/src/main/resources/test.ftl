<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>freemarkerDemo</title>
</head>
<body>
    <#--include指令,引入页面-->

    <#--注释写法-->
    <!--html注释-->
    <#--插值-->
    ${name},你好.${message}<br/>
    <#--ftl指令-->
    <#assign man="周先生"/>
    ${man}<br>

    <#if success=true>
        已经通过实名认证
        <#else>
        未通过实名认证
    </#if>
    <br>
    <#list goodsList as goods>
        ${goods_index} : ${goods.name}  :  ${goods.price}<br>
    </#list>
    <#--内建函数-->
    共${goodsList?size}条记录<br>

    <#--转换json字符串为对象-->
    <#assign jsonToObj="{'brand','三星','account','123134554736456'}">
    <#assign data=jsonToObj?eval />
    品牌:${data.brand}<br>
    编号:${data.account}<br>

    <#--日期的格式化-->
    当前日期:${today?date}<br>
    当前日期时间:${today?datetime}<br>
    当前时间:${today?time}<br>
    日期格式化:${today?string("yyyy年MM月")}<br>

    <#--数字转换字符串-->
    字符串数字:${point}<br>
    去点:${point?c}<br>

    <#--空值得处理,判断变量是否存在,运算符??-->
    <#assign ccc="2">
    <#if ccc??>
        变量存在
        <#else >
        变量不存在
    </#if><br>

    <#--缺失变量默认值,如果aaa变量不存在,则显示后面定义的值-->
    缺失变量默认值:${aaa!'-'}<br>

    <#--
        算数运算符
        逻辑运算符
        比较运算符
        都是支持的
    -->


</body>
</html>