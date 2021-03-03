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
                background-color: #5FB878;
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
                width: 200px;
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
        </style>
    </head>

    <body>
        <div class="container">
            <div class="header">
                <h2>激活成功！</h2>
            </div>
            <div class="content">
                <p>${msg}</p>
                <div style="width: 100%;height: 40px;padding: 20px 0">
                    <a href="${index}" class="button">点击前往首页</a>
                </div>
            </div>
            <div class="footer">
                <p>请保管好您的信息，避免被他人盗用</p>
                <p>©codecrab</p>
            </div>
        </div>
    </body>
</html>
