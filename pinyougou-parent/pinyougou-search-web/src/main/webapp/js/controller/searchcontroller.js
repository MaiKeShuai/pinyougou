app.controller("searchController",function ($scope,searchService) {

    $scope.searchMap = {};

    $scope.search = function(){
        //搜索
        searchService.search($scope.searchMap).success(function (data) {
            $scope.resultMap = data;    //返回的结果集
        });
    }
});