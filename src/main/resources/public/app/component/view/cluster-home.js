define(['text!view/cluster-home.html'], function (tpl, ace) {

    return {
        template: tpl,
        components: {},
        data: function () {
            var vm = this;
            var data = {
                nodes: []
            };

            vm.$http.get("/cluster/nodes").then(function (re) {
                data.nodes = re.body.data;
            });
            return data;
        },
        methods: {}
    };
});