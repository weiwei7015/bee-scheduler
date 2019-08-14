define(['text!comp/dashboard.html'], function (tpl) {
    var aceEditor = function (resolver) {
        require(['comp/ace-editor'], resolver);
    };

    return {
        template: tpl,
        components: {
            "ace-editor": aceEditor
        },
        data: function () {
            var vm = this;
            return {
                code: '{}'
            };
        },
        methods: {
            changeCode: function () {
                this.code = "{'hello':'word'}";

            }
        }
    };
});