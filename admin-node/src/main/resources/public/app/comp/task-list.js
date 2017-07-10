define(['text!comp/task-list.html'], function (tpl) {

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
                queryFormModel: {
                    name: '',
                    group: '',
                    status: '',
                    page: 1
                },
                currentQueryModel: null,
                queryResult: {},
                taskGroups: [],
                jobComponentList: {}
            };

            vm.$http.get("/task/groups").then(function (re) {
                data.taskGroups = re.body.data;
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
                    state: queryFormModel.status,
                    page: 1
                };
                vm.load(queryModel);
            },
            load: function (queryModel) {
                var vm = this;

                vm.currentQueryModel = queryModel;

                vm.queryLoading = true;
                vm.queryResult = {};
                vm.$http.get("/task/list", {params: queryModel}).then(function (re) {
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
            changePage: function (val) {
                this.currentQueryModel.page = val;
                this.load(this.currentQueryModel);
            },
            pauseTask: function (name, group) {
                var vm = this;

                vm.$confirm("确认暂停任务【" + group + "." + name + "】?", '提示', {type: 'warning'}).then(function () {
                    vm.$http.post("/task/pause", null, {params: {name: name, group: group}}).then(function (re) {
                        vm.newTaskDialogVisible = false;
                        vm.$message({message: '任务已暂停！', type: 'success'});
                        vm.reload();
                    });
                }).catch(function () {
                    //...
                });
            },
            resumeTask: function (name, group) {
                var vm = this;

                vm.$confirm("确认恢复任务【" + group + "." + name + "】?", '提示', {type: 'warning'}).then(function () {
                    vm.$http.post("/task/resume", null, {params: {name: name, group: group}}).then(function (re) {
                        vm.newTaskDialogVisible = false;
                        vm.$message({message: '任务已恢复！', type: 'success'});
                        vm.reload();
                    });
                }).catch(function () {
                    //...
                });
            },
            executeTask: function (name, group) {
                var vm = this;

                vm.$confirm("立即执行任务【" + group + "." + name + "】?", '提示', {type: 'warning'}).then(function () {
                    vm.$http.post("/task/execute", null, {params: {name: name, group: group}}).then(function (re) {
                        vm.newTaskDialogVisible = false;
                        vm.$message({message: '任务已触发！', type: 'success'});
                    });
                }).catch(function () {
                    //...
                });
            },
            deleteTask: function (name, group) {
                var vm = this;

                vm.$confirm("确认删除任务【" + group + "." + name + "】?", '提示', {type: 'warning'}).then(function () {
                    vm.$http.post("/task/delete", null, {params: {name: name, group: group}}).then(function (re) {
                        vm.newTaskDialogVisible = false;
                        vm.$message({message: '任务已删除！', type: 'success'});
                        vm.reload();
                    });
                }).catch(function () {
                    //...
                });
            },
            handleTaskCommand: function (command) {
                if (command === "execTmpTask") {
                    this.quickTaskDialogVisible = true;
                }
            },
            goCreateTask: function () {
                this.$router.push("/task/new");
            },
            goCopyTask: function (group, name) {
                this.$router.push("/task/copy/" + group + "-" + name);
            },
            goEditTask: function (group, name) {
                this.$router.push("/task/edit/" + group + "-" + name);
            }
        }
    };
});