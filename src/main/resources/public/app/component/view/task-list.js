define(['text!view/task-list.html'], function (tpl) {


    //定义子组件
    var ace_editor = function (resolver) {
        require(['part/ace-editor'], resolver);
    };


    return {
        template: tpl,
        components: {
            "ace-editor": ace_editor
        },
        data: function () {
            var vm = this;

            var validators = {
                jobComponent: [
                    {required: true, message: '请选择Job组件', trigger: 'change'}
                ],
                taskName: [
                    {required: true, message: '请输入任务名称', trigger: 'blur'}
                ],
                taskCron: [
                    {required: true, message: '请输入任务执行计划（CRON表达式）', trigger: 'blur'}
                ]
            };

            var data = {
                validators: validators,
                queryLoading: false,
                queryFormModel: {
                    name: '',
                    group: '',
                    status: 'ALL'
                },
                currentQueryModel: null,
                taskList: [],
                taskGroups: [],
                jobComponentList: {},
                newTaskDialogVisible: false,
                postNewTaskInProcess: false,
                newTaskFormModel: {
                    jobComponent: '',
                    name: '',
                    group: '',
                    cron: '',
                    params: '',
                    description: ''
                },
                editTaskDialogVisible: false,
                editTaskDialogLoading: true,
                postEditTaskInProcess: false,
                editTaskFormModel: {
                    jobComponent: '',
                    name: '',
                    group: '',
                    cron: '',
                    params: '',
                    description: ''
                }
            };

            vm.$http.get("/task/groups").then(function (re) {
                vm.taskGroups = re.body.data;
            });

            vm.$http.get("/job-component/list").then(function (re) {
                vm.jobComponentList = re.body.data;
            });

            return data;
        },
        watch: {
            'newTaskFormModel.jobComponent': function (newVal, oldVal) {
                var selectedJobComponent = this.jobComponentList[newVal];
                this.newTaskFormModel.params = selectedJobComponent.paramTemplate;
            }
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
                    state: queryFormModel.status
                };
                vm.load(queryModel);
            },
            load: function (queryModel) {
                var vm = this;

                vm.currentQueryModel = queryModel;

                vm.queryLoading = true;
                vm.taskList = [];
                vm.$http.get("/task/list", {params: queryModel}).then(function (re) {
                    vm.queryLoading = false;
                    vm.taskList = re.body.data;
                }, function () {
                    vm.queryLoading = false;
                    vm.taskList = [];
                });
            },
            reload: function () {
                this.load(this.currentQueryModel);
            },
            postNewTask: function () {
                var vm = this;

                vm.$refs["newTaskForm"].validate(function (valid) {
                    if (valid) {
                        var newTaskFormModel = vm.newTaskFormModel;
                        var formData = new FormData();
                        formData.append("job", newTaskFormModel.jobComponent);
                        formData.append("name", newTaskFormModel.name);
                        formData.append("group", newTaskFormModel.group);
                        formData.append("cron", newTaskFormModel.cron);
                        formData.append("params", newTaskFormModel.params);
                        formData.append("description", newTaskFormModel.description);

                        vm.postNewTaskInProcess = true;
                        vm.$http.post("/task/new", formData).then(function (re) {
                            vm.postNewTaskInProcess = false;
                            vm.newTaskDialogVisible = false;
                            vm.$message({message: '任务创建成功！', type: 'success'});
                            vm.query();
                        }, function () {
                            vm.postNewTaskInProcess = false;
                        });
                    } else {
                        return false;
                    }
                });
            },
            openEditTaskDialog: function (name, group) {
                var vm = this;
                vm.editTaskDialogVisible = true;
                vm.editTaskDialogLoading = true;
                vm.$http.get("/task/detail", {params: {name: name, group: group}}).then(function (re) {
                    var taskDetail = re.body.data;
                    vm.editTaskFormModel.jobComponent = taskDetail.jobName;
                    vm.editTaskFormModel.name = taskDetail.taskName;
                    vm.editTaskFormModel.group = taskDetail.taskGroup;
                    vm.editTaskFormModel.cron = taskDetail.cron;
                    vm.editTaskFormModel.params = taskDetail.params;
                    vm.editTaskFormModel.description = taskDetail.description;

                    vm.editTaskDialogLoading = false;
                });
            },
            postEditTask: function () {
                var vm = this;

                vm.$refs["editTaskForm"].validate(function (valid) {
                    if (valid) {
                        var editTaskFormModel = vm.editTaskFormModel;
                        var formData = new FormData();
                        formData.append("job", editTaskFormModel.jobComponent);
                        formData.append("name", editTaskFormModel.name);
                        formData.append("group", editTaskFormModel.group);
                        formData.append("cron", editTaskFormModel.cron);
                        formData.append("params", editTaskFormModel.params);
                        formData.append("description", editTaskFormModel.description);

                        vm.postEditTaskInProcess = true;
                        vm.$http.post("/task/edit", formData).then(function (re) {
                            vm.postEditTaskInProcess = false;
                            vm.editTaskDialogVisible = false;
                            vm.$message({message: '任务修改成功！', type: 'success'});
                            vm.reload();
                        }, function () {
                            vm.postEditTaskInProcess = false;
                        });
                    } else {
                        return false;
                    }
                });
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
            }
        }
    };
});