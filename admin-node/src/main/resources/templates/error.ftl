<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <link rel="stylesheet" href="/public/lib/element-ui-2.0.11/theme-chalk/index.css">
    <link rel="stylesheet" href="/public/app/css/rewrite.css">
    <link rel="stylesheet" href="/public/app/css/base.css">
</head>
<body>
<div class="app" style="position: relative;">
    <div class="content">
        <div class="header">
            <a href="/" class="logo"><img src="/public/app/img/logo.png"></a>
            <span class="logo-text"><div class="el-badge">Bee Scheduler<sup class="el-badge__content is-fixed">BETA</sup></div></span>
            <span class="pull-right" style="margin-right: 10px;">V1.0 - Beta</span>
        </div>
        <div style="position: absolute;left:0;right:0;width: 600px; margin:120px auto 20px auto;">
            <div class="el-card box-card" data-v-140d6310="">
                <div class="el-card__header">
                    <div class="clearfix"><span style="line-height: 36px;font-size: 16px"><i class="el-icon-warning text-warning"></i> <b>出错啦！（${status}）</b></span>
                        <button type="button" class="el-button el-button--primary" style="float: right;" onclick="window.location='/'"><span>前往首页</span></button>
                    </div>
                </div>
                <div class="el-card__body" style="padding: 40px;min-height: 180px">
                    <div>时间：${timestamp?string("yyyy-MM-dd")}</div>
                    <div class="spr"></div>
                    <div>摘要：${error}</div>
                    <div class="spr"></div>
                    <div>详细：${message}</div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>