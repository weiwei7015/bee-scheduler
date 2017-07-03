define(['text!view/settings.html'], function (tpl, ace) {

    return {
        template: tpl,
        components: {},
        data: function () {
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