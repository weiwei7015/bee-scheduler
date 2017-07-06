define(['text!comp/dashboard.html'], function (tpl, ace) {

    var comp_task_trends_plate = function (resolver) {
        require(['comp/task-trends-plate'], resolver);
    };

    return {
        template: tpl,
        components: {
            "task-trends-plate": comp_task_trends_plate
        },
        data: function () {
            var vm = this;
            var data = {
                totalTaskCount: 0,
                taskList: [],
                executingTaskList: [],
                taskHistoryList: []
            };


            var refreshData = function () {
                vm.$http.get("/dashboard/data").then(function (re) {
                    var reData = re.body.data;
                    data.totalTaskCount = reData.taskTotalCount;
                    data.taskList = reData.taskList.result;
                    data.executingTaskList = reData.executingTaskList;
                    data.taskHistoryList = reData.taskHistoryList.result;
                });
            };


            refreshData();
            setInterval(refreshData, 1000);
            return data;
        },
        methods: {}
    };
});