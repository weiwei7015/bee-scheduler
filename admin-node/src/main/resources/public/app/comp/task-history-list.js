define(['text!comp/task-history-list.html'], function (tpl) {
    return {
        template: tpl,
        components: {},
        data: function () {
            var vm = this;
            var data = {
                queryLoading: false,
                queryParams: {
                    keyword: '',
                    page: 1
                },
                curQueryParams: null,
                queryResult: {},
                taskGroups: []
            };

            vm.$http.get("/task/history/groups").then(function (re) {
                vm.taskGroups = re.body.data;
            });

            return data;
        },
        mounted: function () {
            this.query();
        },
        methods: {
            query: function () {
                var vm = this;
                var queryParams = vm.queryParams;
                vm.load(queryParams);
            },
            load: function (queryParams) {
                var vm = this;

                vm.curQueryParams = queryParams;

                vm.queryLoading = true;
                vm.$http.get("/task/history/list", {params: queryParams}).then(function (re) {
                    vm.queryLoading = false;
                    vm.queryResult = re.body.data;
                }, function () {
                    vm.queryLoading = false;
                    vm.queryResult = {};
                });
            },
            reload: function () {
                this.load(this.curQueryParams);
            },
            showTaskHistoryDetail: function (fireId) {
                this.$router.push("/task/history/detail/" + fireId);
            },
            changePage: function (val) {
                this.curQueryParams.page = val;
                this.load(this.curQueryParams);
            },
            resolveRowClass: function (row, index) {
                return row.state === 'SUCCESS' ? "row-success" : row.state === 'FAIL' ? "row-fail" : row.state === 'VETOED' ? "row-warning" : "";
            }
        }
    };
});