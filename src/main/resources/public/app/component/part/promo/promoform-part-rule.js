define(['text!part/promo/promoform-part-rule.html'], function (tpl) {
    return {
        template: tpl,
        props: ["input-model", "options"],
        data: function () {
            var vm = this;

            var numberValidator = {
                required: true,
                trigger: 'blur',
                validator: function (rule, value, callback) {
                    if (!/^\d+$/.test(value)) {
                        callback(new Error('数量输入有误'));
                    } else if (Number(value) <= 0) {
                        callback(new Error('数量需大于0'));
                    } else {
                        callback();
                    }
                }
            };

            var priceValidator = {
                required: true,
                trigger: 'blur',
                validator: function (rule, value, callback) {
                    if (!/^\d{1,8}(\.\d{1,2})?$/.test(value)) {
                        callback(new Error('金额输入有误'));
                    } else if (Number(value) <= 0) {
                        callback(new Error('金额需大于0'));
                    } else {
                        callback();
                    }
                }
            };

            var normalThresholdValidator = {
                required: true,
                trigger: 'blur',
                validator: function (rule, value, callback) {
                    if (!/^\d{1,8}(\.\d{1,2})?$/.test(value)) {
                        callback(new Error('优惠条件输入有误'));
                    } else if (Number(value) <= 0) {
                        callback(new Error('优惠条件需大于0'));
                    } else {
                        callback();
                    }
                }
            };
            var normalPromiseValidator = {
                required: true,
                trigger: 'blur',
                validator: function (rule, value, callback) {
                    if (!/^\d{1,8}(\.\d{1,2})?$/.test(value)) {
                        callback(new Error('优惠力度输入有误'));
                    } else if (Number(value) <= 0) {
                        callback(new Error('优惠力度需大于0'));
                    } else {
                        callback();
                    }
                }
            };

            var validators = {
                thresholdValidator: vm.options.benefitThresholdType === "NUMBER" ? numberValidator : vm.options.benefitThresholdType === "MONEY" ? priceValidator : normalThresholdValidator,
                promiseValidator: vm.options.benefitPromiseType === "NUMBER" ? numberValidator : vm.options.benefitPromiseType === "MONEY" ? priceValidator : normalPromiseValidator
            };

            if (vm.options.benefitItem && vm.inputModel.benefits.length === 0) {
                vm.inputModel.benefits.push({threshold: null, promise: null});
            }

            return {
                validators: validators
            }
        },
        computed: {
            benefitThresholdMaxInputLength: function () {
                return this.options.benefitThresholdType === "NUMBER" ? 5 : this.options.benefitThresholdType === "MONEY" ? 11 : 11;
            },
            benefitPromiseMaxInputLength: function () {
                return this.options.benefitPromiseType === "NUMBER" ? 5 : this.options.benefitPromiseType === "MONEY" ? 11 : 11;
            },
            benefitItemLayoutResult: function () {
                var benefitItemLayout = this.options.benefitItemLayout;
                var benefitItemLayoutResult = [];
                var layoutGroup = benefitItemLayout.split("=>");

                if (layoutGroup.length === 1) {
                    var prefix$suffix = layoutGroup[0].split("[-]");
                    benefitItemLayoutResult.push({type: "threshold", "prefix": prefix$suffix[0], "suffix": prefix$suffix[1] || ""});
                } else if (layoutGroup.length === 2) {
                    if (layoutGroup[0].length === 0) {
                        var prefix$suffix = layoutGroup[1].split("[-]");
                        benefitItemLayoutResult.push({type: "promise", "prefix": prefix$suffix[0], "suffix": prefix$suffix[1] || ""});
                    } else {
                        var threshold_prefix$suffix = layoutGroup[0].split("[-]");
                        var promise_prefix$suffix = layoutGroup[1].split("[-]");
                        benefitItemLayoutResult.push({type: "threshold", "prefix": threshold_prefix$suffix[0], "suffix": threshold_prefix$suffix[1] || ""});
                        benefitItemLayoutResult.push({type: "promise", "prefix": promise_prefix$suffix[0], "suffix": promise_prefix$suffix[1] || ""});
                    }
                }
                // for (var i = 0; i < layoutGroup.length; i++) {
                //     var prefix$suffix = layoutGroup[i].split("[-]");
                //     benefitItemLayoutResult.push({"prefix": prefix$suffix[0], "suffix": prefix$suffix[1] || ""});
                // }
                return benefitItemLayoutResult;
            }
        },
        methods: {
            addBenefitsItem: function () {
                var items = this.inputModel.benefits;
                items.push({threshold: null, promise: null});
            },
            removeBenefitsItem: function (item) {
                var items = this.inputModel.benefits;
                items.splice(items.indexOf(item), 1);
            }
        }
    };
});