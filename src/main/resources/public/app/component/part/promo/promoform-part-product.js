define(['text!part/promo/promoform-part-product.html', 'part/product-searcher', 'part/brand-searcher', 'part/category-searcher', 'part/combined-searcher'], function (tpl, productSearcher, brandSearcher, categorySearcher, combinedSearcher) {
    return {
        template: tpl,
        props: ["input-model", "options"],
        components: {
            "product-searcher": productSearcher,
            "brand-searcher": brandSearcher,
            "category-searcher": categorySearcher,
            "combined-searcher": combinedSearcher
        },
        data: function () {
            var vm = this, options = this.options;

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
                thresholdValidator: options.benefitThresholdType === "NUMBER" ? numberValidator : options.benefitThresholdType === "MONEY" ? priceValidator : normalThresholdValidator,
                promiseValidator: options.benefitPromiseType === "NUMBER" ? numberValidator : options.benefitPromiseType === "MONEY" ? priceValidator : normalPromiseValidator,
                priceValidator: priceValidator,
                numberValidator: numberValidator
            };

            if (options.subProductSetter) {
                if (vm.inputModel.benefits.length === 0) {
                    vm.inputModel.benefits.push({threshold: null, promise: []});
                }
            }

            return {
                validators: validators
            };
        },
        computed: {
            benefitThresholdMaxInputLength: function () {
                return this.options.benefitThresholdType === "NUMBER" ? 5 : this.options.benefitThresholdType === "MONEY" ? 11 : 11;
            },
            benefitPromiseMaxInputLength: function () {
                return this.options.benefitPromiseType === "NUMBER" ? 5 : this.options.benefitPromiseType === "MONEY" ? 11 : 11;
            },
            hasExtendDomainItems: function () {
                return this.options.benefitItem || this.options.productSloganItem || this.options.promoStockItem || this.options.purchaseLimitItem || this.options.couponForbbidenItem || this.options.promoPictureItem || this.options.memberRankPriceItem;
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
            getDomainImg: function () {
                return this.$AppConfig.domain.img;
            },
            removeTargetPromoPic: function (targetId) {
                var vm = this;
                for (var i = 0; i < vm.inputModel.targets.length; i++) {
                    var targetItem = vm.inputModel.targets[i];
                    if (targetItem.id === targetId) {
                        targetItem.extend.promoPicture = null;
                        break;
                    }
                }
            },
            uploadTargetPromoPic: function (elUpload) {
                var vm = this;
                var targetId = elUpload.data;
                var formData = new FormData();
                formData.append("file", elUpload.file);
                this.$http.post("/upload/pic", formData).then(function (re) {
                    var picPath = re.body.data;
                    for (var i = 0; i < vm.inputModel.targets.length; i++) {
                        var targetItem = vm.inputModel.targets[i];
                        if (targetItem.id === targetId) {
                            targetItem.extend.promoPicture = picPath;
                            break;
                        }
                    }
                });
            },
            addBenefitsItem: function (target) {
                target.extend.benefits.push({threshold: null, promise: null});
            },
            removeBenefitsItem: function (target, item) {
                target.extend.benefits.splice(target.extend.benefits.indexOf(item), 1);
            },
            handleSubProductSelected: function (data) {
                var vm = this, globalBenefitPromise = this.inputModel.benefits[0].promise;
                if (globalBenefitPromise === null) {
                    this.inputModel.benefits[0].promise = globalBenefitPromise = [];
                }
                if (vm.options.subProductSetterType === "GIFT") {
                    data.forEach(function (selectedItem) {
                        var flag = false;
                        for (var i in globalBenefitPromise) {
                            var tmp = globalBenefitPromise[i];
                            if (tmp.proId === selectedItem.id) {
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            globalBenefitPromise.push({proId: selectedItem.id, proName: selectedItem.name, proPrice: selectedItem.shopPrice, costPrice: selectedItem.costPrice, proStatus: selectedItem.onSaleType, giftCount: 1});
                        }
                    });
                } else if (vm.options.subProductSetterType === "FT") {
                    data.forEach(function (selectedItem) {
                        var flag = false;
                        for (var i in globalBenefitPromise) {
                            var tmp = globalBenefitPromise[i];
                            if (tmp.proId === selectedItem.id) {
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            globalBenefitPromise.push({proId: selectedItem.id, proName: selectedItem.name, proPrice: selectedItem.shopPrice, costPrice: selectedItem.costPrice, proStatus: selectedItem.onSaleType, exchangePrice: null});
                        }
                    });
                }
            },
            handleMainProductSelected: function (type, selectedItems) {
                var vm = this;
                if (vm.inputModel.targetType !== type) {
                    vm.inputModel.targets = [];
                    vm.inputModel.targetType = type;
                }

                if (selectedItems.length > 0) {
                    selectedItems.forEach(function (selectedItem) {
                        var flag = true;
                        for (var i in vm.inputModel.targets) {
                            if (vm.inputModel.targets[i].id === selectedItem.id) {
                                flag = false;
                                break;
                            }
                        }
                        if (flag) {
                            switch (type) {
                                case "PRODUCT":
                                    var targetItem = {
                                        id: selectedItem.id,
                                        name: selectedItem.name,
                                        price: selectedItem.shopPrice,
                                        costPrice: selectedItem.costPrice,
                                        status: selectedItem.onSaleType,
                                        extend: {
                                            couponDisable: false,
                                            memberRankPrice: null,
                                            productSlogan: '',
                                            enablePromoStock: false,
                                            promoStock: null,
                                            enablePurchaseLimit: false,
                                            purchaseLimit: null,
                                            promoPicture: null,
                                            benefits: []
                                        }
                                    };
                                    if (vm.options.memberRankPriceItem) {
                                        targetItem.extend.memberRankPrice = [null, null, null, null, null];
                                        // targetItem.extend.benefits.push({threshold: null, promise: null});
                                        // targetItem.extend.benefits.push({threshold: null, promise: null});
                                        // targetItem.extend.benefits.push({threshold: null, promise: null});
                                        // targetItem.extend.benefits.push({threshold: null, promise: null});
                                        // targetItem.extend.benefits.push({threshold: null, promise: null});
                                    }
                                    if (vm.options.benefitItem) {
                                        targetItem.extend.benefits.push({threshold: null, promise: null});
                                    }
                                    vm.inputModel.targets.push(targetItem);
                                    break;
                                case "BRAND":
                                    vm.inputModel.targets.push({id: selectedItem.id, name: selectedItem.name});
                                    break;
                                case "CATEGORY":
                                    vm.inputModel.targets.push({id: selectedItem.id, name: selectedItem.name});
                                    break;
                                case "WHOLE_STORE":
                                    vm.inputModel.targets = ["ALL"];
                                    break;
                            }
                        }
                    });
                }
            },
            removeSubProduct: function (item) {
                var vm = this, globalBenefitPromise = this.inputModel.benefits[0].promise;
                globalBenefitPromise.splice(globalBenefitPromise.indexOf(item), 1);
            },
            removeMainProduct: function (item) {
                var targets = this.inputModel.targets;
                targets.splice(targets.indexOf(item), 1);
            },
            resetTarget: function () {
                this.inputModel.targets = [];
            }
        }
    };
});