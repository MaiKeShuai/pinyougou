app.controller("searchController", function ($scope, $location,searchService) {

    $scope.searchMap = {keywords: "", category: "", brand: "", spec: {}, price: "", pageNo: 1, pageSize: 30, sortValue:"", sortField:""};

    $scope.search = function () {
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);
        //搜索
        searchService.search($scope.searchMap).success(function (data) {
            $scope.resultMap = data;    //返回的结果集

            buildPageLabel();   //构建分页栏

        });
    };

    //添加过滤选项
    $scope.addSearchItem = function (key, value) {
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

    //构建分页栏
    buildPageLabel = function () {
        $scope.pageLabel = [];//新增分页栏属性
        var maxPageNo = $scope.resultMap.totalPage;//得到最后页码
        var firstPage = 1;//开始页码
        var lastPage = maxPageNo;//截止页码

        $scope.firstDot = true;//前面有点
        $scope.lastDot = true;//后边有点

        if ($scope.resultMap.totalPage > 5) {  //如果总页数大于 5 页,显示部分页码
            if ($scope.searchMap.pageNo <= 3) {//如果当前页小于等于 3

                lastPage = 5; //前 5 页
                $scope.firstDot = false;//前面没点

            } else if ($scope.searchMap.pageNo >= lastPage - 2) {//如果当前页大于等于最大页码-2

                firstPage = maxPageNo - 4;   //后 5 页
                $scope.lastDot = false;//后边没点

            } else { //显示当前页为中心的 5 页

                firstPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;

                $scope.firstDot = false;//前面无点
                $scope.lastDot = false;//后边无点

            }
        } else {
            $scope.firstDot = false;//前面无点
            $scope.lastDot = false;//后边无点
        }
        //循环产生页码标签
        for (var i = firstPage; i <= lastPage; i++) {

            $scope.pageLabel.push(i);

        }
    };

    //根据页码查询
    $scope.queryByPage = function (pageNo) {
        //页码验证
        if (pageNo <= 0 || pageNo > $scope.resultMap.totalPage) {
            return;
        }

        $scope.searchMap.pageNo = pageNo;
        $scope.search();
    };

    $scope.isTopPage = function () {
        if ($scope.searchMap.pageNo == 1) {
            return true
        } else {
            return false
        }
    };

    $scope.isEndPage = function () {
        if ( $scope.searchMap.pageNo == $scope.resultMap.totalPage ) {
            return true;
        } else {
            return false;
        }
    }

    $scope.isActive =function (page) {
        if ($scope.searchMap.pageNo == page) {
            return true;
        } else {
            return false;
        }
    }

    //排序
    $scope.sortSearch = function (sortValue, sortField) {
        $scope.searchMap.sortValue = sortValue;
        $scope.searchMap.sortField = sortField;
        $scope.search();
    }

    //隐藏品牌列表--判断关键字是否是品牌
    $scope.keywordsIsBrand = function () {
        for (var i=0;i<$scope.resultMap.brandList.length;i++ ) {
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text) >= 0) {
                return true;
            }
        }
        return false;
    }

    //接收传递过来的参数,进行搜索
    $scope.loadkeywords = function () {
        $scope.searchMap.keywords = $location.search()['keywords'];
        $scope.search();
    }

});