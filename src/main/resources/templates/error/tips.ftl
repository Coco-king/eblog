<#include "./include/layout.ftl" />

<@layout "提示页面">

    <#include "./include/header-panel.ftl" />

    <div class="layui-container fly-marginTop">
        <div class="fly-panel">
            <div class="fly-none">
                <h2><i class="iconfont icon-tishilian"></i></h2>
                <p>${message!"啊哦！服务器开小差了，等一会再试试吧！"}</p>
            </div>
        </div>
    </div>
</@layout>
