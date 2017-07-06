define(['text!comp/task-trends-plate.html'], function (tpl, ace) {
    return {
        template: tpl,
        components: {},
        data: function () {
            var vm = this;
            var data = {
                totalTaskCount: 0,
                executingTaskCount: 0,
                taskTrends: []
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
            setInterval(refreshData, 1000);
            return data;
        },
        methods: {}
    };
});