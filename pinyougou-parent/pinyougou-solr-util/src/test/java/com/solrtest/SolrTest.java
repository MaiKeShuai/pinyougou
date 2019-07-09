package com.solrtest;

import com.pinyougou.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-solr.xml")
public class SolrTest {
    @Autowired
    private SolrTemplate solrTemplate;

    @Test   //添加数据
    public void test1(){
        TbItem tbItem = new TbItem();
        tbItem.setId(1L);
        tbItem.setTitle("solr添加数据测试开始");
        tbItem.setPrice(new BigDecimal(123123.23));

        solrTemplate.saveBean(tbItem);
        solrTemplate.commit();
    }

    @Test   //按主键查询
    public void test2(){
        TbItem byId = solrTemplate.getById(1, TbItem.class);
        System.out.println("byId = " + byId);
    }

    @Test   //主键删除
    public void test3(){
        solrTemplate.deleteById("1");
        solrTemplate.commit();
    }

    @Test   //分页查询第一步,存储一百条数据
    public void test4(){
        //首先循环插入 100 条测试数据
        List<TbItem> list = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            TbItem item=new TbItem();
            item.setId(1L+i);
            item.setBrand("华为"+i);
            item.setCategory("手机"+i);
            item.setGoodsId(1L+i);
            item.setSeller("华为 2 号专卖店"+i);
            item.setTitle("华为 Mate9"+i);
            item.setPrice(new BigDecimal(2000+i));
            list.add(item);
        }
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Test   //第二步,查询数据,添加查询条件
    public void test4_1(){
        SimpleQuery query = new SimpleQuery("*:*");


        //添加查询条件
        Criteria criteria = new Criteria("item_title").contains("1");   //查询字段中包含哪先字符
        criteria = criteria.and("item_title").contains("5");
        criteria = criteria.and("item_title").contains("9");
        //将筛选条件添加到查询(query)中
        query.addCriteria(criteria);


        query.setOffset(0);    //开始索引(默认0)
        query.setRows(20);      //每页的纪录数
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);       //每页的所有信息,相当于pageInfo

        System.out.println("总纪录数: "+page.getTotalElements());
        System.out.println("总页数"+page.getTotalPages());

        List<TbItem> content = page.getContent();   //获取每页的对象中的数据

        for (TbItem tbItem : content) {
            System.out.println("tbItem = " + tbItem);
        }
    }

    @Test   //删除全部数据
    public void test5(){
        Query query = new SimpleQuery("*:*");   //*:*表示所有
        solrTemplate.delete(query);
        solrTemplate.commit();
    }


}
