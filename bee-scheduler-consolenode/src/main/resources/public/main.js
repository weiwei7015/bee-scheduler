require.config({
    baseUrl: '/public/',
    paths: {
        // view: 'app/component/view',
        // part: 'app/component/part',
        comp: 'app/comp',
        vue: 'lib/vue-2.5.13.min',
        vue_router: 'lib/vue-router-3.0.1.min',
        vue_resource: 'lib/vue-resource-1.5.0.min',
        ELEMENT: 'lib/element-ui-2.11.1/index',
        moment: 'lib/moment-2.22.2.min',
        text: 'lib/text-2.0.15',
        css: 'lib/require-css-0.1.10.min'
    },
    urlArgs: 'v=2019081001'
});

require(['vue', 'vue_router', 'vue_resource', 'ELEMENT', 'moment', 'comp/helper-dialog', 'comp/task-detail-dialog'], function (Vue, VueRouter, VueResource, Elem, moment, helperDialog, taskDetailDialog) {

    Vue.config.silent = true;

    //register plugins
    Vue.use(Elem);
    Vue.use(VueRouter);
    Vue.use(VueResource);
    //defined moment as the global comp
    Vue.prototype.$moment = moment;

    //view comps
    var views = {
        login: function (resolver) {
            require(['comp/login'], resolver);
        },
        index: function (resolver) {
            require(['comp/index'], resolver);
        },
        dashboard: function (resolver) {
            require(['comp/dashboard'], resolver);
        },
        taskList: function (resolver) {
            require(['comp/task-list'], resolver);
        },
        taskEdit: function (resolver) {
            require(['comp/task-edit'], resolver);
        },
        taskHistoryList: function (resolver) {
            require(['comp/task-history-list'], resolver);
        },
        taskHistoryDetail: function (resolver) {
            require(['comp/task-history-detail'], resolver);
        },
        settings: function (resolver) {
            require(['comp/settings'], resolver);
        },
        clusterHome: function (resolver) {
            require(['comp/cluster-home'], resolver);
        },
        help: function (resolver) {
            require(['comp/help'], resolver);
        },
        error_404: function (resolver) {
            require(['comp/error_404'], resolver);
        }
    };

    //global router
    var router = new VueRouter({
        routes: [
            {path: '/login', component: views.login},
            {
                path: '/', component: views.index,
                children: [
                    {path: '', redirect: '/task/list'},
                    {path: '/dashboard', component: views.dashboard},
                    {path: '/task/list', component: views.taskList},
                    {path: '/task/new', component: views.taskEdit, meta: {editFor: "New"}},
                    {path: '/task/edit/:group-:name', component: views.taskEdit, meta: {editFor: "Edit"}},
                    {path: '/task/copy/:group-:name', component: views.taskEdit, meta: {editFor: "Copy"}},
                    {path: '/task/history/list', component: views.taskHistoryList},
                    {path: '/task/history/detail/:fireId', component: views.taskHistoryDetail},
                    {path: '/cluster', component: views.clusterHome},
                    {path: '/settings', component: views.settings},
                    {path: '/help', component: views.help},
                    // {path: '', redirect: '/home'},
                    {path: '*', component: views.error_404}
                ]
            }
        ]
    });

    //帮助组件注册为全局服务
    Vue.prototype.$helperDialog = helperDialog;
    Vue.prototype.$taskDetailDialog = taskDetailDialog;

    // //自适应高度指令
    // Vue.directive('auto-height', {
    //     // 当绑定元素插入到 DOM 中。
    //     inserted: function (el) {
    //         // 聚焦元素
    //         el.style.height = window.document.body.clientHeight;
    //     }
    // });

    //root comp
    //root comp
    var app = new Vue({
        router: router,
        beforeMount: function () {
            var vm = this;
            vm.$http.get("/passport/status").then(function (re) {
                if (re.body !== true) {
                    vm.$router.push("/login");
                }
            });
        }
    });

    //http interceptors
    Vue.http.interceptors.push(
        function (request) {
            request.params["t"] = (new Date()).getTime();
        },
        function (request) {
            return function (response) {
                if (response.status === 0) {
                    app.$alert("请求服务器失败，请稍后再试", '网络异常', {type: "error"});
                }
                if (response.status >= 400) {
                    if (response.status === 401) {
                        app.$alert("您尚未登录或登录信息已过期", '需要登录', {
                            type: "warning",
                            callback: function () {
                                app.$router.push("/login");
                            }
                        });
                    } else {
                        app.$alert(response.body.message, response.body.error, {type: "warning"});
                    }
                }
            };
        }
    );

    app.$http.get("/configs").then(function (re) {
        Vue.prototype.$AppConfig = re.body.data;

        //mount the root comp
        app.$mount("#app");
    });

});