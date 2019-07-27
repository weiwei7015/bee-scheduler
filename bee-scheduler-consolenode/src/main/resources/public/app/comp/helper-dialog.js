define(['text!comp/helper-dialog.html', 'vue'], function (tpl, Vue) {
    var HelperDialogConstructor = Vue.extend({
        template: tpl,
        components: {},
        data: function () {
            return {
                helperDialogVisible: false,
                title: ""
            };
        },
        watch: {
            'helperDialogVisible': function (visible) {
                if (!visible) {
                    this.$el.addEventListener('animationend', this.destroyElement);
                }
            }
        },
        mounted: function () {
        },
        methods: {
            destroyElement: function () {
                this.$el.removeEventListener('animationend', this.destroyElement);
                // this.$destroy(true);
                this.$el.parentNode.removeChild(this.$el);
            }
        }
    });

    var HelperDialog = {
        comp: null,
        open: function (title) {
            if (this.comp === null) {
                this.comp = (new HelperDialogConstructor()).$mount();
            }
            window.document.body.appendChild(this.comp.$el);
            this.comp.helperDialogVisible = true;
            if (title) {
                this.comp.title = title;
            }
        }
    };
    return HelperDialog;
});