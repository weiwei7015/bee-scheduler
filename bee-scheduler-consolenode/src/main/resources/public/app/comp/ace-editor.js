define(['text!./ace-editor.html', 'ace/ace'], function (tpl, ace) {
    var idCounter = 0;
    return {
        template: tpl,
        props: ['value', 'height', 'theme', 'mode', 'simple-style'],
        components: {},
        data: function () {
            var data = {
                editorId: "ace-editor",
                contentBackup: ""
            };

            idCounter = idCounter + 1;
            data.editorId = "ace-editor-" + (idCounter);
            return data;
        },
        watch: {
            value: function (newVal, oldVal) {
                var vm = this;
                if (this.contentBackup !== newVal) {
                    vm.editor.setValue(newVal);
                    vm.editor.clearSelection();
                }
            }
        },
        mounted: function () {
            var vm = this;
            var editorId = vm.editorId;


            var theme = vm.theme || "", height = vm.height || "300", mode = vm.mode || "javascript";

            //初始化容器高度
            window.document.querySelector("#" + editorId).style.height = height + "px";

            //初始化编辑器
            var editor = vm.editor = ace.edit(editorId, {
                theme: "ace/theme/" + theme,
                value: vm.value,
                highlightActiveLine: vm.simpleStyle,
                showGutter: vm.simpleStyle,
                mode: "ace/mode/" + mode
            });
            // editor.setTheme("ace/theme/" + theme);
            // editor.setValue(vm.value);
            // if (vm.simpleStyle) {
            //     editor.setHighlightActiveLine(false);
            //     editor.renderer.setShowGutter(false);
            // }
            // editor.getSession().setMode("ace/mode/" + mode);
            editor.$blockScrolling = Infinity;


            vm.editor.on("change", function (e) {
                vm.contentBackup = vm.editor.getValue();
                vm.$emit('input', vm.editor.getValue());
            });
        }
    };
});