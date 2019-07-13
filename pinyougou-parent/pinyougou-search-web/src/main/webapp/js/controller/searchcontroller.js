app.controller("searchController",function ($scope,searchService) {

    $scope.searchMap = {keywords:"", category:"", brand:"", spec:{}, price:""};

    $scope.search = function(){
        //搜索
        searchService.search($scope.searchMap).success(function (data) {
            $scope.resultMap = data;    //返回的结果集
        });
    };

    //添加过滤选项
    $scope.addSearchItem = function (key,value) {
        if (key == 'category' || key == "brand" || key == "price") {  //如果点击的是分类或者品牌
            $scope.searchMap[key] = value;
        } else {    //点击规格
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();//执行搜索
    };

    //删除过滤选项
    $scope.removeSearchItem = function (key) {
        if (key == "category" || key == "brand" || key == "price") {
            $scope.searchMap[key] = "";
        } else {
            delete $scope.searchMap.spec[key];
        }
        $scope.search();//执行搜索
    };


});