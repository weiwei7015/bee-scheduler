define(['text!comp/dashboard.html', 'prism'], function (tpl, Prism) {
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
        mounted: function () {
        },
        methods: {
            changeCode: function () {
                this.code = "{\n" +
                    "    \"hello\":\"world\"\n" +
                    "}";

            }
        }
    };
});