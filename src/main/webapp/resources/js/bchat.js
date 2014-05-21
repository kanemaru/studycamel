
var that;
var BChatHandler = function(){};
BChatHandler.prototype = {

    STATUS_COMMIT : "commit",
    STATUS_CHANGE : "change",

    webSocket : null, // WebSocket Object
    username : "", // ユーザ名
    usericon : "", // ユーザアイコン

    init : function() {

        that = this;
        that.username = "ラボ" + Math.floor(Math.random() * 800 + 100);
        that.usericon = "./resources/images/tamago" + Math.floor(Math.random() * 6) + "_480x480.jpeg";

        $("#send").click(function(e){
            var message = $("#message").val();
            if (message.length == 0) {
                return false;
            }
            that.sendMessage(that.STATUS_COMMIT, message);
            $("#message").val("");

            return false;
        });

        $("#message").keyup(function(e) {
            that.sendMessage(that.STATUS_CHANGE, $("#message").val());
        });

        $("#message").keydown(function(e) {
            if( e.keyCode === 13 && e.shiftKey ) {  // "Shift + Enter"
                $("#send").click();
                return false;
            };
        });

        $("#setprofile").click(function(e) {
            var inputText = window.prompt("ユーザ名を入力してください", that.username);
            if (inputText) {
                that.username = inputText;
            }
        });

        $("#setimage").click(function(e) {
            var inputText = window.prompt("画像URLを入力してください (32x32に縮小されます)", that.usericon);
            if (inputText) {
                that.usericon = inputText;
            }
        });

        that.initWebSocket();
    },

    sendMessage : function(status, message) {

        if (that.webSocket == null) {
            alert("WebSocketは閉じられました。\n再読み込みしてください。");
            return false;
        }

        var json = {};
        json.status = status;
        json.name = that.username;
        json.message = message;
        json.usericon = that.usericon;
        var jsonStr = $.stringify(json);
        that.webSocket.send(jsonStr);
    },

    createWebSocketUri : function() {

        var url = window.location.href;
        var arr = url.split("/");

        var extraPath = "";
        if (arr.length > 2 && arr[3] != "" && arr[3] != "#") {
            extraPath = "/" + arr[3];
        }
        var wsUri = "ws://" + arr[2] + extraPath + "/bchat";
        return wsUri;
    },

    initWebSocket : function() {

        var wsUri = that.createWebSocketUri();
        that.webSocket = new WebSocket(wsUri);

        that.webSocket.onopen = function(){
        };

        that.webSocket.onclose = function(){
            that.webSocket = null;
        };

        that.webSocket.onerror = function(event){
            alert("エラーが発生しました。再読み込みしてください。");
        };

        that.webSocket.onmessage = function(message){

            var chatData = $.parseJSON(message.data);
            chatData.message = chatData.message.replace(/&quot;/g, '"');

            if (chatData.status == that.STATUS_COMMIT) {

                // delete old message
                var inputtingElm = $('#inputtingblock div.messageblock[data-session-id="' + chatData.sessionid + '"]');
                inputtingElm.remove();
                // clone new message
                var msgElm = that.createMessageElement(chatData);
                $("#inputtingblock").before(msgElm);
                $("#contents").scrollTop($("#contents").prop("scrollHeight"));

                // ゴミ掃除
                that.clearLeftMessages();

            } else {

                var inputtingElm = $('#inputtingblock div.messageblock[data-session-id="' + chatData.sessionid + '"]');
                var message = chatData.message;
                if (inputtingElm.length > 0) {
                    if (message.length > 0) {
                        inputtingElm.find("div.messagebody").text(chatData.message);
                    } else {
                        inputtingElm.remove();
                    }

                } else if (message.length > 0) {
                    chatData.messagedate = "...";
                    var msgElm = that.createMessageElement(chatData);
                    $("#inputtingblock").append(msgElm);
                }
                $("#contents").scrollTop($("#contents").prop("scrollHeight"));
            }
        };
    },

    createMessageElement : function(chatData) {

        var cloneElm = $($("div.messageblock")[0]).clone(true);
        cloneElm.attr("data-message-id", chatData.messageid);
        cloneElm.attr("data-session-id", chatData.sessionid);
        cloneElm.find("div.name").text(chatData.name);
        cloneElm.find("div.messagebody").text(chatData.message);
        cloneElm.find("p.messagedate").text(chatData.messagedate);
        cloneElm.find("img.usericon").attr("src", chatData.usericon);
        return cloneElm;
    },

    clearLeftMessages : function() {

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
    }

};

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
