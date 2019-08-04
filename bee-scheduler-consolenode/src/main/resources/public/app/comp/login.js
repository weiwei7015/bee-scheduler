define(['text!./login.html'], function (tpl) {
    return {
        template: tpl,
        components: {},
        data: function () {
            return {
                account: "",
                password: "",
                errorMsg: "",
                loginProcessing: false
            };
        },
        methods: {
            login: function () {
                var vm = this;
                var form = new FormData();
                form.append("account", vm.account);
                form.append("password", vm.password);

                vm.loginProcessing = true;
                vm.$http.post("/passport/login", form).then(function (re) {
                    vm.loginProcessing = false;
                    if (re.body === 1) {
                        vm.errorMsg = "";
                        vm.$router.push("/");
                    } else {
                        vm.errorMsg = "用户名或密码有误";
                    }
                }, function () {
                    vm.loginProcessing = false;
                });
            }
        }
    };
});