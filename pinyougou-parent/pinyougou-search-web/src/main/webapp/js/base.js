var app = angular.module("pinyougou", []);

app.filter("trustHtml",function ($sce) {    //$sce html安全策略
    return function (data) {
        return $sce.trustAsHtml(data);  //过滤器
    }
});