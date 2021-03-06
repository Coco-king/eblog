layui.use('layim', function (layim) {
  //先来个客服模式压压精
  // layim.config({
  //   brief: false //是否简约模式（如果true则不显示主面板）
  // }).chat({
  //   name: '客服姐姐'
  //   , type: 'friend'
  //   , avatar: 'http://tp1.sinaimg.cn/5619439268/180/40030060651/1'
  //   , id: -2
  // });

  var $ = layui.jquery;

  layim.config({
    brief: true,
    voice: false, //声音开关
    chatLog: layui.cache.dir + 'css/modules/layim/html/chatlog.html'
  });
  var tioWs = new tio.ws($, layim);

  //获取历史消息
  tioWs.initHistoryMsg();

  //查询用户、群的信息，打开窗口
  tioWs.openChatWindow();

  //建立连接
  tioWs.connect();

  //发送信息
  tioWs.sendChatMessage();
})
