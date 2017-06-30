define(['text!view/task-new.html'], function (tpl) {


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
                jobComponentList: {},
                postNewTaskInProcess: false,
                newTaskFormModel: {
                    jobComponent: '',
                    name: '',
                    group: '',
                    scheduleType: 1,
                    scheduleTypeSimpleOptions: {
                        interval: 3000,
                        repeatType: 1,
                        repeatCount: 10
                    },
                    scheduleTypeCalendarIntervalOptions: {
                        interval: 2,
                        intervalUnit: 1
                    },
                    scheduleTypeDailyTimeIntervalOptions: {
                        startTimeOfDay: null,
                        endTimeOfDay: null,
                        daysOfWeek: [],
                        interval: 2,
                        intervalUnit: 1,
                        repeatType: 1,
                        repeatCount: 10
                    },
                    scheduleTypeCronOptions: {
                        cron: ''
                    },
                    startAtType: 1,
                    startAt: null,
                    misfireHandlingType: 1,
                    params: '',
                    description: ''
                }
            };

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
        methods: {
            cancel: function () {
                this.$router.go(-1);
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
            }
        }
    };
});