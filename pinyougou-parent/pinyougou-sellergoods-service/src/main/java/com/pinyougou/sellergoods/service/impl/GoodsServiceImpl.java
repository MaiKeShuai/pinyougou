package com.pinyougou.sellergoods.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojo.grop.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbBrandMapper brandMapper;

    @Autowired
    private TbSellerMapper sellerMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageInfo<TbGoods> findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<TbGoods> list = goodsMapper.selectByExample(null);
        return new PageInfo<TbGoods>(list);
    }

    /**
     * 增加
     */
    @Override
    public void add(Goods goods) {
        //添加tbGoods //设置初始化商品的状态
        goods.getTbGoods().setAuditStatus("0");
        goodsMapper.insert(goods.getTbGoods());
        //添加tbGoodsDesc
        goods.getTbGoodsDesc().setGoodsId(goods.getTbGoods().getId());
        goodsDescMapper.insert(goods.getTbGoodsDesc());


        if ("1".equals(goods.getTbGoods().getIsEnableSpec())) {     //启用规格

            List<TbItem> itemList = goods.getItemList();
            for (TbItem tbItem : itemList) {

                //商品标题 spu名称+规格选项值
                String title = goods.getTbGoods().getGoodsName();   //spu名称
                Map<String, Object> map = JSON.parseObject(tbItem.getSpec());

                for (String s : map.keySet()) {
                    title += "," + map.get(s);
                }
                tbItem.setTitle(title);

                setItemValue(goods, tbItem);

                itemMapper.insert(tbItem);
            }
        } else {        //不启用规格
            TbItem item = new TbItem();
            item.setTitle(goods.getTbGoods().getGoodsName());   //sku名称
            item.setPrice(goods.getTbGoods().getPrice());   //价格
            item.setStatus("1");
            item.setIsDefault("1");
            item.setNum(999);
            item.setSpec("{}");
            setItemValue(goods, item);

            itemMapper.insert(item);
        }
    }

    private void setItemValue(Goods goods, TbItem tbItem) {
        //存储图片
        List<Map> imgs = JSON.parseArray(goods.getTbGoodsDesc().getItemImages(), Map.class);
        for (Map img : imgs) {
            tbItem.setImage((String) img.get("url"));
        }

        //商品分类
        tbItem.setCategoryid(goods.getTbGoods().getCategory3Id());
        //创建日期
        tbItem.setCreateTime(new Date());
        //更新日期
        tbItem.setUpdateTime(new Date());
        //goodsId
        tbItem.setGoodsId(goods.getTbGoods().getId());
        //sellerId
        tbItem.setSellerId(goods.getTbGoods().getSellerId());
        //分类名称
        TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(goods.getTbGoods().getCategory3Id());
        tbItem.setCategory(tbItemCat.getName());
        //品牌名称
        TbBrand tbBrand = brandMapper.selectByPrimaryKey(goods.getTbGoods().getBrandId());
        tbItem.setBrand(tbBrand.getName());
        //商家名称(店铺名称)
        TbSeller tbSeller = sellerMapper.selectByPrimaryKey(goods.getTbGoods().getSellerId());
        tbItem.setSeller(tbSeller.getNickName());
    }


    /**
     * 修改
     */
    @Override
    public void update(TbGoods goods) {
        goodsMapper.updateByPrimaryKey(goods);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbGoods findOne(Long id) {
        return goodsMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            goodsMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageInfo<TbGoods> findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();

        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                criteria.andSellerIdLike(goods.getSellerId());  //精准匹配
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
                criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
            }

        }

        List<TbGoods> list = goodsMapper.selectByExample(example);
        return new PageInfo<TbGoods>(list);
    }

}
