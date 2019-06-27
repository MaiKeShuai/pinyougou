app.service("brandService", function ($http) {
    this.findPage = function (page, size) {
        return $http.get("../brand/findAllPage.do?page=" + page + "&size=" + size);
    };

    this.findById = function (id) {
        return $http.get("../brand/findById.do?id=" + id);
    };

    this.add = function (entity) {
        return $http.post("../brand/add.do", entity);
    };

    this.update = function (entity) {
        return $http.post("../brand/update.do", entity);
    };

    this.delete = function (ids) {
        return $http.get("../brand/delete.do?ids=" + ids);
    };

    this.search = function (page, size, searchEntity) {
        return $http.post("../brand/search.do?page=" + page + "&size=" + size, searchEntity);
    };
});