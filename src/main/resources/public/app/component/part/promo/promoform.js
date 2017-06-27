define(['text!part/promo/promoform.html'], function (tpl) {

    //定义子组件
    var promo_form_parts = {
        base: function (resolver) {
            require(['part/promo/promoform-part-base'], resolver);
        },
        rule: function (resolver) {
            require(['part/promo/promoform-part-rule'], resolver);
        },
        product: function (resolver) {
            require(['part/promo/promoform-part-product'], resolver);
        }
    };

    return {
        template: tpl,
        components: {
            "promoform-part-base": promo_form_parts.base,
            "promoform-part-rule": promo_form_parts.rule,
            "promoform-part-product": promo_form_parts.product
        },
        computed: {
            form: function () {
                return this.$refs["form"];
            },
            showRulePart: function () {
                return this.partRuleOptions.benefitItem || this.partRuleOptions.loopItem || this.partRuleOptions.couponForbbidenItem;
            }
        },
        props: ["part-base-options", "part-rule-options", "part-product-options", 'init-data'],
        data: function () {
            var vm = this;

            var data = {
                backUpInputModel: {},
                inputModel: {
                    id: null,
                    promoTypeId: null,
                    scopes: [],
                    name: '',
                    linkName: '',
                    linkUrl: '',
                    remark: '',
                    labelId: null,
                    promoStartTime: null,
                    promoEndTime: null,
                    benefits: [],
                    loop: false,
                    couponDisable: false,
                    targetType: "PRODUCT",
                    targets: []
                }
            };

            if (vm.initData) {
                data.inputModel = vm.initData;
            }

            data.backUpInputModel = JSON.stringify(data.inputModel);

            return data;
        },
        methods: {
            reset: function () {
                this.form.resetFields();
                // this.inputModel.targets = [];
                // this.inputModel.gifts = [];
                // this.inputModel.exchangeProducts = [];


                this.inputModel = JSON.parse(this.backUpInputModel);

            }
        }
    };
});