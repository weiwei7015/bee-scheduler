define(['text!part/promo/promoform-part-base.html'], function (tpl) {
    return {
        template: tpl,
        props: ["input-model", "options"],
        data: function () {
            var vm = this;

            // if (!vm.options.scopeItem) {
            //     vm.inputModel.scopes = ['PC', 'APP', 'WAP'];
            // }

            var validators = {
                scopes: [{type: 'array', required: true, message: '请至少选择一个促销平台', trigger: 'change'}],
                name: [
                    {required: true, message: '请输入活动名称', trigger: 'blur'},
                    {pattern: /\S+/, message: '请输入活动名称', trigger: 'blur'}
                ],
                linkName: [{
                    validator: function (rule, value, callback) {
                        if (value === '' && vm.inputModel.linkUrl !== '') {
                            callback(new Error('链接名称和链接地址需同时设置或不设置'));
                        } else {
                            callback();
                        }
                    },
                    trigger: 'blur'
                }],
                linkUrl: [
                    {
                        trigger: 'blur',
                        validator: function (rule, value, callback) {
                            if (value !== '' && !/^http(s)?:\/\/\S+\.\S+$/.test(value)) {
                                callback(new Error('您输入的链接地址有误'));
                            } else {
                                callback();
                            }
                        }
                    },
                    {
                        trigger: 'blur',
                        validator: function (rule, value, callback) {
                            if (value === '' && vm.inputModel.linkName !== '') {
                                callback(new Error('链接名称和链接地址需同时设置或不设置'));
                            } else {
                                callback();
                            }
                        }
                    }],
                labelId: {type: 'number', required: true, message: '请选择活动标签', trigger: 'change'},
                time: [
                    {
                        type: 'array', required: true, len: 2, message: '请设置活动时间', trigger: 'blur',
                        fields: {
                            0: {type: "date", required: true, message: '请设置活动开始时间'},
                            1: {type: "date", required: true, message: '请设置活动结束时间'}
                        }
                    },
                    {
                        type: 'array',
                        trigger: 'blur',
                        validator: function (rule, value, callback) {
                            if (value[0].getTime() >= value[1].getTime()) {
                                callback(new Error('开始时间必须小于结束时间'));
                            } else {
                                callback();
                            }
                        }
                    }
                ],
                promoStartTime: [
                    {type: "date", trigger: 'blur', required: true, message: '请设置活动开始时间'},
                    {
                        trigger: 'blur',
                        validator: function (rule, value, callback) {
                            if (value && vm.inputModel.promoEndTime && value.getTime() >= vm.inputModel.promoEndTime.getTime()) {
                                callback(new Error('开始时间必须小于结束时间'));
                            } else {
                                callback();
                            }
                        }
                    }
                ],
                promoEndTime: [
                    {type: "date", trigger: 'blur', required: true, message: '请设置活动结束时间'},
                    {
                        trigger: 'blur',
                        validator: function (rule, value, callback) {
                            if (value && vm.inputModel.promoStartTime && vm.inputModel.promoStartTime.getTime() >= value.getTime()) {
                                callback(new Error('开始时间必须小于结束时间'));
                            } else {
                                callback();
                            }
                        }
                    }
                ]
            };

            return {
                validators: validators,
                datePickerOptions: {
                    shortcuts: [
                        {
                            text: '一周',
                            onClick: function (picker) {
                                picker.$emit('pick', [vm.$moment().startOf('day').add(1, 'days').toDate(), vm.$moment().add(7, 'days').endOf('day').toDate()]);
                            }
                        }, {
                            text: '一个月',
                            onClick: function (picker) {
                                picker.$emit('pick', [vm.$moment().startOf('day').add(1, 'days').toDate(), vm.$moment().add(30, 'days').endOf('day').toDate()]);
                            }
                        }, {
                            text: '三个月',
                            onClick: function (picker) {
                                picker.$emit('pick', [vm.$moment().startOf('day').add(1, 'days').toDate(), vm.$moment().add(90, 'days').endOf('day').toDate()]);
                            }
                        }
                    ]
                }
            }
        }
    };
});