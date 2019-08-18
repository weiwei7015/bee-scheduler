define(['text!./task-detail-dialog.html', 'vue', 'prism', 'css!./task-detail-dialog.css'], function (tpl, Vue, Prism) {
    var TaskDetailDialogConstructor = Vue.extend({
        template: tpl,
        props: ['name', 'group'],
        components: {},
        data: function () {
            var vm = this;
            var data = {
                dialogVisible: false,
                loading: true,
                taskDetail: {}
            };
            vm.$http.get("/task/detail", {params: {name: vm.name, group: vm.group}}).then(function (re) {
                data.taskDetail = re.body;
                data.loading = false;
            }, function () {
                this.taskDetail = null;
                data.loading = false;
            });
            return data;
        },
        watch: {
            'dialogVisible': function (visible) {
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
                this.$destroy(true);
                this.$el.parentNode.removeChild(this.$el);
            }
        }
    });

    return {
        comp: null,
        open: function (name, group) {
            this.comp = (new TaskDetailDialogConstructor({propsData: {name: name, group: group}})).$mount();
            window.document.body.appendChild(this.comp.$el);
            this.comp.dialogVisible = true;
        }
    };
});