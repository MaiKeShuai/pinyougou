package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper brandMapper;

    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    @Override
    public PageInfo<TbBrand> findAllPage(Integer pageNum, Integer pageSize) {

        //带分页查询所有数据
        PageHelper.startPage(pageNum, pageSize);
        List<TbBrand> tbBrandList = brandMapper.selectByExample(null);

        PageInfo<TbBrand> pageInfo = new PageInfo<TbBrand>(tbBrandList);

        pageInfo.setList(tbBrandList);

        System.out.println(pageInfo);

        return pageInfo;
    }

    @Override
    public void add(TbBrand tbBrand) {
        brandMapper.insert(tbBrand);
    }

    @Override
    public TbBrand findById(Long id) {
        TbBrand tbBrand = brandMapper.selectByPrimaryKey(id);
        return tbBrand;
    }

    @Override
    public void update(TbBrand tbBrand) {
        brandMapper.updateByPrimaryKey(tbBrand);
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            brandMapper.deleteByPrimaryKey(id);
        }
    }

    @Override
    public PageInfo<TbBrand> search(TbBrand tbBrand, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);     //分页

        TbBrandExample example = new TbBrandExample();      //添加条件
        TbBrandExample.Criteria criteria = example.createCriteria();

        if (tbBrand != null) {
            if (tbBrand.getName() != null && tbBrand.getName().length() > 0) {
                criteria.andNameLike("%" + tbBrand.getName() + "%");
            }
            if (tbBrand.getFirstChar() != null && tbBrand.getFirstChar().length() > 0) {
                criteria.andFirstCharLike("%" + tbBrand.getFirstChar() + "%");
            }
        }

        List<TbBrand> list = brandMapper.selectByExample(example);
        PageInfo<TbBrand> pageInfo = new PageInfo<TbBrand>(list);

        return pageInfo;
    }

    @Override
    public List<Map> selectOptionList() {
        return brandMapper.selectOptionList();
    }
}
