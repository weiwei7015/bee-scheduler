define(['text!comp/task-list.html', 'css!./task-list.css'], function (tpl) {

    var quickTaskDialog = function (resolver) {
        require(['comp/quick-task-dialog'], resolver);
    };

    var comp_task_trends_plate = function (resolver) {
        require(['comp/task-trends-plate'], resolver);
    };

    return {
        template: tpl,
        components: {
            'quick-task-dialog': quickTaskDialog,
            "task-trends-plate": comp_task_trends_plate
        },
        data: function () {
            var vm = this;
            var data = {
                quickTaskDialogVisible: false,
                queryLoading: false,
                queryParams: {
                    keyword: '',
                    page: 1
                },
                curQueryParams: null,
                queryResult: {},
                selectedItems: [],
                enabledCommandBtn: [],
                taskModuleList: {}
            };

            return data;
        },
        computed: {
            showResumeBtn: function () {
                for (var i = 0; i < this.selectedItems.length; i++) {
                    var item = this.selectedItems[i];
                    if (item.state === "PAUSED" || item.state === "PAUSED_BLOCKED") {
                        return true;
                    }
                }
                return false;
            },
            showPauseBtn: function () {
                for (var i = 0; i < this.selectedItems.length; i++) {
                    var item = this.selectedItems[i];
                    if (item.state !== "PAUSED" && item.state !== "PAUSED_BLOCKED") {
                        return true;
                    }
                }
                return false;
            },
            showCommandBtnGroup: function () {
                return this.selectedItems.length > 0;
            }
        },
        mounted: function () {
            this.query();
        },
        methods: {
            query: function () {
                var vm = this;
                vm.queryParams.page = 1;
                vm.load(vm.queryParams);
            },
            load: function (queryParams) {
                var vm = this;

                vm.curQueryParams = queryParams;
                vm.queryLoading = true;
                vm.queryResult = {};
                vm.selectedItems = [];

                vm.$http.get("/task/list", {params: queryParams}).then(function (re) {
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
                vm.$http.get("/task/query-suggestions", {params: {input: queryString}}).then(function (re) {
                    var suggestionResult = re.body;
                    suggestionResult.forEach(function (value) {
                        suggestions.push({"value": value});
                    });
                    callback(suggestions);
                }, function (reason) {
                    callback(suggestions);
                });
            },
            onRowClick: function (row, event, column) {
                console.log(column.id);
                if (column.id !== "el-table_1_column_1") {
                    this.$taskDetailDialog.open(row.name, row.group);
                }
            },
            pauseTask: function (name, group) {
                var vm = this;
                var taskIds = [];
                for (var i = 0; i < vm.selectedItems.length; i++) {
                    var item = vm.selectedItems[i];
                    taskIds.push(item.group + "-" + item.name);
                }

                vm.$confirm("确认暂停选中的任务?", '提示', {type: 'warning'}).then(function () {
                    vm.$http.post("/task/pause", null, {params: {taskIds: taskIds.join(",")}}).then(function (re) {
                        vm.$message({message: '任务已暂停', type: 'success'});
                        vm.reload();
                    });
                }).catch(function () {
                    //...
                });
            },
            resumeTask: function (name, group) {
                var vm = this;
                var taskIds = [];
                for (var i = 0; i < vm.selectedItems.length; i++) {
                    var item = vm.selectedItems[i];
                    taskIds.push(item.group + "-" + item.name);
                }

                vm.$confirm("确认恢复选中的任务?", '提示', {type: 'warning'}).then(function () {
                    vm.$http.post("/task/resume", null, {params: {taskIds: taskIds.join(",")}}).then(function (re) {
                        vm.$message({message: '任务已恢复', type: 'success'});
                        vm.reload();
                    });
                }).catch(function () {
                    //...
                });
            },
            executeTask: function (name, group) {
                var vm = this;
                var taskIds = [];
                for (var i = 0; i < vm.selectedItems.length; i++) {
                    var item = vm.selectedItems[i];
                    taskIds.push(item.group + "-" + item.name);
                }

                vm.$confirm("立即执行选中的任务?", '提示', {type: 'warning'}).then(function () {
                    vm.$http.post("/task/execute", null, {params: {taskIds: taskIds.join(",")}}).then(function (re) {
                        vm.$message({message: '任务已触发', type: 'success'});
                    });
                }).catch(function () {
                    //...
                });
            },
            deleteTask: function (name, group) {
                var vm = this;
                var taskIds = [];
                for (var i = 0; i < vm.selectedItems.length; i++) {
                    var item = vm.selectedItems[i];
                    taskIds.push(item.group + "-" + item.name);
                }

                vm.$confirm("确认删除选中的任务?", '提示', {type: 'warning'}).then(function () {
                    vm.$http.post("/task/delete", null, {params: {taskIds: taskIds.join(",")}}).then(function (re) {
                        vm.$message({message: '任务已删除', type: 'success'});
                        vm.reload();
                    });
                }).catch(function () {
                    //...
                });
            },
            handleSelectionChange: function (val) {
                this.selectedItems = val;
            },
            createQuickTask: function () {
                this.quickTaskDialogVisible = true
            },
            goTaskExecHistoryList: function (group, name) {
                this.$router.push("/task/history/list/" + encodeURI("g:" + group + " " + name));
            },
            goCreateTask: function () {
                this.$router.push("/task/new");
            },
            goCopyTask: function () {
                var target = this.selectedItems[0];
                this.$router.push("/task/copy/" + target.group + "-" + target.name);
            },
            goEditTask: function () {
                var target = this.selectedItems[0];
                this.$router.push("/task/edit/" + target.group + "-" + target.name);
            }
        }
    };
});