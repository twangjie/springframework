var stompClient = null;
var useId = null;
var deviceId = 0;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);

    $("#ip").prop("disabled", !connected);
    $("#ip").prop("disabled", connected);

    $("#port").prop("disabled", !connected);
    $("#port").prop("disabled", connected);

    if (connected) {
        $("#conversation").show();
    }
    else {
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
        setTimeout(connect, 3000);
        console.log('STOMP: Reconecting in 3 seconds');
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
        var name = $.getUrlParam('name');
        var host = $.getUrlParam('host');
        var port = $.getUrlParam('port');

        $("#lblConnect").text("Telnet " + name);

        if (host != null && port != null) {
            $("#ip").val(host);
            $("#port").val(port);

            connect();
        }
    }
});


