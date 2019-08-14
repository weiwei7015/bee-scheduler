define(['text!./ace-editor.html', 'css!./ace-editor.css'], function (tpl) {
    var idCounter = 0;
    return {
        template: tpl,
        props: ['value', 'min-lines', 'max-lines', 'theme', 'mode', 'simple-style', 'read-only'],
        components: {},
        data: function () {
            return {
                editorId: "ace-editor-" + (++idCounter),
                editor: null,
                currentContent: ""
            };
        },
        watch: {
            value: function (newVal, oldVal) {
                var vm = this;
                if (this.currentContent !== newVal) {
                    vm.editor.setValue(newVal);
                    vm.editor.clearSelection();
                }
            }
        },
        mounted: function () {
            var vm = this;
            //prepare configs
            var editorId = vm.editorId;
            var theme = vm.theme || "chrome";
            var mode = vm.mode || "javascript";
            var simpleStyle = vm.simpleStyle !== undefined;
            var readOnly = vm.readOnly !== undefined;
            // var minLines = vm.minLines || 10;
            var minLines = 10;
            var maxLines = vm.maxLines || 30;
            if (maxLines < minLines) {
                maxLines = minLines;
            }
            //load ace module
            require(['ace/ace'], function (ace) {
                //create editor
                var editor = vm.editor = ace.edit(editorId, {
                    value: vm.value,
                    theme: "ace/theme/" + theme,
                    mode: "ace/mode/" + mode,
                    highlightActiveLine: !simpleStyle,
                    showGutter: !simpleStyle,
                    showLineNumbers: !simpleStyle,
                    readOnly: readOnly,
                    minLines: minLines,
                    maxLines: maxLines
                });
                //bind change event
                editor.on("change", function (e) {
                    vm.currentContent = vm.editor.getValue();
                    vm.$emit('input', editor.getValue());
                });
            });
        }
    };
});