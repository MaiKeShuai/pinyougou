package com.pinyougou.sellergoods.service.impl;

import java.util.List;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojo.TbSpecificationOptionExample.Criteria;
import com.pinyougou.sellergoods.service.SpecificationOptionService;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SpecificationOptionServiceImpl implements SpecificationOptionService {

    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    @Override
    public List<TbSpecificationOption> findAll() {
        return specificationOptionMapper.selectByExample(null);
    }

    @Override
    public PageInfo<TbSpecificationOption> findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<TbSpecificationOption> list = specificationOptionMapper.selectByExample(null);
        return new PageInfo<TbSpecificationOption>(list);
    }

    @Override
    public void add(TbSpecificationOption specificationOption) {
        specificationOptionMapper.insert(specificationOption);
    }

    @Override
    public void update(TbSpecificationOption specificationOption) {
        specificationOptionMapper.updateByPrimaryKey(specificationOption);
    }

    @Override
    public TbSpecificationOption findOne(Long id) {
        return specificationOptionMapper.selectByPrimaryKey(id);
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            specificationOptionMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageInfo<TbSpecificationOption> findPage(TbSpecificationOption specificationOption, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        Criteria criteria = example.createCriteria();

        if (specificationOption != null) {
            if (specificationOption.getOptionName() != null && specificationOption.getOptionName().length() > 0) {
                criteria.andOptionNameLike("%" + specificationOption.getOptionName() + "%");
            }

        }

        List<TbSpecificationOption> list = specificationOptionMapper.selectByExample(example);
        return new PageInfo<TbSpecificationOption>(list);
    }

}
