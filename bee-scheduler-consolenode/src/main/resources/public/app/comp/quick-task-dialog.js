define(['text!comp/quick-task-dialog.html', 'vue'], function (tpl, Vue) {
    return {
        template: tpl,
        components: {},
        props: ['visible'],
        data: function () {
            var vm = this;
            var validators = {
                jobComponent: [
                    {required: true, message: '请选择任务组件', trigger: 'change'}
                ],
                taskName: [
                    {required: true, message: '请输入任务名称', trigger: 'blur'},
                    {required: true, pattern: /^[A-Za-z0-9_]+$/, message: '任务名称只允许使用字母、数字和下划线，请检查', trigger: 'blur'}
                ]
            };
            var data = {
                validators: validators,
                jobComponentList: {},
                postTaskInProcess: false,
                initializing: false,
                editTaskFormModel: {
                    name: '',
                    jobComponent: '',
                    params: '',
                    enableStartDelay: false,
                    startDelay: 1000
                }
            };

            data.initializing = true;
            vm.$http.get("/job-component/list").then(function (re) {
                data.jobComponentList = re.body.data;
                data.initializing = false;
            });

            return data;
        },
        watch: {
            'editTaskFormModel.jobComponent': function (newVal, oldVal) {
                var selectedJobComponent = this.jobComponentList[newVal];
                this.editTaskFormModel.params = selectedJobComponent.paramTemplate;
            },
            'visible': function (newVal) {
                if (newVal) {
                    this.editTaskFormModel.name = this.$moment().format("YYYYMMDDHHmmssSSS");
                }
                this.$emit('update:visible', newVal);
            }
        },
        methods: {
            post: function () {
                var vm = this;

                vm.$refs["editTaskForm"].validate(function (valid) {
                    if (valid) {
                        var editTaskFormModel = vm.editTaskFormModel;
                        vm.postTaskInProcess = true;
                        vm.$http.post("/task/tmp", editTaskFormModel).then(function (re) {
                            vm.$message({message: '任务已触发！', type: 'success'});
                            vm.postTaskInProcess = false;
                            vm.visible = false;
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