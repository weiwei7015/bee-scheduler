define(['text!part/brand-searcher.html'], function (tpl) {
    return {
        template: tpl,
        props: {
            handleBtnLabel: {default: "加入促销"}
        },
        data: function () {
            return {
                queryLoading: false,
                selectedItems: [],
                queryModel: {
                    name: null,
                    page: 1,
                    pageSize: 10
                },
                queryResult: {}
            };
        },
        methods: {
            query: function () {
                this.queryModel.page = 1;
                this.doQuery();
            },
            doQuery: function () {
                this.queryLoading = true;
                this.$http.get("/brand/list", {params: this.queryModel}).then(function (re) {
                    this.queryLoading = false;
                    this.queryResult = re.body.data;
                }, function () {
                    this.queryLoading = false;
                    this.queryResult = {};
                });
            },
            changePage: function (val) {
                this.queryModel.page = val;
                this.doQuery();
            },
            setSelectedItems: function (selectedItems) {
                this.selectedItems = selectedItems;
            },
            handleSelectedItems: function () {
                this.$emit('handle', this.selectedItems);
            },
            reset: function () {
                console.log("brand searcher reset！");
            }
        }
    };
});