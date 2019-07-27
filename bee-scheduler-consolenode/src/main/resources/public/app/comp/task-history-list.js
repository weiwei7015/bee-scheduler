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
                taskGroups: [],
                execStatus: ["SUCCESS", "FAIL", "VETOED"],
                firedWays: ["SCHEDULE", "MANUAL", "TMP", "LINKAGE"]

            };

            vm.$http.get("/task/history/groups").then(function (re) {
                vm.taskGroups = re.body.data;
            });

            return data;
        },
        watch: {
            '$route': function (to, from) {
                // this.queryParams.keyword = this.$route.params.kw || "";
                // this.queryParams.page = 1;
                // this.load(this.queryParams);
            }
        },
        mounted: function () {
            // this.queryParams.keyword = this.$route.params.kw || "";
            // this.queryParams.page = 1;
            // this.load(this.queryParams);
            this.query();
        },
        methods: {
            query: function () {
                var vm = this;
                vm.queryParams.page = 1;
                vm.load(vm.queryParams);
                // this.$router.push("/task/history/list/" + encodeURI(this.queryParams.keyword));
            },
            load: function (queryParams) {
                var vm = this;

                vm.curQueryParams = queryParams;
                vm.queryLoading = true;
                vm.queryResult = {};

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
            changePage: function (val) {
                this.curQueryParams.page = val;
                this.load(this.curQueryParams);
            },
            querySuggestion: function (queryString, cb) {
                var suggestions = [];
                var matchResult = /^(.+\s+)?(\S+)$/.exec(queryString);
                if (matchResult) {
                    if (matchResult[2] === "g:") {
                        this.taskGroups.forEach(function (value) {
                            suggestions.push({"value": matchResult[0] + value + " "});
                        });
                    } else if (matchResult[2] === "s:") {
                        this.execStatus.forEach(function (value) {
                            suggestions.push({"value": matchResult[0] + value + " "});
                        });
                    } else if (matchResult[2] === "f:") {
                        this.firedWays.forEach(function (value) {
                            suggestions.push({"value": matchResult[0] + value + " "});
                        });
                    }
                }
                cb(suggestions)
            },
            showTaskHistoryDetail: function (fireId) {
                this.$router.push("/task/history/detail/" + fireId);
            },
            resolveRowClass: function (row, index) {
                return row.state === 'SUCCESS' ? "row-success" : row.state === 'FAIL' ? "row-fail" : row.state === 'VETOED' ? "row-warning" : "";
            }
        }
    };
});