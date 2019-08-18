define(['text!comp/index.html', 'css!./index.css'], function (tpl) {
    return {
        template: tpl,
        components: {},
        data: function () {
            var vm = this;
            var data = {
                isMenuFold: false,
                now: null
            };
            var refreshServerTime = function () {
                vm.$http.get("/server-time").then(function (re) {
                    data.now = re.body;
                });
            };
            // refreshServerTime();
            // window.setInterval(refreshServerTime, 1000);
            return data;
        },
        mounted: function () {
            var vm = this;
            var NoticeService = {
                data: {
                    offset: new Date().getTime()
                },
                fetch: function (offset, callback, onerror) {
                    vm.$http.get("/notices", {params: {"offset": offset}}).then(function (re) {
                        var re_data = re.body;
                        NoticeService.data.offset = re_data.offset;
                        callback(re_data.notificationList);
                    }, function () {
                        window.setTimeout(function () {
                            onerror(data, status);
                        }, 3000);

                    });
                },
                listenMsg: function (onNotice) {
                    NoticeService.fetch(NoticeService.data.offset, function (noticeList) {
                        onNotice(noticeList);
                        NoticeService.listenMsg(onNotice);
                    }, function () {
                        NoticeService.listenMsg(onNotice);
                    });
                }
            };

            /*
             NoticeService.listenMsg(function (noticeList) {
             for (var i in noticeList) {
             var notice = noticeList[i];
             var content = notice.content;

             if (notice.type === "JOB_TO_BEEXECUTED") {
             var task_key = content.taskGroup + "." + content.taskName;
             window.setTimeout(function () {
             vm.$notify({type: 'info', title: "开始执行任务", message: task_key});
             }, 50);
             } else if (notice.type === "JOB_WAS_EXECUTED") {
             var task_key = content.taskGroup + "." + content.taskName;
             if (content.state === "success") {
             window.setTimeout(function () {
             vm.$notify({type: 'success', title: "执行成功", message: task_key});
             }, 50);
             } else if (content.state === "fail") {
             window.setTimeout(function () {
             vm.$notify({type: 'error', title: "执行失败", message: task_key});
             }, 50);
             }
             } else if (notice.type === "JOB_EXECUTION_VETOED") {
             var task_key = content.taskGroup + "." + content.taskName;
             window.setTimeout(function () {
             vm.$notify({type: 'warning', title: "取消执行", message: task_key});
             }, 50);
             }
             }
             });
             */
        },
        methods: {}
    };
});