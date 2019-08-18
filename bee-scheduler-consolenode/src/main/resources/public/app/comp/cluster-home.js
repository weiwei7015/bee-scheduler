define(['text!comp/cluster-home.html'], function (tpl, ace) {

    return {
        template: tpl,
        components: {},
        data: function () {
            var vm = this;
            var data = {
                clusterInfoLoading: false,
                nodes: []
            };

            data.clusterInfoLoading = true;
            vm.$http.get("/cluster/nodes").then(function (re) {
                data.nodes = re.body;
                data.clusterInfoLoading = false;
            }, function () {
                data.clusterInfoLoading = false;
            });
            return data;
        },
        methods: {
            refreshClusterInfo: function () {
                var vm = this;
                vm.clusterInfoLoading = true;
                vm.nodes = [];
                vm.$http.get("/cluster/nodes").then(function (re) {
                    vm.nodes = re.body;
                    vm.clusterInfoLoading = false;
                    vm.$message({message: '集群信息已刷新', type: 'success'});
                }, function () {
                    vm.clusterInfoLoading = false;
                });
            }
        }
    };
});