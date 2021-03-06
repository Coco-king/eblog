if (typeof (tio) == 'undefined') {
  tio = {};
}

tio.ws = {};
tio.ws = function ($, layim) {

  this.heartbeatTimeout = 5000; // 心跳超时时间，单位：毫秒
  this.heartbeatSendInterval = this.heartbeatTimeout / 2;

  var self = this;

  //建立连接
  this.connect = function () {
    var url = "ws://121.196.172.171:9326?userId=" + self.userId;
    var socket = new WebSocket(url);
    self.socket = socket;

    //开启时
    socket.onopen = function () {

      self.lastInteractionTime(new Date().getTime());

      //建立心跳
      self.ping();
    }

    //接收消息时
    socket.onmessage = function (res) {
      var msgBody = eval('(' + res.data + ')');
      if (msgBody.emit === 'chatMessage') {
        layim.getMessage(msgBody.data);

        self.lastInteractionTime(new Date().getTime());
      }
    }

    //关闭时
    socket.onclose = function () {
      //尝试重连
      self.reconn();
    }
  }

  //获取历史消息
  this.initHistoryMsg = function () {
    localStorage.clear();
    $.ajax({
      url: '/chat/getGroupHistoryMsg',
      success(res) {
        var data = res.data;
        if (data.length < 1) {
          return;
        }

        for (let i in data) {
          layim.getMessage(data[i]);
        }
      }
    });
  }

  //查询用户、群的信息，打开窗口
  this.openChatWindow = function () {

    $.ajax({
      url: '/chat/getMineAndGroupData',
      async: false,
      success(res) {
        self.group = res.data.group;
        self.mine = res.data.mine;
        self.userId = self.mine.id;
      }
    });

    var cache = layui.layim.cache();
    cache.mine = self.mine;

    // 打开窗口
    layim.chat(self.group);
    layim.setChatMin(); //收缩聊天面板
  }

  // 发送消息
  this.sendChatMessage = function () {
    layim.on('sendMessage', function (res) {
      self.socket.send(JSON.stringify({
        type: 'chatMessage'
        , data: res
      }));
    });
  }

  //-----------重试机制---------------
  this.lastInteractionTime = function () {
    // debugger;
    if (arguments.length === 1) {
      this.lastInteractionTimeValue = arguments[0]
    }
    return this.lastInteractionTimeValue
  }

  this.ping = function () {
    console.log("------------->准备心跳中~");

    //建立一个定时器，定时心跳
    self.pingIntervalId = setInterval(function () {
      var iv = new Date().getTime() - self.lastInteractionTime(); // 已经多久没发消息了

      // debugger;

      // 单位：秒
      if ((self.heartbeatSendInterval + iv) >= self.heartbeatTimeout) {
        self.socket.send(JSON.stringify({
          type: 'pingMessage'
          , data: 'ping'
        }))
        console.log("------------->心跳中~")
      }
    }, self.heartbeatSendInterval)
  };

  this.reconn = function () {
    // 先删除心跳定时器
    clearInterval(self.pingIntervalId);

    // 然后尝试重连
    self.connect();
  };

}
