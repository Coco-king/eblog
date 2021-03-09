<#include "/include/layout.ftl" />

<@layout "${type}文章">

    <div class="layui-container fly-marginTop">
        <div class="fly-panel" pad20 style="padding-top: 5px;">
            <div class="layui-form layui-form-pane">
                <div class="layui-tab layui-tab-brief" lay-filter="user">
                    <ul class="layui-tab-title">
                        <li class="layui-this"><#if post??>编辑文章<#else>发布文章</#if></li>
                    </ul>
                    <div class="layui-form layui-tab-content" id="LAY_ucm" style="padding: 20px 0;">
                        <div class="layui-tab-item layui-show">
                            <form action="/post/submit" method="post">
                                <div class="layui-row layui-col-space15 layui-form-item">
                                    <div class="layui-col-md3">
                                        <label class="layui-form-label">所在专栏</label>
                                        <div class="layui-input-block">
                                            <select lay-verify="required" name="categoryId" lay-filter="column">
                                                <option></option>
                                                <#list categories as c>
                                                    <option <#if c.id == post.categoryId>selected</#if>
                                                            value="${c.id}">${c.name}</option>
                                                </#list>
                                            </select>
                                        </div>
                                    </div>
                                    <div class="layui-col-md9">
                                        <label for="L_title" class="layui-form-label">标题</label>
                                        <div class="layui-input-block">
                                            <input type="text" id="L_title" name="title" required lay-verify="required"
                                                   value="${post.title}" autocomplete="off" class="layui-input">
                                            <input type="hidden" name="id" value="${post.id}">
                                        </div>
                                    </div>
                                </div>
                                <div class="layui-form-item layui-form-text">
                                    <div id="test-editormd">
                                        <textarea style="display:none;" name="content">${post.content}</textarea>
                                    </div>
                                    <#--<div class="layui-input-block">
                                        <textarea id="L_content" name="content" required lay-verify="required"
                                                  placeholder="详细描述" class="layui-textarea fly-editor"
                                                  style="height: 260px;">${post.content}</textarea>
                                    </div>-->
                                </div>
                                <div class="layui-form-item">
                                    <label for="L_vercode" class="layui-form-label">人类验证</label>
                                    <div class="layui-input-inline">
                                        <input type="text" id="L_vercode" name="vercode" required lay-verify="required"
                                               placeholder="请输入验证码" autocomplete="off" class="layui-input">
                                    </div>
                                    <div class="layui-form-mid" style="padding: 0!important;">
                                        <img id="captcha" src="/postCaptcha.jpg" alt="验证码" style="cursor:pointer;">
                                    </div>
                                </div>
                                <div class="layui-form-item">
                                    <button class="layui-btn" lay-filter="*" lay-submit alert="1">立即发布</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
      layui.cache.page = 'jie';

      $(function () {
        var testEditor = editormd("test-editormd", {
          height: 640,
          path: '/res/lib/',
        });
      });

      //刷新验证码
      $('#captcha').click(function () {
        this.src = "/postCaptcha.jpg";
      })
    </script>
</@layout>
