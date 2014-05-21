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
    <!-- Include BCHAT JavaScript -->
    <script src="./resources/js/bchat.js"></script>
    <!-- Bootstrap -->
    <link href="./resources/css/bootstrap.min.css" rel="stylesheet" />
    <!-- Common -->
    <link href="./resources/css/common.css" rel="stylesheet" />

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