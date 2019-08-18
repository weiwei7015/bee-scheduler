define(['text!comp/task-history-list.html'], function (tpl) {
    return {
        template: tpl,
        components: {},
        data: function () {
            var vm = this;
            return {
                queryLoading: false,
                queryParams: {
                    keyword: '',
                    page: 1
                },
                curQueryParams: null,
                queryResult: {}
            };
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
                    vm.queryResult = re.body;
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
            querySuggestion: function (queryString, callback) {
                var vm = this;
                var suggestions = [];
                vm.$http.get("/task/history/query-suggestions", {params: {input: queryString}}).then(function (re) {
                    var suggestionResult = re.body;
                    suggestionResult.forEach(function (value) {
                        suggestions.push({"value": value});
                    });
                    callback(suggestions);
                }, function (reason) {
                    callback(suggestions);
                });
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