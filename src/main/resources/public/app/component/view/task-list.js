define(['text!view/task-list.html'], function (tpl) {

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
                    status: ''
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
                    var formData = new FormData();
                    formData.append("name", name);
                    formData.append("group", group);
                    vm.$http.post("/task/pause", formData).then(function (re) {
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
                    var formData = new FormData();
                    formData.append("name", name);
                    formData.append("group", group);
                    vm.$http.post("/task/resume", formData).then(function (re) {
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

                vm.$confirm("确认执行任务【" + group + "." + name + "】?", '提示', {type: 'warning'}).then(function () {
                    var formData = new FormData();
                    formData.append("name", name);
                    formData.append("group", group);
                    vm.$http.post("/task/execute", formData).then(function (re) {
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
                    var formData = new FormData();
                    formData.append("name", name);
                    formData.append("group", group);
                    vm.$http.post("/task/delete", formData).then(function (re) {
                        vm.newTaskDialogVisible = false;
                        vm.$message({message: '任务已删除！', type: 'success'});
                        vm.reload();
                    });
                }).catch(function () {
                    //...
                });
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