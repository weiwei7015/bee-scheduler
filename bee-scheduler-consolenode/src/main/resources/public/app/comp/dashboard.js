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
            return {};
        },
        methods: {}
    };
});