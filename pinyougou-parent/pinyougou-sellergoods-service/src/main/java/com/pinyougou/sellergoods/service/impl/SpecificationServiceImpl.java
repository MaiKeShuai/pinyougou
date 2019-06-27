package com.pinyougou.sellergoods.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojo.grop.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.sellergoods.service.SpecificationService;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private TbSpecificationMapper specificationMapper;

    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbSpecification> findAll() {
        return specificationMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageInfo<TbSpecification> findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<TbSpecification> list = specificationMapper.selectByExample(null);
        return new PageInfo<TbSpecification>(list);
    }

    /**
     * 增加
     */
    @Override
    public void add(Specification specification) {
        //添加规格      回传id
        specificationMapper.insert(specification.getSpecification());

        //根据添加进去的规格回传的id进行添加规格选项
        for (TbSpecificationOption tbSpecificationOption : specification.getSpecificationOptionList()) {
            //给规格列表中设置规格id
            tbSpecificationOption.setSpecId(specification.getSpecification().getId());

            //保存规格选项
            specificationOptionMapper.insert(tbSpecificationOption);
        }
    }


    /**
     * 修改
     */
    @Override
    public void update(Specification specification) {

        //删除原来规格选项
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        example.createCriteria().andSpecIdEqualTo(specification.getSpecification().getId());
        specificationOptionMapper.deleteByExample(example);

        //获取规格实体---修改规格数据
        TbSpecification tbSpecification = specification.getSpecification();
        specificationMapper.updateByPrimaryKey(tbSpecification);

        //调用添加方法,重新添加一条数据
        for (TbSpecificationOption tbSpecificationOption : specification.getSpecificationOptionList()) {
            tbSpecificationOption.setSpecId(tbSpecification.getId());   //设置规格id
            specificationOptionMapper.insert(tbSpecificationOption);    //存储新的规格选项
        }
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Specification findOne(Long id) {

        //根据id查询tbSpecification对象
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
        //根据id查询tbSpecificationOption对象
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(id);
        List<TbSpecificationOption> tbSpecificationOptionList = specificationOptionMapper.selectByExample(example);
        //封装Specification对象
        Specification specification = new Specification();
        specification.setSpecification(tbSpecification);
        specification.setSpecificationOptionList(tbSpecificationOptionList);

        return specification;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            //根据id删除规格数据
            specificationMapper.deleteByPrimaryKey(id);
            //根据id=SpecId删除option中的数据
            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria().andSpecIdEqualTo(id);
            specificationOptionMapper.deleteByExample(example);
        }
    }


    @Override
    public PageInfo<TbSpecification> findPage(TbSpecification specification, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);    //分页

        TbSpecificationExample example = new TbSpecificationExample();
        Criteria criteria = example.createCriteria();

        if (specification != null) {    //筛选框中有数据,添加筛选条件
            if (specification.getSpecName() != null && specification.getSpecName().length() > 0) {
                criteria.andSpecNameLike("%" + specification.getSpecName() + "%");
            }
        }

        List<TbSpecification> list = specificationMapper.selectByExample(example);

        return new PageInfo<TbSpecification>(list);

    }

}
