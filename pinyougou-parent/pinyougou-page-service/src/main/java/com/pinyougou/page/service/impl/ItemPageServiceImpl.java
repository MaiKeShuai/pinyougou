package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Value("${pagedir}")
    private String pagedir;

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Override
    public boolean genItemHtml(Long goodsId) {
        try {
            //创建配置文件对象
            Configuration configuration = freeMarkerConfig.getConfiguration();
            //获取加载模板
            Template template = configuration.getTemplate("item.ftl");      //模板的位置
            //加载模板的数据
            Map map = new HashMap();
            //加载商品列表数据
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
            map.put("goods",tbGoods);


            //测试数据
            map.put("test","测试数据");


            //加载商品扩展列表数据
            TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            map.put("goodsDesc",tbGoodsDesc);
            //创建输出流
            Writer out = new FileWriter(pagedir+goodsId+".html");
            //使用模板生成页面
            template.process(map,out);
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
