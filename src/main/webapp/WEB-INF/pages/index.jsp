<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta http-equiv="content-language" content="ja" />
    <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=8" />
    <meta http-equiv="content-style-type" content="text/css"/>
    <meta http-equiv="content-script-type" content="text/javascript"/>
    <meta name="author" content="SANE"/>
    <meta name="copyright" content="Copyright SANE. All rights reserved. No reproduction or republication without written permission."/>

    <title>BCHAT</title>
    <!-- jQuery -->
    <script type="text/javascript" src="./resources/js/jquery-1.10.1.min.js"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script src="./resources/js/bootstrap.min.js"></script>
    <!-- Bootstrap -->
    <link href="./resources/css/bootstrap.min.css" rel="stylesheet" />
    <!-- Common -->
    <link href="./resources/css/common.css" rel="stylesheet" />

    <script type="text/javascript">

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

    </script>
</head>

<body>
<nav class="navbar navbar-default" role="navigation">
    <div class="container-fluid">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">BCHAT</a>
        </div>

        <!-- Collect the nav links, forms, and other content for toggling -->
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav navbar-right">
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">設定 <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="#" id="setprofile">ユーザ名変更</a></li>
                        <li><a href="#" id="setimage">ユーザ画像変更</a></li>
                        <li><a href="#" class="logout" onclick="alert('ログアウト！');">ログアウト</a></li>
                    </ul>
                </li>
            </ul>
        </div><!-- /.navbar-collapse -->
    </div><!-- /.container-fluid -->
</nav>

<div class="container-fluid">

    <!-- ▼グリッドレイアウト -->
    <div class="row-fluid">

        <!-- ▼グリッド3列分の段（12列のうち） -->
        <div class="col-md-2 sidebar">

            <!-- ▼サイドバー：ナビゲーションリスト -->
            <ul class="nav nav-list">

                <li class="nav-header">グループ</li>
                <li><a href="#"><i class="icon-home"></i>ラボ雑談用</a></li>

            </ul><!-- /.nav nav-list -->

        </div>

        <div class="col-md-10">
            <div id="contents" style="width: 95%; height: 500px; background-color: #222; overflow-y: scroll; overflow-x: hidden;">

                <div class="messageblock" data-message-id="0" data-session-id="0" data-timeout="0">
                    <div class="row">
                        <div class="col-md-2 name" style="color: #6EE;">管理人</div>
                        <div class="col-md-8"><p class="text-right messagedate text-muted">2014/05/16 17:15:00</p></div>
                    </div>
                    <div class="row">
                        <div class="col-md-2"><img class="usericon" src="./resources/images/tamago_480x480.jpg" width="32px" height="32px" /></div>
                        <div class="col-md-8 messagebody" style="white-space: pre;">ようこそラボチャットへ！
このチャットは、入力中のメッセージも読むことができます。
ユーザ名や画像は、右上の設定から変更することができます。</div>
                    </div>
                    <hr/>
                </div>

                <div id="inputtingblock" class="inputtingblock">

                </div>

            </div>
            <div id="input_area" style="width: 100%; height: 100px; background-color: #222;">

                <div class="col-md-2">メッセージ<br/>(Shift+Enterで送信)</div>
                <form class="form-inline" role="form">
                    <div class="form-group">
                        <textarea id="message" cols="80" rows="4" class="form-control"></textarea>
                    </div>
                    <button id="send" type="button" class="btn btn-default">送信</button>
                </form>

            </div>

        </div>

    </div><!-- /.row-fluid -->

</div>

</body>

</html>