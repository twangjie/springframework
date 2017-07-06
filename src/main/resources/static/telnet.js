var stompClient = null;
var useId = null;
var deviceId = 0;
var deviceJSONObj = null;
var retry = 3;

function setConnected(connected) {

    if (connected) {
        $("#lblStatus").text("已连接");
        $("#conversation").show();
    }
    else {
        if (retry <= 0)
            $("#lblStatus").text("连接失败");
        else
            $("#lblStatus").text("未连接");

        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function S4() {
    return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
}
function guid() {
    return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4()+S4());
}

function connect() {
    var socket = new SockJS('/telnet');

    stompClient = Stomp.over(socket);

    var stompFailureCallback = function (error) {
        disconnect();
        console.log('STOMP: ' + error);
        if(retry > 0) {
            setTimeout(connect, 3000);
            console.log('STOMP: Reconecting in 3 seconds');
        }

        retry--;
    };

    var stompSuccessCallback = function () {
        setConnected(true);
        console.log('IP: ' + $("#ip").val());
        console.log('port: ' + $("#port").val());
        console.log('Connected: ' );
        stompClient.subscribe('/topic/telnet', function (greeting) {
            var content = JSON.parse(greeting.body).content;
            showGreeting(content);
        });

        if(useId == null) {
            useId = guid();
        }

        var uri = '/user/' + useId + '/telnet';
        stompClient.subscribe(uri, function (greeting) {
            var content = JSON.parse(greeting.body).content;
            showGreeting(content);
        });

        telnetConnect();
    };

    stompClient.connect({}, stompSuccessCallback, stompFailureCallback);
}

function disconnect() {

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
    stompClient.send("/app/telnet/connect", {}, JSON.stringify({'deviceId': deviceId, 'userId': useId, 'ipaddr': telnetHost, 'port': telnetPort }));
}

function telnetDisconnect() {
    stompClient.send("/app/telnet/disconnect", {}, JSON.stringify({'deviceId': deviceId, 'userId': useId }));
}

function sendCommand() {

    var cmd = $("#name").val();
    if (cmd == "exit") {
        disconnect();
    } else {
        stompClient.send("/app/telnet", {}, JSON.stringify({'deviceId': deviceId, 'userId': useId, 'content': $("#name").val()}));
    }

    $("#name").val("");
    $("#name").focus();
}

function showGreeting(message) {

    var text = htmlEncode(message);
    $("#greetings").append(text);
    $('#greetings').scrollTop( $('#greetings')[0].scrollHeight );
}

function htmlEncode(value){
    value = $('<div/>').text(value).html();
    value = value.replace(/\r\n|\n|\r/g, '<br />');
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


