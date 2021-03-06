var stompClient = null;
var useId = null;
var deviceId = 0;
var sessionTimeOut = 3600;
var deviceJSONObj = null;
var retry = 3;
var connected = false;
var currentKeyDown = '';
var lastKeyDown = '';

var connectTime = 0;
var firstResponseTime = 0;
var lastResponseTime = 0;

var sessionTimer = null;
var timeoutAlerted = false;

if (!window.console) console = {
    log: function () {
    }
};

function onReady() {

    $(window).ready(function () {

        $("#lblStatus").text("连接中");

        $.telnetConnect();

        $("body").eq(0).bind("mousedown", function (event) {
            //$('#greetings').focus();
            return false;
        });

        $('body').bind('keydown', function (event) {
            currentKeyDown = event.key;

            if (event.key == "F5") {
                return !connected;
            }
            // F1 - F12
            else if (event.keyCode >= 112 && event.keyCode <= 123) {
                return !connected;
            }
            // 多媒体键
            else if (event.keyCode >= 170 && event.keyCode <= 180) {
                return !connected;
            }
            else if (event.key == "Shift" || event.key == "Alt" || event.key == "Ctrl"
                || event.key == "CapsLock" || event.key == "Control" || event.key == "Escape"
                || event.key == "Insert" || event.key == "Home" || event.key == "End"
                || event.key == "PageUp" || event.key == "PageDown" || event.key == "NumLock"
                || event.key == "ScrollLock" || event.key == "Pause" || event.key == "Meta"
                || event.key == "ContextMenu" || event.key == "Delete" || event.key == "ArrowUp"
                || event.key == "ArrowDown" || event.key == "ArrowLeft" || event.key == "ArrowRight") {
                return !connected;
            }

            if (!connected) {
                showAlert("错误", "连接已断开，请重新打开页面.");
                return true;
            }

            switch (event.key) {
                case "Tab":
                    sendTab();
                    return false;
                case "Backspace":
                    sendKey('\b');
                    break;
                case "Enter":
                    //sendKey('\r');
                    sendKey('\n');
                    break;
                case "Spacebar":
                    sendKey(' ');
                    break
                default:
                    sendKey(event.key);
            }

            lastKeyDown = event.key;
        });
    });
}

function setConnected(connected) {

    this.connected = connected;

    if (connected) {
        $("#lblStatus").text("已连接");
    }
    else {
        if (retry <= 0) {
            $("#lblStatus").text("连接失败");
        }
        else {
            $("#lblStatus").text("未连接");
        }
    }
}

function S4() {
    return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
}

function guid() {
    return (S4() + S4() + "-" + S4() + "-" + S4() + "-" + S4() + "-" + S4() + S4() + S4());
}

function telnet(devid, name, ip, port) {
    window.open("/telnet.html?devid=" + devid);
}

function connect() {
    //var socket = new SockJS('/telnet', "xdr-streaming");
    var socket = new SockJS('/telnet');

    stompClient = Stomp.over(socket);

    var stompFailureCallback = function (error) {
        disconnect();
        console.log('STOMP: ' + error);
    };

    var stompSuccessCallback = function () {
        console.log('IP: ' + $("#ip").val());
        console.log('port: ' + $("#port").val());
        console.log('Connected: ');
        stompClient.subscribe('/topic/telnet', function (greeting) {
            var content = JSON.parse(greeting.body).content;
            showCmdResp(content);
        });

        if (useId == null) {
            useId = guid();
        }

        var uri = '/user/' + useId + '/telnet';
        stompClient.subscribe(uri + '/cmdresp', function (greeting) {
            setConnected(true);
            var content = JSON.parse(greeting.body).content;
            showCmdResp(content);
        });

        stompClient.subscribe(uri + '/tabresp', function (greeting) {
            setConnected(true);
            var content = JSON.parse(greeting.body).content;
            showTabResp(content);
        });

        stompClient.subscribe(uri + '/keydownresp', function (greeting) {
            setConnected(true);
            var content = JSON.parse(greeting.body).content;
            showKeyDownResp(content);
        });

        telnetConnect();

        connectTime = new Date().getTime();
        sessionTimer = setInterval(sessionTimeoutCheck, 1000);
    };

    stompClient.connect({}, stompSuccessCallback, stompFailureCallback);
}

function disconnect() {

    clearInterval(sessionTimer);

    telnetDisconnect();

    if (stompClient != null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function telnetConnect() {
    var telnetHost = $("#ip").val();
    var telnetPort = $("#port").val();
    stompClient.send("/app/telnet/connect", {}, JSON.stringify({
        'deviceId': deviceId,
        'userId': useId,
        'ipaddr': telnetHost,
        'port': telnetPort
    }));
}

function telnetDisconnect() {
    stompClient.send("/app/telnet/disconnect", {}, JSON.stringify({'deviceId': deviceId, 'userId': useId}));
}

function sendCommand() {

    var cmd = $("#cmdtext").val();
    if (cmd == "exit") {
        disconnect();
    } else {
        if (lastKeyDown == "Tab") {
            cmd = "\r\n";
        } else {
            cmd = cmd.trim() + "\r\n";
        }

        stompClient.send("/app/telnet/command", {}, JSON.stringify({
            'deviceId': deviceId,
            'userId': useId,
            'content': cmd
        }));
    }
}

function sendTab() {

    stompClient.send("/app/telnet/tab", {}, JSON.stringify({'deviceId': deviceId, 'userId': useId, 'content': '\t'}));
}

function sendKey(key) {

    if (key != null) {
        stompClient.send("/app/telnet/keydown", {}, JSON.stringify({
            'deviceId': deviceId,
            'userId': useId,
            'content': key
        }));
    }
}

function showCmdResp(message) {

    lastResponseTime = new Date().getTime();
    if (firstResponseTime == 0) {
        firstResponseTime = lastResponseTime;
    }

    if (message.length == 0) {
        return;
    }

    if (message.indexOf("\b") >= 0) {
        var text = $("#greetings").text();
        for (i = 0; i < message.length; i++) {
            var temp = message[i];
            if (temp == '\b') {
                text = text.substring(0, text.length - 1);
            } else {
                text += temp;
            }
        }

        $("#greetings").text(text);
    } else {
        $("#greetings").append(message);
    }

    $("#greetings").scrollTop($("#greetings")[0].scrollHeight);
}

function showTabResp(message) {

    if (message.length == 0) {
        return;
    }

    var words = message.split('\b');
    var text = words[words.length - 1];

    if (text.indexOf("\r\n") > 0) {
        var lines = text.split("\r\n");
        for (var i = 0; i < lines.length; i++) {
            showCmdResp(lines[i]);
        }
    }
}

function showKeyDownResp(message) {

    if (message.length == 0) {
        return;
    }

    if (currentKeyDown == '\t') {
        // var text = $("#cmdtext").val();
        // text += "\t";
        // text += message;
        // $("#cmdtext").val(text);
    } else if (currentKeyDown == "Enter") {
        // $("#cmdtext").val("");
    } else if (currentKeyDown == ' ') {
        if (message.indexOf("\r\n") > 0) {
            var lines = message.split("\r\n");
            for (var i = 0; i < lines.length; i++) {
                showCmdResp(lines[i]);
            }

        } else {
            showCmdResp(message);
        }
    }

    $('#divList').scrollTop($('#divList')[0].scrollHeight);
}

function htmlEncode(value) {

    value = $('<div/>').text(value).html();
    value = value.replace(/[ ]/g, "&nbsp;");
    value = value.replace(/\t/g, '&nbsp;&nbsp;&nbsp;&nbsp;');
    value = value.replace(/\r\n|\n|\r/g, '<br/>');

    return value;
}

//Html解码获取Html实体
function htmlDecode(value) {
    return $('<div/>').html(value).text();
}

function findDeviceByid(id) {
    var respText = $.ajax({
        url: "/api/device/" + id,
        async: false,
        type: 'GET',
        cache: false,
        contentType: "application/json; charset=utf-8",
        dataType: 'text',
        data: null
    }).responseText;

    return JSON.parse(respText);
}

function sessionTimeoutCheck() {

    var now = new Date().getTime();
    var remainTime = (sessionTimeOut * 1000 - (now - firstResponseTime)) / 1000;
    if (firstResponseTime == 0) {
        if (now - connectTime > 30000) {
            showAlert("错误", "建立连接超时");
            disconnect();
        }
    } else if (now - firstResponseTime > sessionTimeOut * 1000) {
        showAlert("超时", "会话已超时，断开连接。");
        disconnect();
    } else if (!timeoutAlerted && remainTime <= 300) {
        timeoutAlerted = true;
        showAlert("提示", "实验时间还剩5分钟。");
    } else {
        var hour = parseInt(remainTime / 3600);
        var min = parseInt((remainTime - hour * 3600) / 60);
        var sec = parseInt((remainTime - hour * 3600 - min * 60));

        if (hour > 0) {
            $("#webConsole-title-remainTime").text("剩余时间：" + hour + "小时" + min + "分钟" + sec + "秒");
        } else {
            $("#webConsole-title-remainTime").text("剩余时间：" + min + "分钟" + sec + "秒");
        }

    }
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(function () {
        connect();
    });
    $("#disconnect").click(function () {
        disconnect();
    });
    $("#send").click(function () {
        sendCommand();
    });

    $.getUrlParam = function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) return unescape(r[2]);
        return null;
    }

    $.telnetConnect = function () {
        console.log(window.location.href);

        deviceId = $.getUrlParam('devid');
        var timeout = $.getUrlParam('timeout');
        if (timeout != null) {
            sessionTimeOut = timeout * 60;
        }

        deviceJSONObj = findDeviceByid(deviceId);

        var name = deviceJSONObj.name;
        var address = deviceJSONObj.address;
        var host = deviceJSONObj.ip;
        var port = deviceJSONObj.port;
        var info = deviceJSONObj.info;

        $("#webConsole-title").text(name);
        $("#lblName").text(name);
        $("#lblAddress").text(address);
        $("#lblIP").text(host);
        $("#lblPort").text(port);
        $("#lblInfo").text(info);

        if (host != null && port != null) {
            connect();
        }
    }
});

function showAlert(title, text) {
    $("#dialog-modal").text(text);
    $("#dialog-modal").dialog({
        title: title,
        height: 140,
        modal: true,
        buttons: {
            Ok: function () {
                $(this).dialog("close");
            }
        }
    });
}
