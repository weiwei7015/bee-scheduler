define(['text!comp/settings.html'], function (tpl, ace) {

    return {
        template: tpl,
        components: {},
        data: function () {
            var vm = this;
            var data = {
                meta: {}
            };

            vm.$http.get("/settings/meta").then(function (re) {
                data.meta = re.body.data;
            });
            return data;
        },
        methods: {}
    };
});