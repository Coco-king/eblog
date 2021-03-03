<#include "/include/layout.ftl" />

<@layout "激活邮箱">
    <div class="layui-container fly-marginTop fly-user-main">

        <@userCenterLeft index = 4></@userCenterLeft>

        <div class="site-tree-mobile layui-hide">
            <i class="layui-icon">&#xe602;</i>
        </div>
        <div class="site-mobile-shade"></div>

        <div class="site-tree-mobile layui-hide">
            <i class="layui-icon">&#xe602;</i>
        </div>
        <div class="site-mobile-shade"></div>


        <div class="fly-panel fly-panel-user" pad20>
            <div class="layui-tab layui-tab-brief" lay-filter="user">
                <ul class="layui-tab-title">
                    <li class="layui-this">
                        激活邮箱
                    </li>
                </ul>
                <div class="layui-tab-content" id="LAY_ucm" style="padding: 20px 0;">
                    <ul class="layui-form">
                        <li class="layui-form-li">
                            <label for="activate">您的邮箱：</label>
                            <span class="layui-form-text">${user.email}
                                <#if user.status=0>
                                    <em style="color:#999;">（已成功激活）</em>
                                    <a class="layui-form-a" style="color:#4f99cf;" href="/user/set">更换邮箱</a>
                                </#if>
                                <#if user.status=-1>
                                    <em style="color:#c00;">（尚未激活）</em>
                                    <a class="layui-form-a" style="color:#4f99cf;" id="LAY-activate" href="javascript:;"
                                       email="${user.email}">发送激活邮件</a>
                                </#if>
                            </span>
                        </li>
                        <#if user.status=-1>
                            <li class="layui-form-li" style="margin-top: 20px; line-height: 26px;">
                                <div>
                                    1. 如果您未收到邮件，或激活链接失效，您可以
                                    <a class="layui-form-a" style="color:#4f99cf;" id="LAY-activate" href="javascript:;"
                                       email="${user.email}">重新发送邮件</a>，或者
                                    <a class="layui-form-a" style="color:#4f99cf;" href="/user/set">更换邮箱</a>；
                                </div>
                                <div>
                                    2. 如果您始终没有收到 Fly 发送的邮件，请注意查看您邮箱中的垃圾邮件；
                                </div>
                                <div>
                                    3. 如果你实在无法激活邮件，您还可以联系：wg3060550682@gmail.com
                                </div>
                            </li>
                        </#if>
                    </ul>
                </div>
            </div>
        </div>
    </div>

    <script>
      layui.cache.page = 'user';
    </script>
</@layout>
