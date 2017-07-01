define(['text!view/task-edit.html'], function (tpl) {

    return {
        template: tpl,
        components: {},
        data: function () {
            var vm = this;
            var editFor = vm.$route.meta.editFor;

            var validators = {
                jobComponent: [
                    {required: true, message: '请选择Job组件', trigger: 'change'}
                ],
                taskName: [
                    {required: true, message: '请输入任务名称', trigger: 'blur'}
                ]
            };

            var data = {
                validators: validators,
                editFor: editFor,
                helpDialogVisible: false,
                jobComponentList: {},
                postNewTaskInProcess: false,
                initEditFormModelInProcess: false,
                editTaskFormModel: {
                    name: '',
                    group: '',
                    scheduleType: 4,
                    scheduleTypeSimpleOptions: {
                        interval: 3000,
                        repeatType: 1,
                        repeatCount: 10,
                        misfireHandlingType: 0
                    },
                    scheduleTypeCalendarIntervalOptions: {
                        interval: 2,
                        intervalUnit: "HOUR",
                        misfireHandlingType: 0
                    },
                    scheduleTypeDailyTimeIntervalOptions: {
                        startTimeOfDay: null,
                        endTimeOfDay: null,
                        daysOfWeek: [],
                        interval: 2,
                        intervalUnit: "HOUR",
                        misfireHandlingType: 0
                    },
                    scheduleTypeCronOptions: {
                        cron: '',
                        misfireHandlingType: 0
                    },
                    startAtType: 1,
                    startAt: null,
                    endAtType: 1,
                    endAt: null,
                    jobComponent: '',
                    params: '',
                    description: ''
                }
            };


            data.initEditFormModelInProcess = true;
            vm.$http.get("/job-component/list").then(function (re) {
                data.jobComponentList = re.body.data;
                if (editFor === "Edit" || editFor === "Copy") {
                    var name = vm.$route.params.name;
                    var group = vm.$route.params.group;
                    vm.$http.get("/task/detail", {params: {name: name, group: group}}).then(function (re) {
                        data.editTaskFormModel = re.body.data;
                        data.initEditFormModelInProcess = false;
                    });
                } else {
                    data.initEditFormModelInProcess = false;
                }
            });

            return data;
        },
        watch: {
            'editTaskFormModel.jobComponent': function (newVal, oldVal) {
                var selectedJobComponent = this.jobComponentList[newVal];
                this.editTaskFormModel.params = selectedJobComponent.paramTemplate;
            }
        },
        methods: {
            cancel: function () {
                this.$router.go(-1);
            },
            postNewTask: function () {
                var vm = this;

                vm.$refs["editTaskForm"].validate(function (valid) {
                    if (valid) {
                        var editTaskFormModel = vm.editTaskFormModel;
                        vm.postNewTaskInProcess = true;
                        vm.$http.post("/task/new", editTaskFormModel).then(function (re) {
                            vm.postNewTaskInProcess = false;
                            vm.newTaskDialogVisible = false;
                            vm.$message({message: '任务创建成功！', type: 'success'});
                            vm.query();
                        }, function () {
                            vm.postNewTaskInProcess = false;
                        });
                    } else {
                        vm.$message({message: '填写有误，请检查', type: 'warning'});
                        return false;
                    }
                });
            }
        }
    };
});