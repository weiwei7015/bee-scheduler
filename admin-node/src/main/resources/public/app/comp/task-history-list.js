define(['text!comp/task-history-list.html'], function (tpl) {
    return {
        template: tpl,
        components: {},
        data: function () {
            var vm = this;
            var data = {
                queryLoading: false,
                queryFormModel: {
                    name: '',
                    group: '',
                    execState: '',
                    firedWay: '',
                    page: 1,
                    pageSize: 10
                },
                currentQueryModel: null,
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
                var queryFormModel = vm.queryFormModel;
                var queryModel = {
                    taskName: queryFormModel.name,
                    taskGroup: queryFormModel.group,
                    execType: queryFormModel.execType,
                    firedWay: queryFormModel.firedWay,
                    page: 1
                };
                vm.load(queryModel);
            },
            load: function (queryModel) {
                var vm = this;

                vm.currentQueryModel = queryModel;

                vm.queryLoading = true;
                vm.$http.get("/task/history/list", {params: queryModel}).then(function (re) {
                    vm.queryLoading = false;
                    vm.queryResult = re.body.data;
                }, function () {
                    vm.queryLoading = false;
                    vm.queryResult = {};
                });
            },
            reload: function () {
                this.load(this.currentQueryModel);
            },
            showTaskHistoryDetail: function (fireId) {
                this.$router.push("/task/history/detail/" + fireId);
            },
            changePage: function (val) {
                this.currentQueryModel.page = val;
                this.load(this.currentQueryModel);
            },
            resolveRowClass: function (row, index) {
                return row.state === 'SUCCESS' ? "row-success" : row.state === 'FAIL' ? "row-fail" : row.state === 'VETOED' ? "row-warning" : "";
            }
        }
    };
});