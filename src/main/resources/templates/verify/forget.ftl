<#include "/include/layout.ftl" />

<@layout "找回密码/重置密码">

    <div class="layui-container fly-marginTop">
        <div class="fly-panel fly-panel-user" pad20>
            <div class="layui-tab layui-tab-brief" lay-filter="user">
                <ul class="layui-tab-title">
                    <li><a href="/login">登入</a></li>
                    <li class="layui-this">找回密码<!--重置密码--></li>
                </ul>
                <div class="layui-form layui-tab-content" id="LAY_ucm" style="padding: 20px 0;">
                    <div class="layui-tab-item layui-show">
                        <!-- 重置密码 -->
                        <!--
                        <div class="fly-msg">{{d.username}}，请重置您的密码</div>
                        <div class="layui-form layui-form-pane"  style="margin-top: 30px;">
                          <form action="/user/repass" method="post">
                            <div class="layui-form-item">
                              <label for="L_pass" class="layui-form-label">密码</label>
                              <div class="layui-input-inline">
                                <input type="password" id="L_pass" name="pass" required lay-verify="required" autocomplete="off" class="layui-input">
                              </div>
                              <div class="layui-form-mid layui-word-aux">6到16个字符</div>
                            </div>
                            <div class="layui-form-item">
                              <label for="L_repass" class="layui-form-label">确认密码</label>
                              <div class="layui-input-inline">
                                <input type="password" id="L_repass" name="repass" required lay-verify="required" autocomplete="off" class="layui-input">
                              </div>
                            </div>
                            <div class="layui-form-item">
                              <label for="L_vercode" class="layui-form-label">人类验证</label>
                              <div class="layui-input-inline">
                                <input type="text" id="L_vercode" name="vercode" required lay-verify="required" placeholder="请回答后面的问题" autocomplete="off" class="layui-input">
                              </div>
                              <div class="layui-form-mid">
                                <span style="color: #c00;">{{d.vercode}}</span>
                              </div>
                            </div>
                            <div class="layui-form-item">
                              <input type="hidden" name="username" value="{{d.username}}">
                              <input type="hidden" name="email" value="{{d.email}}">
                              <button class="layui-btn" alert="1" lay-filter="*" lay-submit>提交</button>
                            </div>
                          </form>
                        </div>


                        <div class="fly-error">该重置密码链接已失效，请重新校验您的信息</div>
                        <div class="fly-error">非法链接，请重新校验您的信息</div>
                        -->

                        <div class="layui-form layui-form-pane">
                            <form method="post">
                                <div class="layui-form-item">
                                    <label for="L_email" class="layui-form-label">邮箱</label>
                                    <div class="layui-input-inline">
                                        <input type="text" id="L_email" name="email" required lay-verify="required"
                                               placeholder="请输入已激活的邮箱" autocomplete="off" class="layui-input">
                                    </div>
                                </div>
                                <div class="layui-form-item">
                                    <label for="L_vercode" class="layui-form-label">人类验证</label>
                                    <div class="layui-input-inline">
                                        <input type="text" id="L_vercode" name="code" required lay-verify="required"
                                               placeholder="请输入验证码" autocomplete="off" class="layui-input L_code">
                                    </div>
                                    <div class="layui-form-mid" style="padding: 0!important;">
                                        <img id="captcha" src="/captcha.jpg" alt="验证码" style="cursor:pointer;">
                                    </div>
                                </div>
                                <div class="layui-form-item">
                                    <label for="L_vercode" class="layui-form-label">验证码</label>
                                    <div class="layui-input-inline">
                                        <input type="text" id="L_vercode" name="vercode" required lay-verify="required"
                                               placeholder="请输入邮箱验证码" autocomplete="off" class="layui-input">
                                    </div>
                                    <div class="layui-form-mid" style="padding: 0!important;">
                                        <button type="button" class="layui-btn layui-btn-normal" id="sendCode">获取验证码
                                        </button>
                                    </div>
                                </div>
                                <div class="layui-form-item">
                                    <label for="L_pass" class="layui-form-label">密码</label>
                                    <div class="layui-input-inline">
                                        <input type="password" id="L_pass" name="pass" required lay-verify="required"
                                               autocomplete="off" class="layui-input">
                                    </div>
                                    <div class="layui-form-mid layui-word-aux">长度在6-16个字符之间</div>
                                </div>
                                <div class="layui-form-item">
                                    <label for="L_repass" class="layui-form-label">确认密码</label>
                                    <div class="layui-input-inline">
                                        <input type="password" id="L_repass" name="repass" required
                                               lay-verify="required" autocomplete="off" class="layui-input">
                                    </div>
                                </div>
                                <div class="layui-form-item">
                                    <button class="layui-btn" alert="1" lay-filter="*" lay-submit>提交</button>
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

      //刷新验证码
      $('#captcha').click(function () {
        this.src = "/captcha.jpg";
      })

      timeDown = $.cookie("timeDown") <= 0 ? 60 : $.cookie("timeDown");
      disable = $.cookie("disable")

      //刷新验证码
      $('#sendCode').click(function () {
        $this = $('#sendCode');
        //没有被禁用时禁用并执行倒计时
        if (!$this.hasClass("layui-btn-disabled")) {
          if ($this.attr("id") == "sendCode") {
            $.get('/checkForgetCode', {code: $(".L_code").val()}, function (res) {
              if (res.status == 0) {
                //邮件发送过慢，先提示再异步发送
                layer.msg('邮件已发送，接受可能会稍有延迟，请注意查收。');
                $this.addClass("layui-btn-disabled");
                timeDown = 60
                $.cookie("disable", "disable", {"expires": 1})
                inter1 = setInterval(function () {
                  countDown($this)
                }, 1000)

                $.get('/sendForgetCode', {email: $("#L_email").val()}, function (res) {
                  if (res.status != 0) {
                    //发送失败
                    layer.msg(res.msg);
                  }
                  $.removeCookie("timeDown")
                  $.removeCookie("disable")
                })
              } else {
                layer.msg(res.msg)
              }
            })
          }
        }
      })

      function countDown($obj) {
        var time;
        if ($obj.attr("id") == "sendCode") {
          time = --timeDown;
          $.cookie("timeDown", time, {"expires": 1});
          if (time <= 0) {
            timeDown = 60;
            $obj.removeClass("layui-btn-disabled")
            clearInterval(inter1)
            $obj.text("重新获取验证码")
            $.cookie("disable", "")
            return
          }
        }
        $obj.text(time + "秒后重新发送")
      }

      $(function () {
        if (disable == "disable") {
          $("#sendCode").addClass("layui-btn-disabled")
          inter1 = setInterval(function () {
            countDown($("#sendCode"))
          }, 1000)
        }
      })
    </script>
</@layout>
