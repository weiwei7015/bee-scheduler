define(['text!part/category-searcher.html'], function (tpl) {
    return {
        template: tpl,
        props: {
            handleBtnLabel: {default: "加入促销"},
            searchers: {default: ["PRODUCT", "CATEGORY", "BRAND", "WHOLE_STORE"]}
        },
        data: function () {
            var data = {
                queryLoading: false,
                selectedItems: [],
                queryResult: []
            };
            this.query();
            return data;
        },
        methods: {
            query: function () {
                this.queryLoading = true;
                this.$http.get("/category/list").then(function (re) {
                    this.queryLoading = false;
                    this.queryResult = re.body.data;
                }, function () {
                    this.queryLoading = false;
                    this.queryResult = [];
                });
            },
            handleSelectedItems: function () {
                this.$emit('handle', this.selectedItems);
            },
            reset: function () {
                console.log("category searcher reset！");
            }
        }
    };
});