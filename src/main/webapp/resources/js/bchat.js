
var username = "ラボ" + Math.floor(Math.random() * 800 + 100);
var usericon = "./resources/images/tamago" + Math.floor(Math.random() * 6) + "_480x480.jpeg";

var ws;
var closedFlag = true;

$(document).ready(function() {
    $("#send").click(function(e){

        if (closedFlag) {
            alert("WebSocketは閉じられました。\n再読み込みしてください。");
            return false;
        }

        var message = $("#message").val();
        if (message.length == 0) {
            return false;
        }

        var json = {};
        json.status = "commit";
        json.name = username;
        json.message = message;
        json.usericon = usericon;
        var jsonStr = $.stringify(json);
        ws.send(jsonStr);
        $("#message").val("");
        return false;
    });

    $("#message").keydown(function(e) {
        if( e.keyCode === 13 && e.shiftKey ) {  // When "Shift + Enter"
            $("#send").click();
            return false;
        };
    });

    $("#message").keyup(function(e) {

        if (closedFlag) {
            alert("WebSocketは閉じられました。\n再読み込みしてください。");
            return false;
        }

        var json = {};
        json.status = "change";
        json.name = username;
        json.message = $("#message").val();
        json.usericon = usericon;
        var jsonStr = $.stringify(json);
        ws.send(jsonStr);
    });

    $("#setprofile").click(function(e) {
        var inputText = window.prompt("ユーザ名を入力してください", username);
        if (inputText) {
            username = inputText;
        }
    });

    // いきなりポップアップはあれなのでなくす。
    // $("#setprofile").click();

    $("#setimage").click(function(e) {
        var inputText = window.prompt("画像URLを入力してください (32x32に縮小されます)", usericon);
        if (inputText) {
            usericon = inputText;
        }
    });

    //    $(function(){
    var url = window.location.href;
    var arr = url.split("/");

    var extraPath = "";
    if (arr.length > 2 && arr[3] != "" && arr[3] != "#") {
        extraPath = "/" + arr[3];
    }
    var wsUri = "ws://" + arr[2] + extraPath + "/bchat";
    ws = new WebSocket(wsUri);

    ws.onopen = function(){
        closedFlag = false;
    };

    ws.onclose = function(){
        closedFlag = true;
    };

    ws.onmessage = function(message){
        var msgdata = $.parseJSON(message.data);
        msgdata.message = msgdata.message.replace(/&quot;/g, '"');
        if (msgdata.status == "commit") {
            // delete old message
            var inputtingElm = $('#inputtingblock div.messageblock[data-session-id="' + msgdata.sessionid + '"]');
            inputtingElm.remove();
            // clone new message
            var cloneElm = $($("div.messageblock")[0]).clone(true);
            cloneElm.attr("data-message-id", msgdata.messageid);
            cloneElm.attr("data-session-id", msgdata.sessionid);
            cloneElm.find("div.name").text(msgdata.name);
            cloneElm.find("div.messagebody").text(msgdata.message);
            cloneElm.find("p.messagedate").text(msgdata.messagedate);
            cloneElm.find("img.usericon").attr("src", msgdata.usericon);
            $("#inputtingblock").before(cloneElm);
            $("#contents").scrollTop($("#contents").prop("scrollHeight"));

            // ゴミ掃除
            $("#inputtingblock").find("div.messageblock").each(function() {
                var timeout = $(this).data("data-timeout");
                if (timeout == null || typeof(timeout) == "undefined") {
                    timeout = 1;
                }
                timeout = parseInt(timeout) + 1;
                $(this).data("data-timeout", timeout);
                if (timeout > 5) {
                    $(this).remove();
                }
            });

        } else {
            var inputtingElm = $('#inputtingblock div.messageblock[data-session-id="' + msgdata.sessionid + '"]');
            var message = msgdata.message;
            if (inputtingElm.length > 0) {
                if (message.length > 0) {
                    inputtingElm.find("div.messagebody").text(msgdata.message);
                } else {
                    inputtingElm.remove();
                }

            } else if (message.length > 0) {
                var cloneElm = $($("div.messageblock")[0]).clone(true);
                cloneElm.attr("data-message-id", msgdata.messageid);
                cloneElm.attr("data-session-id", msgdata.sessionid);
                cloneElm.find("div.name").text(msgdata.name);
                cloneElm.find("div.messagebody").text(msgdata.message);
                cloneElm.find("p.messagedate").text("...");
                cloneElm.find("img.usericon").attr("src", msgdata.usericon);
                $("#inputtingblock").append(cloneElm);
            }
            $("#contents").scrollTop($("#contents").prop("scrollHeight"));
        }
    };

    ws.onerror = function(event){
        alert("エラーが発生しました。再読み込みしてください。");
    };
});

jQuery.extend({
    stringify : function stringify(obj) {
        var t = typeof (obj);
        if (t != "object" || obj === null) {
            // simple data type
            if (t == "string") obj = '"' + obj + '"';
            return String(obj);
        } else {
            // recurse array or object
            var n, v, json = [], arr = (obj && obj.constructor == Array);

            for (n in obj) {
                v = obj[n];
                t = typeof(v);
                if (obj.hasOwnProperty(n)) {
                    if (t == "string") {
                        v = v.replace(/\n/g,"\\\\n");
                        v = v.replace(/"/g,'&quot;');
                        v = '"' + v + '"';
                    } else if (t == "object" && v !== null) v = jQuery.stringify(v);
                    json.push((arr ? "" : '"' + n + '":') + String(v));
                }
            }
            return (arr ? "[" : "{") + String(json) + (arr ? "]" : "}");
        }
    }
});
