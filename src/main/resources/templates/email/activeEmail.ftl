<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>激活邮件</title>
        <style type="text/css">
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
                font-family: Arial, Helvetica, sans-serif;
            }

            body {
                background-color: #ffffff;
            }

            .container {
                width: 800px;
                margin: 50px auto;
            }

            .header {
                height: 80px;
                background-color: #15adff;
                border-top-left-radius: 5px;
                border-top-right-radius: 5px;
                padding-left: 30px;
            }

            .header h2 {
                padding-top: 25px;
                color: white;
            }

            .content {
                background-color: #f6f6f6;
                padding-left: 30px;
                padding-bottom: 30px;
                border-bottom: 1px solid #ccc;
            }

            .content h2 {
                padding-top: 20px;
                padding-bottom: 20px;
            }

            .content p {
                padding-top: 10px;
            }

            .footer {
                background-color: #F6F6F6;
                border-bottom-left-radius: 5px;
                border-bottom-right-radius: 5px;
                padding: 35px;
            }

            .footer p {
                color: #747474;
                padding-top: 10px;
            }

            .button {
                line-height: 40px;
                height: 40px;
                font-weight: 600;
                font-size: 20px;
                cursor: pointer;
                border: #15adff 1px solid;
                background-color: #e0f0ff;
                width: 100%;
                margin: 0 auto;
                text-align: center;
                border-radius: 5px;
                color: #27caff;
                transition: all 0.5s;
                display: block;
                text-decoration: none;
            }

            .button:hover {
                border-color: #e0f0ff;
                color: #15adff;
                background-color: #f7faff;
            }

            .lay {
                margin-top: 60px;
            }
        </style>
    </head>

    <body>
        <div class="container">
            <div class="header">
                <#if params.type==0> <#--验证邮箱-->
                    <h2>欢迎注册！</h2>
                </#if>
                <#if params.type==1>
                    <h2>邮箱验证码：重置密码</h2>
                </#if>
            </div>
            <div class="content">
                <h2>亲爱的${(params.username)!""}您好</h2>

                <div style="width: 100%;height: 40px;margin: 20px 0">
                    <#if params.type==0> <#--验证邮箱-->
                        <a style="margin-bottom: 20px;" href="${(params.href)!"www.codecrab.top"}" class="button">点击下方链接验证您的邮箱</a>
                        <p style="padding-bottom: 20px;display: block;line-height: 10px; font-size: 20px;">
                            ${(params.href)!"www.codecrab.top"}
                        </p>
                    </#if>
                    <#if params.type==1>
                        <div>您的验证码为：${(params.code)}</div>
                    </#if>
                </div>

                <p class="<#if params.type==0>lay</#if>">您的邮箱：<b><span>${(params.email)!""}</span></b></p>
                <p>您注册时的日期：<b><span>${(params.created?string("yyyy-MM-dd HH:mm:ss"))!""}</span></b></p>
                <p>当您在使用本网站时，务必要遵守法律法规</p>
                <p>如果您有什么疑问可以联系管理员，Email: <b>wg3060550682@gmail.com</b></p>
            </div>
            <div class="footer">
                <p>此为系统邮件，请勿回复</p>
                <p>请保管好您的信息，避免被他人盗用</p>
                <p>©codecrab</p>
            </div>
        </div>
    </body>
</html>
