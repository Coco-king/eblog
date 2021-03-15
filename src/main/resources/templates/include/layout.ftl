<#macro layout title >

    <!DOCTYPE html>
    <html>
        <head>
            <meta charset="utf-8">
            <title>${title}</title>
            <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
            <meta name="keywords" content="fly,layui,前端社区">
            <meta name="description" content="Fly社区是模块化前端UI框架Layui的官网社区，致力于为web开发提供强劲动力">
            <link rel="shortcut icon" href="https://img.imgdb.cn/item/6038ab575f4313ce25159507.png" type="image/x-icon">
            <link rel="stylesheet" href="/res/layui/css/layui.css">
            <link rel="stylesheet" href="/res/css/global.css">
            <link rel="stylesheet" href="/res/css/editormd.min.css">

            <script src="/res/layui/layui.js"></script>
            <script src="/res/js/jquery.min.js"></script>
            <script src="/res/js/jquery.cookie.js"></script>
            <script src="/res/js/sockjs.js"></script>
            <script src="/res/js/stomp.js"></script>
            <script src="/res/js/im.js"></script>
            <script src="/res/js/chat.js"></script>
            <script src="/res/js/editormd.min.js"></script>
            <script src="/res/lib/marked.min.js"></script>
            <script src="/res/lib/prettify.min.js"></script>
            <script src="/res/lib/raphael.min.js"></script>
            <script src="/res/lib/underscore.min.js"></script>
            <script src="/res/lib/sequence-diagram.min.js"></script>
            <script src="/res/lib/flowchart.min.js"></script>
            <script src="/res/lib/jquery.flowchart.min.js"></script>
        </head>
        <body>

            <#include "/include/header.ftl" />
            <#include "/include/common.ftl" />

            <#nested />

            <#include "/include/footer.ftl" />

            <script>
              // layui.cache.page = '';
              layui.cache.user = {
                username: '${userInfo.username ! "游客"}'
                , uid: ${userInfo.id ! -1}
                , avatar: '${userInfo.avatar ! "/res/images/avatar/00.jpg"}'
                , experience: 0
                , sex: '${userInfo.sex}'
              };
              layui.config({
                version: "3.0.0"
                , base: '/res/mods/' //这里实际使用时，建议改成绝对路径
              }).extend({
                fly: 'index'
              }).use('fly');
            </script>

            <script>

              //新消息通知
              function showTips(count) {
                var elemUser = $('.fly-nav-user');
                var msg = $('<a class="fly-nav-msg" href="javascript:;">' + count + '</a>');
                elemUser.append(msg);
                msg.on('click', function () {
                  fly.json('/message/read', {}, function (res) {
                    if (res.status === 0) {
                      location.href = '/user/message';
                    }
                  });
                });
                layer.tips('你有 ' + count + ' 条未读消息', msg, {
                  tips: 3
                  , tipsMore: true
                  , fixed: true
                });
                msg.on('mouseenter', function () {
                  layer.closeAll('tips');
                })
              }

              $(function () {
                //新消息通知
                var elemUser = $('.fly-nav-user');
                if (layui.cache.user.uid !== -1 && elemUser[0]) {
                  //创建websocket连接客户端
                  var websocket = new SockJS("/websocket");
                  //交给stomp管理
                  var stompClient = Stomp.over(websocket);
                  //建立连接
                  stompClient.connect({}, function (frame) {
                    //订阅user
                    stompClient.subscribe("/user/" + ${userInfo.id} + "/messageCount", function (res) {
                      //弹窗提醒
                      showTips(res.body);
                    })
                  });
                }
              });
            </script>

            <script type="text/javascript">


              $(function () {
                var testEditor = editormd("test-editormd", {
                  width: "100%",
                  // height: 640,
                  path: '/res/lib/',
                  //syncScrolling : false,
                  saveHTMLToTextarea: true,    // 保存 HTML 到 Textarea
                  searchReplace: true,
                  //watch : false,                // 关闭实时预览
                  htmlDecode: "style,script,iframe|on*",            // 开启 HTML 标签解析，为了安全性，默认不开启
                  //toolbar  : false,             //关闭工具栏
                  //previewCodeHighlight : false, // 关闭预览 HTML 的代码块高亮，默认开启
                  emoji: true,
                  taskList: true,
                  tocm: true,         // Using [TOCM]
                  tex: true,                   // 开启科学公式TeX语言支持，默认关闭
                  flowChart: true,             // 开启流程图支持，默认关闭
                  sequenceDiagram: true,       // 开启时序/序列图支持，默认关闭,
                  imageUpload: true,
                  imageFormats: ["jpg", "jpeg", "gif", "png", "bmp", "webp"],
                  imageUploadURL: "/upload",
                  codeFold: true,//折叠代码块
                  toolbarIcons: function () {  //自定义工具栏，后面有详细介绍
                    return editormd.toolbarModes['full']; // full, simple, mini
                  },
                });
              });
            </script>

        </body>
    </html>

</#macro>
