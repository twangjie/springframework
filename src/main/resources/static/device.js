var flag = false;

var jsonObj = { device: [
    { id:'1', ip: "192.168.35.3", port: "23"},
    { id:'2', ip: "192.168.35.4", port: "23"},
    { id:'3', ip: "192.168.35.5", port: "23"}
]};

function refresh_list() {
    refresh();
    setTimeout(refresh_list, 10000);
}

$(window).ready(function() {

    var path = window.location.pathname;
    if(path.indexOf("device.html") >= 0) {
        flag = true;
    }

    refresh_list();
});

function getDevices() {
    var respText = $.ajax({
        url: "/api/device",
        async: false,
        type:'GET',
        cache:false,
        contentType: "application/json; charset=utf-8",
        dataType:'json'
    }).responseText;

    jsonObj.device = JSON.parse(respText);
}

function addDevice() {

    var ip = $('#ip').val();
    var port = $('#port').val();
    var name = $('#name').val();
    var address = $('#address').val();
    var info = $('#info').val();

    var flag = checkDeviceExist(ip, port);
    if(flag == false) {
        alert("添加失败! ip: "+ ip +  ", port: "+ port + " 已经存在!");
        return false;
    }

    var newDevice = '{"ip": "'+ip+'", "port":"'+port+'", "name":"'+name+'", "address":"'+address+'", "info":"'+info+'", "status":2}';

    var respText = $.ajax({
        url: "/api/device",
        async: false,
        type:'PUT',
        cache:false,
        contentType: "application/json; charset=utf-8",
        dataType:'text',
        data: newDevice
    }).responseText;

    refresh();
}

function deleteDevice(obj) {

    var respText = $.ajax({
        url: "/api/device?id=" + obj.id,
        async: false,
        type:'DELETE',
        cache:false,
        contentType: "application/json; charset=utf-8",
        dataType:'text',
        data: null
    }).responseText;

    refresh();
}

function getStatsDesc(status) {
    switch(status) {
        case 1:
            return "连接";
            break;
        case 2:
            return "空闲";
            break;
    }
    return "未知";
}

function refresh() {
    getDevices();

    var devices = jsonObj.device;
    $("tr[name='device']").remove();
    for(var i=0; i<devices.length; i++) {
        var device = devices[i];

        var idTd = "<td>" + device.id + "</td>";
        var nameTd = "<td>" + device.name + "</td>";
        var addrTd = "<td>" + device.address + "</td>"
        var ipTd = "<td>" + device.ip + "</td>";
        var portTd = "<td>" + device.port + "</td>"
        var infoTd = "<td>" + device.info + "</td>"
        var statusTd = "<td>" + getStatsDesc(device.status) + "</td>"

        var action = "<td><a href='#' onclick='telnet("+device.id+",\""+device.name+"\",\""+device.ip+"\","+device.port+")'</a>Telnet</td>"
        if (flag == true) {
            action = "<td><a href='#' onclick='deleteDevice(this)' id='";
            action += device.id + "'" + "</a>Delete</td>"
        }

        var trStr = "<tr name='device'>";
        trStr += idTd;
        trStr += nameTd;
        trStr += addrTd;
        trStr += ipTd;
        trStr += portTd;
        trStr += infoTd;
        trStr += statusTd;
        trStr += action;
        trStr += "</tr>";

        $('#deviceform').append(trStr);
    }
}

function checkDeviceExist(ip, port) {
    var devices = jsonObj.device;
    for(var i=0; i<devices.length; i++) {
        if(devices[i].ip == ip && devices[i].port == port) {
            return false;
        }
    }
    return true;
}
