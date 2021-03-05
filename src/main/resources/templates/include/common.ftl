<#--分页-->
<#macro paging pageData>
    <div style="text-align: center">
        <div id="laypage-main">
        </div>
        <script>
          layui.use(['laypage', 'layer'], function () {
            var laypage = layui.laypage, layer = layui.layer;
            //完整功能
            laypage.render({
              elem: 'laypage-main',
              count: ${pageData.total},
              curr: ${pageData.current},
              limit: ${pageData.size},
              layout: ['prev', 'page', 'next', 'skip'],
              jump: function (obj, first) {
                if (!first) {
                  location.href = "?cp=" + obj.curr;
                }
              }
            });
          })
        </script>
    </div>
</#macro>

<#--博客列表-->
<#macro postList post>
    <li>
        <a href="/user/other/home/${post.authorName}" class="fly-avatar">
            <img src="${post.authorAvatar}" alt="${post.authorName}">
        </a>
        <h2>
            <a class="layui-badge">${post.categoryName}</a>
            <a href="/post/${post.id}">${post.title}</a>
        </h2>
        <div class="fly-list-info">
            <a href="/user/other/home/${post.authorName}" link>
                <cite>${post.authorName}</cite>
            </a>
            <span>${timeAgo(post.created)}</span>
            <span class="fly-list-nums">
                <i class="iconfont icon-pinglun1" title="回答"></i> ${post.commentCount}
            </span>
        </div>
        <div class="fly-list-badge">
            <#if post.status == 0><span class="layui-badge">审核中</span>
            <#elseif post.status == -1><span style="background-color: #ff2222" class="layui-badge">已删除</span></#if>
            <#if post.recommend><span class="layui-badge layui-bg-red">精贴</span></#if>
            <#if post.level gt 0><span class="layui-badge layui-bg-black">置顶</span></#if>
        </div>
    </li>
</#macro>
<#macro userCenterLeft index>
    <ul class="layui-nav layui-nav-tree layui-inline" lay-filter="user">
        <li class="layui-nav-item <#if index==0>layui-this</#if>">
            <a href="/user/home">
                <i class="layui-icon">&#xe609;</i>
                我的主页
            </a>
        </li>
        <li class="layui-nav-item <#if index==1>layui-this</#if>">
            <a href="/user/index">
                <i class="layui-icon">&#xe612;</i>
                用户中心
            </a>
        </li>
        <li class="layui-nav-item <#if index==2>layui-this</#if>">
            <a href="/user/set">
                <i class="layui-icon">&#xe620;</i>
                基本设置
            </a>
        </li>
        <li class="layui-nav-item <#if index==3>layui-this</#if>">
            <a href="/user/message">
                <i class="layui-icon">&#xe611;</i>
                我的消息
            </a>
        </li>
        <li class="layui-nav-item <#if index==4>layui-this</#if>">
            <a href="/user/activate">
                <i class="layui-icon">&#xe605;</i>
                密保邮箱
            </a>
        </li>
        <@shiro.hasRole name="admin">
            <li class="layui-nav-item <#if index==5>layui-this</#if>">
                <a href="/admin/check">
                    <i class="layui-icon">&#xe613;</i>
                    管理中心
                </a>
            </li>
        </@shiro.hasRole>
    </ul>
</#macro>
