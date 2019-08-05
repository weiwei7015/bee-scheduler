<!DOCTYPE html>
<html>
<head>
    <title>BeeScheduler</title>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <link rel="stylesheet" href="/public/lib/element-ui-2.11.1/theme-black/index.css">
    <link rel="stylesheet" href="/public/app/css/base.css">
</head>
<body>
<div class="app" style="position: relative;">
    <div class="main">
        <div class="content-wrapper no-padding">
            <div class="content" style="background-color: #F2F6FC">
                <div class="content-body" style="top: 0">
                    <div style="position: absolute;left:0;right:0;width: 600px; margin:100px auto;">
                        <div class="el-card is-always-shadow">
                            <div class="el-card__header">
                                <div class="clearfix"><span style="line-height: 36px;font-size: 16px"><i class="el-icon-warning text-warning"></i> <b>出错啦！</b></span>
                                    <button type="button" class="el-button el-button--primary" style="float: right;" onclick="window.location='/'"><span>前往首页</span></button>
                                </div>
                            </div>
                            <div class="el-card__body" style="padding: 40px;min-height: 150px">
                                <div>Time：${timestamp?string("yyyy-MM-dd HH:mm:ss")}</div>
                                <div class="spr"></div>
                                <div>Error：${error}</div>
                                <div class="spr"></div>
                                <div>Message：${message}</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>