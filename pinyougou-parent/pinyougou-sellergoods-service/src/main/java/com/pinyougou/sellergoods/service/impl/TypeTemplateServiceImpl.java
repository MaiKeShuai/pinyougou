package com.pinyougou.sellergoods.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.dubbo.common.json.ParseException;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.pojo.TbTypeTemplateExample;
import com.pinyougou.pojo.TbTypeTemplateExample.Criteria;
import com.pinyougou.sellergoods.service.TypeTemplateService;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TbTypeTemplateMapper typeTemplateMapper;

    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询全部
     */
    @Override
    public List<TbTypeTemplate> findAll() {
        return typeTemplateMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageInfo<TbTypeTemplate> findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<TbTypeTemplate> list = typeTemplateMapper.selectByExample(null);
        return new PageInfo<TbTypeTemplate>(list);
    }

    /**
     * 增加
     */
    @Override
    public void add(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.insert(typeTemplate);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.updateByPrimaryKey(typeTemplate);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbTypeTemplate findOne(Long id) {
        return typeTemplateMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            typeTemplateMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageInfo<TbTypeTemplate> findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbTypeTemplateExample example = new TbTypeTemplateExample();
        Criteria criteria = example.createCriteria();

        if (typeTemplate != null) {
            if (typeTemplate.getName() != null && typeTemplate.getName().length() > 0) {
                criteria.andNameLike("%" + typeTemplate.getName() + "%");
            }
            if (typeTemplate.getSpecIds() != null && typeTemplate.getSpecIds().length() > 0) {
                criteria.andSpecIdsLike("%" + typeTemplate.getSpecIds() + "%");
            }
            if (typeTemplate.getBrandIds() != null && typeTemplate.getBrandIds().length() > 0) {
                criteria.andBrandIdsLike("%" + typeTemplate.getBrandIds() + "%");
            }
            if (typeTemplate.getCustomAttributeItems() != null && typeTemplate.getCustomAttributeItems().length() > 0) {
                criteria.andCustomAttributeItemsLike("%" + typeTemplate.getCustomAttributeItems() + "%");
            }

        }

        List<TbTypeTemplate> list = typeTemplateMapper.selectByExample(example);
        saveToRedis();
        //每次查询,都进行缓存
        return new PageInfo<TbTypeTemplate>(list);
    }

    //缓存品牌规格和品牌数据
    private void saveToRedis(){

        List<TbTypeTemplate> tbTypeTemplateList = findAll();
        for (TbTypeTemplate tbTypeTemplate : tbTypeTemplateList) {
            //缓存品牌
            List<Map> brandList = JSON.parseArray(tbTypeTemplate.getBrandIds(), Map.class);
            redisTemplate.boundHashOps("brandList").put(tbTypeTemplate.getId(),brandList);

            //缓存规格,由于规格的数据不够完整,模板中只存有规格的名称,没有详细信息,需要在specificationOption中查询详细信息
            List<Map> specList = findSpecList(tbTypeTemplate.getId());
            redisTemplate.boundHashOps("specList").put(tbTypeTemplate.getId(),specList);
        }
    }


    @Override
    public List<Map> findSpecList(Long id) {
        //根据模板id查询模板
        TbTypeTemplate tbTypeTemplate = typeTemplateMapper.selectByPrimaryKey(id);
        //模板中的规格数据分装到List<Map>中
        List<Map> list = JSON.parseArray(tbTypeTemplate.getSpecIds(), Map.class);
        //遍历list
        for (Map map : list) {
            //获取每一个规格id
//            Long o = (Long) map.get(id);
            //根据规格id进行查询规格选项,    在specificationOption中查询specId=o的

            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            criteria.andSpecIdEqualTo(new Long((Integer) map.get("id")));
            List<TbSpecificationOption> tbSpecificationOptionList = specificationOptionMapper.selectByExample(example);

            //将获取到的规格选项存储到map中
            map.put("option", tbSpecificationOptionList);
        }
        return list;
    }

}
