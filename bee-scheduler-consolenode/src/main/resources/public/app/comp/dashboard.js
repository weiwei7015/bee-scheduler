define(['text!comp/dashboard.html', 'comp/ace-editor'], function (tpl, aceEditor) {
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