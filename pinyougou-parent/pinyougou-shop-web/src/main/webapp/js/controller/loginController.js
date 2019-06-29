app.controller("loginController",function ($scope,loginService) {
    $scope.showLoginName = function () {
        loginService.showLoginName().success(function (data) {
            $scope.map = data;
        })
    }
});