app.controller("brandController", function ($scope, brandService) {
    //分页开始------------------
    $scope.paginationConf1 = {
        currentPage: 1,		//当前页
        totalItems: 200,		//总条数
        itemsPerPage: 8,	//每页显示条数
        perPageOptions: [8, 18, 28, 38, 48],	//页码选项
        onChange: function () {
            $scope.reloadList();//重新加载
        }
    };

    $scope.reloadList = function () {
        $scope.search($scope.paginationConf1.currentPage, $scope.paginationConf1.itemsPerPage);

    };

    $scope.findPage = function (page, size) {
        brandService.findPage(page,size).success(function (data) {
            $scope.list = data.list;
            $scope.paginationConf1.totalItems = data.total;
        })
    };
    //分页结束------------------
    //添加数据,或者修改数据开始----
    $scope.save = function () {

        var object = null;
        if ($scope.entity.id != null) {	//如果有id执行修改方法
            object = brandService.update($scope.entity);//执行修改方法
        } else {
            object = brandService.add($scope.entity);
        }

        object.success(function (data) {
            if (data.success) {	//成功
                $scope.reloadList();	//重新加载
            } else {
                alert(data.message);
            }
        })
    };
    //添加数据,或者修改数据结束---
    //数据的回显开始------------
    $scope.findById = function (id) {
        brandService.findById(id).success(function (data) {
            $scope.entity = data;
        })
    };
    //数据的回显结束------------
    //删除开始-----------------
    $scope.ids = [];    //勾选id的集合
    $scope.updateSelectIds = function ($event, id) {
        if ($event.target.checked) {
            $scope.ids.push(id);    //向数组中添加一条数据
        } else {
            var idIndex = $scope.ids.indexOf(id);    //查询id在ids数组中的索引
            $scope.ids.splice(idIndex, 1);   //从数组中删除指定元素,第一个参数,删除元素的所有位置,第二个参数,删除索引位置开始的几个元素
        }
    };

    $scope.delete = function () {
        brandService.delete($scope.ids).success(function (data) {
            if (data.success) {
                $scope.reloadList();
            } else {
                alert(data.message);
            }
        })
    };
    //删除结束-----------------
    //条件查询开始-------------
    $scope.searchEntity = {};
    $scope.search = function (page, size) {
        brandService.search(page, size, $scope.searchEntity).success(function (data) {
            $scope.list = data.list;
            $scope.paginationConf1.totalItems = data.total;
        });
    }
    //条件查询结束-------------
});