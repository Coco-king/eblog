<#include "/include/layout.ftl" />

<@layout "${name}的主页">

    <div class="fly-home fly-panel" style="background-image: url();">
        <img src="${user.avatar}" alt="${user.username}">
        <i class="iconfont icon-renzheng" title="社区认证"></i>
        <h1>
            ${user.username}
            <#if user.gender == '0'>
                <i class="iconfont icon-nan"></i>
            </#if>
            <#if user.gender == '1'>
                <i class="iconfont icon-nv"></i>
            </#if>
            <#if user.gender == '-1'>
                <span style="color:#d9aa5e;">保密</span>
            </#if>
            <#if user.id == 1>
                <i class="layui-badge fly-badge-vip">VIP9</i>
                <span style="color:#c00;">(管理员)</span>
            <#--<span style="color:#5FB878;">(社区之光)</span>-->
            </#if>
            <!--
            <span>（该号已被封）</span>
            -->
        </h1>

        <#--<p style="padding: 10px 0; color: #5FB878;">认证信息：管理员</p>-->
        <p class="fly-home-info">
            <#--<i class="iconfont icon-kiss" title="飞吻"></i><span style="color: #FF7200;">66666 飞吻</span>-->
            <i class="iconfont icon-shijian"></i><span>${user.created?string('yyyy-MM-dd')} 加入</span>
            <i class="iconfont icon-chengshi"></i><span>来自${user.city}</span>
        </p>

        <p class="fly-home-sign">（${user.sign!'这个人很懒，什么都没留下'}）</p>

        <#--<div class="fly-sns" data-user="">
            <a href="javascript:void(0);" class="layui-btn layui-btn-primary fly-imActive" data-type="addFriend">加为好友</a>
            <a href="javascript:void(0);" class="layui-btn layui-btn-normal fly-imActive" data-type="chat">发起会话</a>
        </div>-->
    </div>

    <div class="layui-container">
        <div class="layui-row layui-col-space15">
            <div class="layui-col-md6 fly-home-jie">
                <div class="fly-panel">
                    <h3 class="fly-panel-title">${user.username} 最近一年的文章</h3>
                    <ul class="jie-row">
                        <#list posts as post>
                            <li>
                                <span class="fly-jing">${post.recommend?string('精','')}</span>
                                <a href="/post/${post.id}" class="jie-title"> ${post.title}</a>
                                <i>${timeAgo(post.created)}</i>
                                <em class="layui-hide-xs">${post.viewCount}阅/${post.commentCount}答</em>
                            </li>
                        </#list>
                        <#if !posts>
                            <div class="fly-none" style="min-height: 50px; padding:30px 0; height:auto;"><i
                                        style="font-size:14px;">没有发表任何求解</i></div>
                        </#if>
                    </ul>
                </div>
            </div>

            <div class="layui-col-md6 fly-home-da">
                <div class="fly-panel">
                    <h3 class="fly-panel-title">${user.username} 最近一年的评论</h3>
                    <ul class="home-jieda">
                        <!--<li>
                            <p>
                                <span>1分钟前</span>
                                在<a href="" target="_blank">tips能同时渲染多个吗?</a>中回答：
                            </p>
                            <div class="home-dacontent">
                                尝试给layer.photos加上这个属性试试：
                                <pre>
                                    full: true
                                </pre>
                                文档没有提及
                            </div>
                        </li>
                        <li>
                            <p>
                                <span>5分钟前</span>
                                在<a href="" target="_blank">在Fly社区用的是什么系统啊?</a>中回答：
                            </p>
                            <div class="home-dacontent">
                                Fly社区采用的是NodeJS。分享出来的只是前端模版
                            </div>
                        </li>-->
                        <#if !comments >
                            <div class="fly-none" style="min-height: 50px; padding:30px 0; height:auto;">
                                <span>没有评论任何文章</span>
                            </div>
                        </#if>
                        <#list comments as comm >
                            <li>
                                <p>
                                    <span>${timeAgo(comm.created)}</span>
                                    在<a href="/post/${comm.postId}" target="_blank">${comm.postTitle}</a>中评论：
                                </p>
                                <div class="home-dacontent">
                                    ${comm.content}
                                </div>
                            </li>
                        </#list>
                    </ul>
                </div>
            </div>
        </div>
    </div>

    <script>
      layui.cache.page = 'user';
    </script>
</@layout>
