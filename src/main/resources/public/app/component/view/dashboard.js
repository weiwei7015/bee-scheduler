define(['text!view/dashboard.html', 'ace/ace'], function (tpl, ace) {

    //定义子组件
    var ace_editor = function (resolver) {
        setTimeout(function () {
            require(['part/ace-editor'], resolver);
        },2000);
    };


    return {
        template: tpl,
        components: {
            "ace-editor": ace_editor
        },
        data: function () {
            return {};
        },
        methods: {}
    };
});