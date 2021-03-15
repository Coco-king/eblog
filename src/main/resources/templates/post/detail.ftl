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
                                <span class="layui-btn layui-btn-xs jie-admin" type="set" field="delete">删除</span>

                                <#if post.level == 0>
                                    <span class="layui-btn layui-btn-xs jie-admin" type="set" field="stick"
                                          rank="1">置顶</span>
                                </#if>
                                <#if post.level gt 0>
                                    <span class="layui-btn layui-btn-xs jie-admin" type="set"
                                          field="stick" rank="0" style="background-color:#ccc;">取消置顶</span>
                                </#if>

                                <#if !post.recommend>
                                    <span class="layui-btn layui-btn-xs jie-admin" type="set"
                                          field="status" rank="1">加精</span>
                                </#if>
                                <#if post.recommend>
                                    <span class="layui-btn layui-btn-xs jie-admin" type="set"
                                          field="status" rank="0" style="background-color:#ccc;">取消加精</span>
                                </#if>
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
                    <div class="detail-body photos" id="md-body" style="width: auto">
                        <textarea id="append-test" style="display:none;">
                        ${post.content}
                        </textarea>
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
                                    <a class="fly-avatar" href="/user/other/home/${comm.authorName}">
                                        <img src="${comm.authorAvatar}" alt="${comm.authorName}">
                                    </a>
                                    <div class="fly-detail-user">
                                        <a href="/user/other/home/${comm.authorName}" class="fly-link">
                                            <cite>${comm.authorName}</cite>
                                            <#if comm.userId == 1>
                                                <i class="iconfont icon-renzheng" title="认证信息：管理员"></i>
                                                <i class="layui-badge fly-badge-vip">VIP9</i>
                                            </#if>
                                        </a>
                                        <#if comm.userId == post.userId>
                                            <span>(楼主)</span>
                                        </#if>
                                        <#if comm.userId == 1>
                                            <span style="color:#FF9E3F">(管理员)</span>
                                        <#--<span style="color:#5FB878">(社区之光)</span>-->
                                        </#if>
                                        <!--
                                        <span style="color:#999">（该号已被封）</span>
                                        -->
                                    </div>

                                    <div class="detail-hits">
                                        <span>${comm.created?string("yyyy年MM月dd日")}</span>
                                    </div>
                                    <#if comm.status == 66>
                                        <i class="iconfont icon-caina" title="最佳答案"></i>
                                    </#if>
                                </div>
                                <#--<div class="detail-body jieda-body photos" id="comm-content"
                                     style="line-height: 10px;height: auto;width: auto">
                                    <textarea class="comm-test" style="display:none;">${comm.content}</textarea>
                                </div>-->
                                <div class="detail-body comm-body jieda-body photos"
                                     style="line-height: 10px;height: auto;width: auto">
                                    ${comm.content}
                                </div>
                                <div class="jieda-reply">
                                    <#--<span class="jieda-zan" type="zan">
                                        <i class="iconfont icon-zan"></i>
                                        <em>${comm.voteUp}</em>
                                    </span>-->
                                    <span type="reply">
                                        <i class="iconfont icon-svgmoban53"></i>
                                        回复
                                    </span>
                                    <#if Session["userInfo"]??>
                                        <div class="jieda-admin">
                                            <#--<span type="edit">编辑</span>-->
                                            <#if comm.userId == Session["userInfo"].id || Session["userInfo"].id == post.userId>
                                                <span type="del">删除</span>
                                            </#if>
                                        </div>
                                    </#if>
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
                        <form action="/post/reply" method="post">
                            <div class="layui-form-item layui-form-text">
                                <a name="comment"></a>
                                <div class="layui-input-block">
                                    <textarea id="L_content" name="content" required lay-verify="required"
                                              placeholder="请输入内容" class="layui-textarea fly-editor"
                                              style="height: 150px;"></textarea>
                                    <#--<div id="test-editormd">
                                        <textarea style="display:none;" required lay-verify="required"
                                                  name="content"></textarea>
                                    </div>-->
                                </div>
                            </div>
                            <div class="layui-form-item">
                                <input type="hidden" name="postId" value="${post.id}">
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
        var testEditormdView;
        testEditormdView = editormd.markdownToHTML("md-body", {
          htmlDecode: "style,script,iframe",  // you can filter tags decode
          emoji: true,
          taskList: true,
          tex: true,  // 默认不解析
          flowChart: true,  // 默认不解析
          sequenceDiagram: true,  // 默认不解析
        });

        layui.use(['fly', 'face'], function () {
          var fly = layui.fly;

          $('.comm-body').each(function () {
            var othis = $(this), html = othis.html();
            othis.html(fly.content(html));
          });
        });
      });
    </script>

</@layout>
