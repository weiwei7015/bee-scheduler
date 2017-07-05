define(['text!comp/task-trends-plate.html'], function (tpl, ace) {
    return {
        template: tpl,
        components: {},
        data: function () {
            var vm = this;
            var data = {taskList: []};
            vm.$http.get("/task/executing/list").then(function (re) {
                data.taskList = re.body.data;
            });
            return data;
        },
        methods: {}
    };
});