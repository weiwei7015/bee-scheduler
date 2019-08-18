define(['text!comp/task-history-detail.html', 'css!./task-history-detail.css'], function (tpl) {
    return {
        template: tpl,
        components: {},
        data: function () {
            var vm = this;
            var fireId = vm.$route.params.fireId;
            var data = {
                taskHistoryDetailLoading: true,
                taskHistoryDetail: {}
            };

            vm.$http.get("/task/history/detail?fireId=" + fireId).then(function (re) {
                data.taskHistoryDetail = re.body;
                data.taskHistoryDetailLoading = false;
            });

            return data;
        },
        methods: {
            formatLogContent: function (logContent) {
                return String(logContent).replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/\r/g, "\n").replace(/(\[WARN].*)/g, "<span class='text-warning'>$1</span>").replace(/(\[ERROR].*)/g, "<span class='text-danger'>$1</span>");
            }
        }
    };
});