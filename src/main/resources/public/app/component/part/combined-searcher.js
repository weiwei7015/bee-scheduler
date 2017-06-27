define(['text!part/combined-searcher.html', 'part/product-searcher', 'part/brand-searcher', 'part/category-searcher'], function (tpl, productSearcher, brandSearcher, categorySearcher) {
    return {
        template: tpl,
        props: {
            handleBtnLabel: {default: "加入促销"},
            searchers: {default: ["PRODUCT", "CATEGORY", "BRAND"]}
        },
        components: {
            "product-searcher": productSearcher,
            "brand-searcher": brandSearcher,
            "category-searcher": categorySearcher
        },
        data: function () {
            return {
                activeSearcherType: this.searchers[0]
            };
        },
        watch: {
            activeSearcherType: function (val, oldVal) {
                this.$emit('searcherTypeChange', val, oldVal);
            }
        },
        methods: {
            searcherVisiable: function (type) {
                return this.searchers.indexOf(type) != -1;
            },
            handleProductSelectedItems: function (selectedItems) {
                this.$emit('handle', {type: "PRODUCT", selectedItems: selectedItems});
            },
            handleBrandSelectedItems: function (selectedItems) {
                this.$emit('handle', {type: "BRAND", selectedItems: selectedItems});
            },
            handleCategorySelectedItems: function (selectedItems) {
                this.$emit('handle', {type: "CATEGORY", selectedItems: selectedItems});
            },
            handleWholeSelectedItems: function (selectedItems) {
                this.$emit('handle', {type: "WHOLE_STORE", selectedItems: ['ALL']});
            },
            reset: function () {
                this.$refs['productSearcher'].reset();
                this.$refs['brandSearcher'].reset();
                this.$refs['categorySearcher'].reset();
            }
        }
    };
});