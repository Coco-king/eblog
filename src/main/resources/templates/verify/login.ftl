<#include "/include/layout.ftl" />

<@layout "用户登录">

    <div class="layui-container fly-marginTop">
        <div class="fly-panel fly-panel-user" pad20>
            <div class="layui-tab layui-tab-brief" lay-filter="user">
                <ul class="layui-tab-title">
                    <li class="layui-this">登入</li>
                    <li><a href="/register">注册</a></li>
                </ul>
                <div class="layui-form layui-tab-content" id="LAY_ucm" style="padding: 20px 0;">
                    <div class="layui-tab-item layui-show">
                        <div class="layui-form layui-form-pane">
                            <form method="post">
                                <div class="layui-form-item">
                                    <label for="L_email" class="layui-form-label">账号</label>
                                    <div class="layui-input-inline">
                                        <input type="text" id="L_email" name="username" required lay-verify="required"
                                               placeholder="请输入邮箱/昵称" autocomplete="off" class="layui-input">
                                    </div>
                                </div>
                                <div class="layui-form-item">
                                    <label for="L_pass" class="layui-form-label">密码</label>
                                    <div class="layui-input-inline">
                                        <input type="password" id="L_pass" name="password" required
                                               lay-verify="required"
                                               placeholder="请输入密码" autocomplete="off" class="layui-input">
                                    </div>
                                </div>
                                <#-- <div class="layui-form-item">
                                     <label for="L_vercode" class="layui-form-label">人类验证</label>
                                     <div class="layui-input-inline">
                                         <input type="text" id="L_vercode" name="vercode" required lay-verify="required"
                                                placeholder="请输入验证码" autocomplete="off" class="layui-input">
                                     </div>
                                     <div class="layui-form-mid" style="padding: 0!important;">
                                         <img id="captcha" src="/captcha.jpg" alt="验证码" style="cursor:pointer;">
                                     </div>
                                 </div>-->
                                <div class="layui-form-item">
                                    <button class="layui-btn" lay-filter="*" lay-submit>立即登录</button>
                                    <span style="padding-left:20px;">
                                        <a href="/forgetPass">忘记密码？</a>
                                    </span>
                                </div>
                                <div class="layui-form-item fly-form-app">
                                    <span>或者使用社交账号登入</span>
                                    <a href="" onclick="layer.msg('正在通过QQ登入', {icon:16, shade: 0.1, time:0})"
                                       class="iconfont icon-qq" title="QQ登入"></a>
                                    <a href="" onclick="layer.msg('正在通过微博登入', {icon:16, shade: 0.1, time:0})"
                                       class="iconfont icon-weibo" title="微博登入"></a>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
      layui.cache.page = 'user';
    </script>
</@layout>
