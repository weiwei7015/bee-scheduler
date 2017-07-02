define(['text!part/helper-dialog.html', 'vue'], function (tpl, Vue) {
    var HelperDialogConstructor = Vue.extend({
        template: tpl,
        components: {},
        data: function () {
            return {
                helperDialogVisible: false
            };
        },
        watch: {
            'helperDialogVisible': function (visible) {
                if (!visible) {
                    this.$el.addEventListener('animationend', this.destroyElement);
                }
            }
        },
        methods: {
            destroyElement: function () {
                this.$el.removeEventListener('animationend', this.destroyElement);
                this.$destroy(true);
                this.$el.parentNode.removeChild(this.$el);
            },
            open: function (title) {
                this.helperDialogVisible = true;
                if (title) {

                }
            }
        }
    });

    var HelperDialog = {
        open: function (title) {
            var helperDialogComponent = new HelperDialogConstructor();
            window.document.body.appendChild((helperDialogComponent).$mount().$el);
            helperDialogComponent.helperDialogVisible = true;
            if (title) {
                console.log(title);
            }
        }
    };
    return HelperDialog;
});