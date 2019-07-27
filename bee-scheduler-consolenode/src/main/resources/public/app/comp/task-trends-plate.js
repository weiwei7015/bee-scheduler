define(['text!comp/task-trends-plate.html'], function (tpl, ace) {
    return {
        template: tpl,
        components: {},
        data: function () {
            var vm = this;
            var data = {
                totalTaskCount: 0,
                executingTaskCount: 0,
                taskTrends: [],
                refreshTaskTrendsTimer: null
            };

            var refreshData = function () {
                vm.$http.get("/task/trends").then(function (re) {
                    var reData = re.body.data;
                    data.totalTaskCount = reData.taskTotalCount;
                    data.executingTaskCount = reData.executingTaskCount;
                    data.taskTrends = reData.taskTrends;
                });
            };

            refreshData();
            data.refreshTaskTrendsTimer = setInterval(refreshData, 2000);

            return data;
        },
        destroyed: function () {
            window.clearInterval(this.refreshTaskTrendsTimer);
        },
        methods: {}
    };
});