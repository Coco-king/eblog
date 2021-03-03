<#include "/include/layout.ftl" />

<@layout "我的消息">

    <div class="layui-container fly-marginTop fly-user-main">

        <@userCenterLeft index=3></@userCenterLeft>

        <div class="site-tree-mobile layui-hide">
            <i class="layui-icon">&#xe602;</i>
        </div>
        <div class="site-mobile-shade"></div>

        <div class="site-tree-mobile layui-hide">
            <i class="layui-icon">&#xe602;</i>
        </div>
        <div class="site-mobile-shade"></div>


        <div class="fly-panel fly-panel-user" pad20>
            <div class="layui-tab layui-tab-brief" lay-filter="user" id="LAY_msg" style="margin-top: 15px;">
                <#if pageData.total gt 0 >
                    <button class="layui-btn layui-btn-danger" id="LAY_delallmsg">清空全部消息</button>
                </#if>
                <div id="LAY_minemsg" style="margin-top: 10px;">
                    <#if pageData.total == 0>
                        <div class="fly-none">您暂时没有最新消息</div>
                    </#if>
                    <ul class="mine-msg">
                        <#list pageData.records as msg>
                            <li data-id="${msg.id}">
                                <blockquote class="layui-elem-quote">
                                    <#if msg.type == 0>
                                        系统消息：${msg.content}
                                    </#if>
                                    <#if msg.type == 1>
                                        <a href="/user/other/home/${msg.fromUsername}" target="_blank">
                                            <cite>${msg.fromUsername}</cite>
                                        </a>回答了您的文章
                                        <a target="_blank" href="/post/${msg.postId}">
                                            <cite>${msg.postTitle}</cite>
                                        </a>
                                    </#if>
                                    <#if msg.type == 2>
                                        <a href="/user/other/home/${msg.fromUsername}" target="_blank">
                                            <cite>${msg.fromUsername}</cite>
                                        </a>回复了您的评论
                                        <a href="/post/${msg.postId}" target="_blank">
                                            <cite>${msg.content}</cite>
                                        </a>
                                        &nbsp;所属文章：
                                        <a target="_blank" href="/post/${msg.postId}">
                                            <cite>${msg.postTitle}</cite>
                                        </a>
                                    </#if>
                                </blockquote>
                                <p>
                                    <span>${timeAgo(msg.created)}</span>
                                    <a href="javascript:;"
                                       class="layui-btn layui-btn-small layui-btn-danger fly-delete">删除
                                    </a>
                                </p>
                            </li>
                        </#list>
                    </ul>
                    <#if pageData.total gt 0 >
                        <@paging pageData></@paging>
                    </#if>
                </div>
            </div>
        </div>
    </div>

    <script>
      layui.cache.page = 'user';
    </script>
</@layout>
