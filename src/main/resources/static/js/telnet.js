var stompClient = null;
var useId = null;
var deviceId = 0;
var deviceJSONObj = null;
var retry = 3;
var connected = false;
var currentKeyDown = '';
var lastKeyDown = '';

var connectTime = 0;
var lastResponseTime = 0;

function onReady() {
    $(window).ready(function () {
        $.telnetConnect();

        $('#divList').bind('mousedown', function (event) {
            $('#cmdtext').focus();
            return false;
        });

        $('#cmdtext').bind('keydown', function (event) {

            currentKeyDown = event.key;

            switch (event.key) {
                case "Tab":
                    sendTab();
                    return false;
                case ' ':
                    var txt = $('#cmdtext').val();
                    txt = txt.trim();
                    if(txt.length == 0) {
                        sendKey(' ');
                    }

                    break;
                case "Enter":
                    sendCommand();
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
    // $("#greetings").html("");
}

function S4() {
    return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
}

function guid() {
    return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4()+S4());
}

function telnet(devid, name, ip, port) {
    window.open("/telnet.html?devid="+devid);
}

function connect() {
    var socket = new SockJS('/telnet');

    stompClient = Stomp.over(socket);

    var stompFailureCallback = function (error) {
        disconnect();
        console.log('STOMP: ' + error);
    };

    var stompSuccessCallback = function () {
        // setConnected(true);
        console.log('IP: ' + $("#ip").val());
        console.log('port: ' + $("#port").val());
        console.log('Connected: ' );
        stompClient.subscribe('/topic/telnet', function (greeting) {
            var content = JSON.parse(greeting.body).content;
            showCmdResp(content);
        });

        if(useId == null) {
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
        setTimeout(respTimeoutCheck, 1000);
    };

    stompClient.connect({}, stompSuccessCallback, stompFailureCallback);
}

function disconnect() {

    telnetDisconnect();

    $("#cmdtext").val("");
    $("#cmdtext").focus();
    $("#cmdtext").attr("disabled",true);

    if (stompClient != null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function telnetConnect() {
    var telnetHost = $("#ip").val();
    var telnetPort = $("#port").val();
    stompClient.send("/app/telnet/connect", {}, JSON.stringify({'deviceId': deviceId, 'userId': useId, 'ipaddr': telnetHost, 'port': telnetPort }));
}

function telnetDisconnect() {
    stompClient.send("/app/telnet/disconnect", {}, JSON.stringify({'deviceId': deviceId, 'userId': useId }));
}

function sendCommand() {

    var cmd = $("#cmdtext").val();
    if (cmd == "exit") {
        disconnect();
    } else {
        if(lastKeyDown == "Tab") {
            cmd = "\r\n";
        }else{
            cmd = cmd.trim() + "\r\n";
        }

        stompClient.send("/app/telnet/command", {}, JSON.stringify({
            'deviceId': deviceId,
            'userId': useId,
            'content': cmd
        }));
    }

    $("#cmdtext").val("");
    $("#cmdtext").focus();
}

function sendTab() {

    var cmd = $("#cmdtext").val();
    if (cmd != null) {
        cmd += "\t";
        stompClient.send("/app/telnet/tab", {}, JSON.stringify({'deviceId': deviceId, 'userId': useId, 'content': cmd}));
    }

    $("#cmdtext").focus();
}

function sendKey(key) {

    if (key != null) {
        stompClient.send("/app/telnet/keydown", {}, JSON.stringify({'deviceId': deviceId, 'userId': useId, 'content': key}));
    }

    $("#cmdtext").focus();
}

function showCmdResp(message) {

    lastResponseTime = new Date().getTime();

    if(message.length == 0) {
        return;
    }

    if(message.length == 1 && currentKeyDown == message) {
        $("#cmdtext").val("");
        return;
    }
    var text = message;
    //  response = response.replaceAll("\\e\\[[\\d;]*[^\\d;]","");  // \e matches escape character
    // text = text.replace("\\e\\[[\\d;]*[^\\d;]","");
    text = htmlEncode(text);
    $("#greetings").append("<li>" + text + "</li>");
    $('#divList').scrollTop( $('#divList')[0].scrollHeight );
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

    } else {
        $("#cmdtext").val(text);
    }
    $("#cmdtext").focus();
}

function showKeyDownResp(message) {

    if (message.length == 0) {
        return;
    }

    if (currentKeyDown == '\t') {
        var text = $("#cmdtext").val();
        text += "\t";
        text += message;
        $("#cmdtext").val(text);
    } else if (currentKeyDown == "Enter") {
        $("#cmdtext").val("");
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

function htmlEncode(value){

    value = $('<div/>').text(value).html();
    value = value.replace(/[ ]/g,"&nbsp;");
    value = value.replace(/\t/g, '&nbsp;&nbsp;&nbsp;&nbsp;');
    value = value.replace(/\r\n|\n|\r/g, '<br/>');

    return value;
}
//Html解码获取Html实体
function htmlDecode(value){
    return $('<div/>').html(value).text();
}

function findDeviceByid(id) {
    var respText = $.ajax({
        url: "/api/device/" + id,
        async: false,
        type:'GET',
        cache:false,
        contentType: "application/json; charset=utf-8",
        dataType:'text',
        data: null
    }).responseText;

    return JSON.parse(respText);
}

function respTimeoutCheck() {

    var now = new Date().getTime();

    if (lastResponseTime == 0) {
        if (now - connectTime > 10000) {
            disconnect();
        }
    } else {
        if (now - lastResponseTime > 300 * 1000) {
            disconnect();
        }
    }

    if (this.connected) {
        setTimeout(respTimeoutCheck, 1000);
    }
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendCommand(); });

    $.getUrlParam = function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) return unescape(r[2]); return null;
    }

    $.telnetConnect = function () {
        console.log(window.location.href);

        deviceId = $.getUrlParam('devid');

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


