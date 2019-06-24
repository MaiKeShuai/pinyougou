package com.pinyougou.sellergoods.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbBrand;

import java.util.List;

/**
 * 商品品牌接口
 */
public interface BrandService {
    /**
     * 查询所有商品品牌,未带分页
     * @return list集合
     */
    List<TbBrand> findAll();

    /**
     * 查询所有的商品品牌,带分页
     * @param pageNum 当前页
     * @param pageSize 每页的数量
     * @return PageInfo
     */
    PageInfo<TbBrand> findAllPage(Integer pageNum,Integer pageSize);

}
