package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 10000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {

        //处理搜索关键字空格
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords",keywords.replace(" ",""));

        Map<String,Object> map = new HashMap<>();

        map.putAll(searchList(searchMap));  //高亮查询列表

        List categoryList = searchCategoryList(searchMap);//查询分类列表
        map.put("categoryList",categoryList);

        //从缓存中查询数据
        if (categoryList.size()>0){
            //显示第一个上面分类名称的品牌和规格数据
            map.putAll(searchBrandAndSpecList((String) categoryList.get(0)));
        }

        return map;
    }

    private Map searchBrandAndSpecList(String categoryName) {
        Map map = new HashMap();

        //根据名称查询分类id
        Long categoryId = (Long) redisTemplate.boundHashOps("itemCat").get(categoryName);

        //根据id查询品牌
        if (categoryId != null){
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(categoryId);
            map.put("brandList",brandList);

            List specList = (List) redisTemplate.boundHashOps("specList").get(categoryId);
            map.put("specList",specList);
        }

        return map;
    }

    private List searchCategoryList(Map searchMap) {
        List list = new ArrayList();

        Query query = new SimpleQuery();

        //查询条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));    //where  keywords
        //query中添加查询条件where
        query.addCriteria(criteria);

        //分类条件
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");       //groupBy item_category
        //query中添加分类条件groupBy
        query.setGroupOptions(groupOptions);

        //获得分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);

        //获取分组结果集对象
        GroupResult<TbItem> groupResult  = page.getGroupResult("item_category");
        //获取分组入口页
        Page<GroupEntry<TbItem>> content = groupResult.getGroupEntries();
        //获取分组入口集合
        for (GroupEntry<TbItem> entity : content) {
            list.add(entity.getGroupValue());
        }

        return list;
    }


    /**
     * spring data solr查询高亮列表
     */
    private Map searchList(Map searchMap) {


        /*        //筛选条件---在搜索引擎中查询数据,根据关键字进行查询,
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);*/


        Map<String, Object> map = new HashMap<>();
        //条件对象
        HighlightQuery query = new SimpleHighlightQuery();

        //高亮总对象
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");        //设置高亮域
        highlightOptions.setSimplePrefix("<em style='color:red'>");  //设置高亮的前缀
        highlightOptions.setSimplePostfix("</em>");    //设置高亮的后缀

        //按关键字搜索
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));    //搜索的域为复制域,is是匹配那个字段

        query.addCriteria(criteria);    //添加检索条件
        query.setHighlightOptions(highlightOptions);    //添加高亮条件

        //按照分类查询
        if (!"".equals(searchMap.get("category"))) {
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        //按照品牌查询
        if (!"".equals(searchMap.get("brand"))) {
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        //按照规格查询
        if (null != searchMap.get("spec")) {
            Map<String,String> specMap = (Map<String, String>) searchMap.get("spec");
            for (String key : specMap.keySet()) {
                Criteria filterCriteria = new Criteria("item_spec_"+key).is(specMap.get(key));
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //按照价格过滤
        if (!"".equals(searchMap.get("price"))) {
            String priceStr = (String) searchMap.get("price");
            String[] price = priceStr.split("-");
            if (!price[0].equals("0")){
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(price[0]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            if (!price[1].equals("*")){
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //按照分页进行查询
        Integer pageNo = (Integer) searchMap.get("pageNo");
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageNo == null)
            pageNo = 1;
        if (pageSize == null)
            pageSize = 30;
        query.setOffset((pageNo-1) * pageSize);   //开始索引
        query.setRows(pageSize);



        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);

        //高亮入口
        List<HighlightEntry<TbItem>> highlighted = page.getHighlighted();
        for (HighlightEntry<TbItem> h : highlighted) {
            TbItem item = h.getEntity();  //真正的实体
            if (h.getHighlights().size()>0 && h.getHighlights().get(0).getSnipplets().size()>0){
                item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));  //设置高亮结果
            }
        }

        map.put("rows",page.getContent());
        map.put("totalPage",page.getTotalPages());   //总页数
        map.put("total",page.getTotalElements());    //总纪录数

        return map;
    }
}
