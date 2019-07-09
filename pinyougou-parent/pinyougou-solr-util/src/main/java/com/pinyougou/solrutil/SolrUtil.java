package com.pinyougou.solrutil;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    public void importItemData(){
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");     //审核通过的
        List<TbItem> tbItemList = itemMapper.selectByExample(example);
        for (TbItem tbItem : tbItemList) {
            System.out.println(tbItem.getId()+"  "+ tbItem.getTitle());
            Map map = JSON.parseObject(tbItem.getSpec(), Map.class);
            tbItem.setSpecMap(map);
        }

        solrTemplate.saveBeans(tbItemList);
        solrTemplate.commit();
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext app = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil = (SolrUtil) app.getBean("solrUtil");
        solrUtil.importItemData();
    }
}
