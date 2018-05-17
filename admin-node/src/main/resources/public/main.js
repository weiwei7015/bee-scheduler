require.config({
    // baseUrl: '/public/lib',
    paths: {
        // view: '/public/app/component/view',
        // part: '/public/app/component/part',
        comp: '/public/app/comp',
        vue: '/public/lib/vue.min',
        vue_router: '/public/lib/vue-router.min',
        vue_resource: '/public/lib/vue-resource.min',
        ELEMENT: '/public/lib/element-ui-2.3.8/index',
        moment: '/public/lib/moment.min',
        text: '/public/lib/text'
    },
    urlArgs: 'v=2018051701'
});

require(['vue', 'vue_router', 'vue_resource', 'ELEMENT', 'moment', 'comp/helper-dialog', 'comp/task-detail-dialog'], function (Vue, VueRouter, VueResource, Elem, moment, helperDialog, taskDetailDialog) {

    Vue.config.silent = true;

    //注册Vue组件
    Vue.use(Elem);
    Vue.use(VueRouter);
    Vue.use(VueResource);
    //定义moment作为全局服务
    Vue.prototype.$moment = moment;

    //定义视图组件
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

    //配置根路由器
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

    //配置根组件
    var app = new Vue({
        router: router
    });

    //Http拦截器
    Vue.http.interceptors.push(function (request, next) {
        request.params["t"] = (new Date()).getTime();
        next();
    });
    Vue.http.interceptors.push(function (request, next) {
        next(function (response) {
            if (response.status !== 200) {
                // if (response.body.code <= 200) {
                //     if (response.body.code === 102) {
                //         app.$alert(response.body.msg, '消息', {
                //             type: "warning",
                //             confirmButtonText: '前往登录',
                //             callback: function (action) {
                //                 window.location = response.body.data;
                //             }
                //         });
                //     } else {
                //         app.$alert(response.body.msg, '消息', {type: "warning", confirmButtonText: '确定'});
                //     }
                // }
                app.$alert(response.body.message, '消息', {type: "warning", confirmButtonText: '确定'});
            }

        });
    });

    app.$http.get("/configs").then(function (re) {
        Vue.prototype.$AppConfig = re.body.data;

        //渲染根组件
        app.$mount("#app");
    });

});