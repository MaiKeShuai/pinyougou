//控制层
app.controller('goodsController', function ($scope, $controller, $location, goodsService, dfsService, item_CatService, type_TemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    };

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.list;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };

    //查询实体
    $scope.findOne = function () {
        var id = $location.search()['id'];

        if (id == null) {
            return;
        }

        goodsService.findOne(id).success(function (response) {
            $scope.entity = response;

            //向富文本编辑器中添加回显数据
            editor.html($scope.entity.tbGoodsDesc.introduction);

            //向图片列表回显图片数据
            $scope.entity.tbGoodsDesc.itemImages = JSON.parse($scope.entity.tbGoodsDesc.itemImages);

            //读取商品扩展属性
            $scope.entity.tbGoodsDesc.customAttributeItems = JSON.parse($scope.entity.tbGoodsDesc.customAttributeItems);

            //读取规格
            $scope.entity.tbGoodsDesc.specificationItems = JSON.parse($scope.entity.tbGoodsDesc.specificationItems);

            //sku列表的展示
            for (var i = 0; i < $scope.entity.itemList.length; i++) {
                $scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
            }
        });
    };

    //根据规格名称和选项名称返回是否被勾选
    $scope.checkAttributeValue = function(specName,optionName){
        var items = $scope.entity.tbGoodsDesc.specificationItems;

        var object = $scope.searchObjectByKey(items,'attributeName',specName);

        if (object == null) {
            return false;
        } else {
            if (object.attributeValue.indexOf(optionName) >= 0){
                return true;
            } else {
                return false;
            }
        }
    }

    //保存数据
    $scope.add = function () {
        $scope.entity.tbGoodsDesc.introduction = editor.html();
        goodsService.add($scope.entity).success(function (response) {
            if (response.success) {
                alert("新增成功!");
                $scope.entity = {};
                editor.html("");    //清空文本编辑器的内容
            } else {
                alert(response.message);
            }
        });
    };


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    };
    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.list;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };

    //文件的上传
    $scope.upload = function () {
        dfsService.upload().success(function (data) {
            if (data.success) {
                $scope.image_entity.url = data.message;
            } else {
                alert(data.message);
            }
        })
    };

    $scope.entity = {tbGoods: {}, tbGoodsDesc: {itemImages: []}};
    //添加图片列表
    $scope.add_image_entity = function () {
        $scope.entity.tbGoodsDesc.itemImages.push({color: $scope.color, url: $scope.image_entity.url});
    };

    //删除列表
    $scope.remove_image_entity = function (index) {
        $scope.entity.tbGoodsDesc.itemImages.splice(index, 1);
    };

    //读取一级分类
    $scope.selectItemCat1List = function () {
        item_CatService.findByParentId(0).success(function (data) {
            $scope.itemCaty1List = data;
        });
    };

    //读取二级分类
    $scope.$watch("entity.tbGoods.category1Id", function (newValue, oldValue) {
        //根据新改变的值查询二级分类
        item_CatService.findByParentId(newValue).success(function (data) {
            $scope.itemCaty2List = data;
        });
    });

    //读取三级分类
    $scope.$watch("entity.tbGoods.category2Id", function (newValue, oldValue) {
        //二级分类的值改变,三级分类
        item_CatService.findByParentId(newValue).success(function (data) {
            $scope.itemCaty3List = data;
        });
    });

    //三级分类选择完毕,则进行查询模板
    $scope.$watch("entity.tbGoods.category3Id", function (newValue, oldValue) {
        item_CatService.findOne(newValue).success(function (data) {
            $scope.entity.tbGoods.typeTemplateId = data.typeId;
        });
    });

    //监听模板id的更新,查询模板id中的品牌列表
    $scope.$watch("entity.tbGoods.typeTemplateId", function (newValue, oldValue) {
        type_TemplateService.findOne(newValue).success(function (data) {
            $scope.typeTemplate = data;
            $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);
            //模板id的值改变,还需要进行加载扩展属性
            
            //回显新增,如果没有id才进行加载,
            if ($location.search()['id'] == null) {
                $scope.entity.tbGoodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
            }
        });

        //根据模板id查询规格选项列表
        type_TemplateService.findSpecList(newValue).success(function (data) {
            $scope.specList = data;
        })
    });

    //保存选中的规格选项
    $scope.entity = {tbGoodsDesc: {itemImages: [], specificationItems: []}};

    $scope.updateSpecAttribute = function ($event, name, value) {
        var object = $scope.searchObjectByKey($scope.entity.tbGoodsDesc.specificationItems, 'attributeName', name);
        if (object != null) {
            if ($event.target.checked) {     //当前checkBox是选中状态
                object.attributeValue.push(value);
            } else {    //取消选中
                object.attributeValue.splice(object.attributeValue.indexOf(value), 1);   //移除选项
                //如果选项都取消了,则将此条纪录移除
                if (object.attributeValue.length == 0) {
                    $scope.entity.tbGoodsDesc.specificationItems.splice($scope.entity.tbGoodsDesc.specificationItems.indexOf(object), 1);
                }
            }
        } else {
            $scope.entity.tbGoodsDesc.specificationItems.push({"attributeName": name, "attributeValue": [value]});
        }
    };


    //商品录入SKU商品信息
    $scope.createTbItem = function () {
        //初始化
        $scope.entity.itemList = [{spec: {}, price: 0, num: 99999, status: '0', isDefault: '0'}];

        var items = $scope.entity.tbGoodsDesc.specificationItems;

        for (var i = 0; i < items.length; i++) {        //遍历得到的对象{"attributeName":"网络","attributeValue":["移动3G","移动4G"]}
            $scope.entity.itemList = addColumn($scope.entity.itemList, items[i].attributeName, items[i].attributeValue);
        }
    }

    addColumn = function (list, columnName, columnValues) {
        var newList = [];//新集合
        for (var i = 0; i < list.length; i++) {
            var oldRow = list[i];
            for (var j = 0; j < columnValues.length; j++) {
                var newRow = JSON.parse(JSON.stringify(oldRow));//深克隆
                newRow.spec[columnName] = columnValues[j];
                newList.push(newRow);
            }

        }
        return newList;
    }

    //显示订单的状态信息
    $scope.status=['未审核','已审核','审核未通过','已关闭'];  //商品的状态

    //显示分类的名称

    //定义商品分类列表
    $scope.itemCatList = [];

    $scope.findItemCatList = function () {
        item_CatService.findAll().success(function (data) {
            for (var i = 0; i < data.length; i++) {
                $scope.itemCatList[data[i].id] = data[i].name;
            }
        });
    }

    //修改后,保存
    $scope.save = function () {
        //提取富文本编辑器中的数据
        $scope.entity.tbGoodsDesc.introduction = editor.html();

        var serviceObject;  //服务层对象
        
        if ($scope.entity.tbGoods.id != null) {     //如果有id
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            serviceObject = goodsService.add($scope.entity);    //添加一条数据
        }

        serviceObject.success(function (data) {
           if (data.success){
               alert("保存成功!")
               $scope.entity={};//置空对象
               editor.html(""); //清空文本
               location.href = "goods.html";
           } else {
               alert(data.message);
           }
        });
    }
});	
