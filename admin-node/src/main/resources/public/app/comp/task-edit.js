define(['text!comp/task-edit.html'], function (tpl) {

    return {
        template: tpl,
        components: {},
        data: function () {
            var vm = this;
            var editFor = vm.$route.meta.editFor;

            var validators = {
                jobComponent: [
                    {required: true, message: '请选择任务组件', trigger: 'change'}
                ],
                taskName: [
                    {required: true, message: '请输入任务名称', trigger: 'blur'}
                ],
                taskGroup: [
                    {required: true, message: '请输入任务所属组', trigger: 'blur'}
                ]
            };

            var data = {
                validators: validators,
                editFor: editFor,
                helpDialogVisible: false,
                jobComponentList: {},
                postTaskInProcess: false,
                initEditFormModelInProcess: false,
                editTaskFormModel: {
                    name: '',
                    group: '',
                    scheduleType: 4,
                    scheduleTypeSimpleOptions: {
                        interval: 30000,
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
                    description: '',
                    linkageRule: ''
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
                if (this.editFor === "New") {
                    var selectedJobComponent = this.jobComponentList[newVal];
                    this.editTaskFormModel.params = selectedJobComponent.paramTemplate;
                }
            }
        },
        methods: {
            routerGoback: function () {
                this.$router.go(-1);
            },
            postNewTask: function () {
                var vm = this;

                vm.$refs["editTaskForm"].validate(function (valid) {
                    if (valid) {
                        var editTaskFormModel = vm.editTaskFormModel;
                        var editFor = vm.editFor;
                        var postUrl = editFor === "New" ? "/task/new" : editFor === "Copy" ? "/task/new" : "/task/edit";
                        vm.postTaskInProcess = true;
                        vm.$http.post(postUrl, editTaskFormModel).then(function (re) {
                            vm.$message({message: '保存成功！', type: 'success'});
                            vm.postTaskInProcess = false;
                            vm.routerGoback();
                        }, function () {
                            vm.postTaskInProcess = false;
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