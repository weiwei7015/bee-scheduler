define(['text!./ace-editor.html', 'css!./ace-editor.css'], function (tpl) {
    var idCounter = 0;
    return {
        template: tpl,
        props: {
            value: {
                type: String,
                required: true
            },
            language: {
                type: String,
                required: true
            },
            minLines: {
                type: Number,
                default: 5
            },
            maxLines: {
                type: Number,
                default: 30
            },
            theme: {
                type: String,
                default: 'eclipse'
            }
        },
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
                if (vm.editor != null && this.currentContent !== newVal) {
                    vm.editor.setValue(newVal);
                    vm.editor.clearSelection();
                }
            }
        },
        mounted: function () {
            var vm = this;
            //prepare configs
            if (vm.maxLines < vm.minLines) {
                vm.maxLines = vm.minLines;
            }
            //load ace module
            require(['ace/ace'], function (ace) {
                //create editor
                var editor = window.editor = vm.editor = ace.edit(vm.editorId, {
                    value: vm.value,
                    theme: "ace/theme/" + vm.theme,
                    mode: "ace/mode/" + vm.language,
                    highlightActiveLine: false,
                    showGutter: true,
                    showLineNumbers: true,
                    readOnly: false,
                    showFoldWidgets: false,
                    showPrintMargin: false,
                    displayIndentGuides: false,
                    fadeFoldWidgets: false,
                    minLines: vm.minLines,
                    maxLines: vm.maxLines
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