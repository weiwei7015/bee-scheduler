require.config({
    // baseUrl: '/public/lib',
    paths: {
        view: '/public/app/component/view',
        part: '/public/app/component/part',
        vue: '/public/lib/vue.min',
        vue_router: '/public/lib/vue-router.min',
        vue_resource: '/public/lib/vue-resource.min',
        ELEMENT: '/public/lib/element-ui-1.3.5/index',
        ace: '/public/lib/ace-1.2.7/lib/ace',
        moment: '/public/lib/moment.min',
        smoothScroll: '/public/lib/smooth-scroll.min',
        text: '/public/lib/text'
    },
    urlArgs: 'v=2017060501'
});

require(['vue', 'vue_router', 'vue_resource', 'ELEMENT', 'moment', 'smoothScroll'], function (Vue, VueRouter, VueResource, Elem, moment, smoothScroll) {

    Vue.config.silent = true;

    //注册Vue组件
    Vue.use(Elem);
    Vue.use(VueRouter);
    Vue.use(VueResource);
    //定义moment作为全局服务
    Vue.prototype.$moment = moment;
    //初始化smooth-scroll插件
    smoothScroll.init({selectorHeader: ".header", speed: 500, easing: 'easeInOutCubic', offset: 40});

    //定义视图组件
    var views = {
        index: function (resolver) {
            require(['view/index'], resolver);
        },
        dashboard: function (resolver) {
            require(['view/dashboard'], resolver);
        },
        taskList: function (resolver) {
            require(['view/task-list'], resolver);
        },
        taskHistoryList: function (resolver) {
            require(['view/task-history-list'], resolver);
        },
        taskHistoryDetail: function (resolver) {
            require(['view/task-history-detail'], resolver);
        },
        error_404: function (resolver) {
            require(['view/error_404'], resolver);
        }
    };

    //配置根路由器
    var router = new VueRouter({
        routes: [
            {path: '/login', component: views.home},
            {
                path: '/', component: views.index,
                children: [
                    {path: '', redirect: '/task/list'},
                    {path: '/dashboard', component: views.dashboard},
                    {path: '/task/list', component: views.taskList},
                    {path: '/task/history/list', component: views.taskHistoryList},
                    {path: '/task/history/detail/:fireId', component: views.taskHistoryDetail},
                    // {path: '', redirect: '/home'},
                    {path: '/manual/:manualName', component: views.manual},
                    {path: '/promo-list/:type?', component: views.promo_list},
                    {path: '/promo/new/:type', component: views.promo_edit, meta: {editFor: "NEW"}},
                    {path: '/promo/edit/:id', component: views.promo_edit, meta: {editFor: "UPDATE"}},
                    {path: '/promo/copy/:id', component: views.promo_edit, meta: {editFor: "COPY"}},
                    {path: '/promo/detail/:id', component: views.promo_detail},
                    {path: '*', component: views.error_404}
                ]
            }
        ]
    });

    //配置根组件
    var app = new Vue({
        router: router,
        data: {
            promoTypes: null,
            contentLoading: false
        },
        methods: {
            setContentLoading: function (flag) {
                this.contentLoading = flag;
            }
        }
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