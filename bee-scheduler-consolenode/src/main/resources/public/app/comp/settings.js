define(['text!comp/settings.html'], function (tpl, ace) {
    return {
        template: tpl,
        components: {},
        data: function () {
            var vm = this;
            var data = {
                tab: 'first',
                oldPassword: '',
                newPassword: '',
                reNewPassword: '',
                passport: {}
            };
            vm.$http.get("/passport/info").then(function (re) {
                data.passport = re.body;
            });

            return data;
        },
        methods: {
            logout: function () {
                var vm = this;
                this.$confirm('确认退出登录吗?', '提示').then(function () {
                    vm.$http.post("/passport/logout").then(function (re) {
                        vm.$router.push("/login");
                    });
                }).catch(function () {
                });
            },
            updatePassword: function () {
                var vm = this;
                params = {account: vm.passport.account, oldpassword: vm.oldPassword, newpassword: vm.newPassword, renewpassword: vm.reNewPassword};
                vm.$http.post("/user/updatepassword", null, {params: params}).then(function (re) {
                    vm.oldPassword = "";
                    vm.newPassword = "";
                    vm.reNewPassword = "";
                    vm.$alert("密码已更新", {type: "success"});
                });
            }
        }
    };
});