<#include "/include/layout.ftl" />

<@layout "博客详情">

    <#include "/include/header-panel.ftl" />

    <div class="layui-container">
        <div class="layui-row layui-col-space15">
            <div class="layui-col-md8 content detail">
                <div class="fly-panel detail-box">
                    <h1>${post.title}</h1>
                    <div class="fly-detail-info">
                        <#if post.status == 0><span class="layui-badge">审核中</span></#if>
                        <span class="layui-badge layui-bg-green fly-detail-column">${post.categoryName}</span>

                        <#if post.level gt 0><span class="layui-badge layui-bg-black">置顶</span></#if>
                        <#if post.recommend><span class="layui-badge layui-bg-red">精帖</span></#if>

                        <div class="fly-admin-box" data-id="${post.id}">
                            <#if post.userId == userInfo.id && post.userId != 1>
                                <span class="layui-btn layui-btn-xs jie-admin" type="del">删除</span>
                            </#if>

                            <@shiro.hasRole name="admin">
                                <span class="layui-btn layui-btn-xs jie-admin" type="del">删除</span>

                                <span class="layui-btn layui-btn-xs jie-admin" type="set" field="stick"
                                      rank="1">置顶</span>
                                <!-- <span class="layui-btn layui-btn-xs jie-admin" type="set" field="stick" rank="0" style="background-color:#ccc;">取消置顶</span> -->

                                <span class="layui-btn layui-btn-xs jie-admin" type="set" field="status"
                                      rank="1">加精</span>
                                <!-- <span class="layui-btn layui-btn-xs jie-admin" type="set" field="status" rank="0" style="background-color:#ccc;">取消加精</span> -->
                            </@shiro.hasRole>
                        </div>
                        <span class="fly-list-nums">
                            <a href="#comment"><i class="iconfont" title="回答">&#xe60c;</i> ${post.commentCount}</a>
                            <i class="iconfont" title="人气">&#xe60b;</i> ${post.viewCount}
                        </span>
                    </div>
                    <div class="detail-about">
                        <a class="fly-avatar" href="/user/other/home/${post.authorName}">
                            <img src="${post.authorAvatar}" alt="${post.authorName}">
                        </a>
                        <div class="fly-detail-user">
                            <a href="/user/other/home/${post.authorName}" class="fly-link">
                                <cite>${post.authorName}</cite>
                                <#if post.userId == 1>
                                    <i class="iconfont icon-renzheng" title="认证信息：管理员"></i>
                                    <i class="layui-badge fly-badge-vip">VIP9</i>
                                </#if>
                            </a>
                            <span>${post.created?string("yyyy年MM月dd日")}</span>
                        </div>
                        <div class="detail-hits" id="LAY_jieAdmin" data-id="${post.id}">
                            <#--<span style="padding-right: 10px; color: #FF7200">悬赏：60飞吻</span>-->
                            <#if post.userId == userInfo.id>
                                <span class="layui-btn layui-btn-xs jie-admin" type="edit">
                                    <a href="/post/edit?id=${post.id}">编辑此贴</a>
                                </span>
                            </#if>
                        </div>
                    </div>
                    <div class="detail-body photos">
                        ${post.content}
                    </div>
                </div>

                <div class="fly-panel detail-box" id="flyReply">
                    <fieldset class="layui-elem-field layui-field-title" style="text-align: center;">
                        <legend>回帖</legend>
                    </fieldset>

                    <ul class="jieda" id="jieda">
                        <#list pageData.records as comm>
                            <li data-id="${comm.id}" class="jieda-daan">
                                <a name="item-${comm.id}"></a>
                                <div class="detail-about detail-about-reply">
                                    <a class="fly-avatar" href="/user/${comm.authorId}">
                                        <img src="${comm.authorAvatar}" alt="${comm.authorName}">
                                    </a>
                                    <div class="fly-detail-user">
                                        <a href="/user/${comm.authorId}" class="fly-link">
                                            <cite>${comm.authorName}</cite>
                                            <#if comm.userId == 1>
                                                <i class="iconfont icon-renzheng" title="认证信息：管理员"></i>
                                                <i class="layui-badge fly-badge-vip">VIP8</i>
                                            </#if>
                                        </a>
                                        <#if comm.userId == post.userId>
                                            <span>(楼主)</span>
                                        </#if>
                                        <#if comm.userId == 1>
                                            <span style="color:#5FB878">(管理员)</span>
                                            <span style="color:#FF9E3F">(社区之光)</span>
                                        </#if>
                                        <!--
                                        <span style="color:#999">（该号已被封）</span>
                                        -->
                                    </div>

                                    <div class="detail-hits">
                                        <span>${post.created?string("yyyy年MM月dd日")}</span>
                                    </div>
                                    <#if comm.status == 66>
                                        <i class="iconfont icon-caina" title="最佳答案"></i>
                                    </#if>
                                </div>
                                <div class="<#--detail-body--> jieda-body photos">
                                    <p>${comm.content}</p>
                                </div>
                                <div class="jieda-reply">
                                  <span class="jieda-zan zanok" type="zan">
                                      <i class="iconfont icon-zan"></i>
                                      <em>${comm.voteUp}</em>
                                  </span>
                                    <span type="reply">
                                        <i class="iconfont icon-svgmoban53"></i>
                                        回复
                                    </span>
                                    <div class="jieda-admin">
                                        <span type="edit">编辑</span>
                                        <span type="del">删除</span>
                                        <span class="jieda-accept" type="accept">采纳</span>
                                    </div>
                                </div>
                            </li>
                        </#list>

                        <#if pageData.total == 0>
                            <!-- 无数据时 -->
                            <li style="height: 10px;line-height: 10px;text-align: center">消灭零回复</li>
                        </#if>

                        <@paging pageData></@paging>

                    </ul>

                    <div class="layui-form layui-form-pane">
                        <form action="/jie/reply/" method="post">
                            <div class="layui-form-item layui-form-text">
                                <a name="comment"></a>
                                <div class="layui-input-block">
                                        <textarea id="L_content" name="content" required lay-verify="required"
                                                  placeholder="请输入内容" class="layui-textarea fly-editor"
                                                  style="height: 150px;"></textarea>
                                </div>
                            </div>
                            <div class="layui-form-item">
                                <input type="hidden" name="jid" value="123">
                                <button class="layui-btn" lay-filter="*" lay-submit>提交回复</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <#include "/include/right.ftl" />
        </div>
    </div>

    <script>
      layui.cache.page = 'jie';

      $(function () {
        layui.use(['fly', 'face'], function () {
          var fly = layui.fly;

          $('.detail-body').each(function () {
            var othis = $(this), html = othis.html();
            othis.html(fly.content(html));
          });
        });
      });
    </script>

</@layout>
