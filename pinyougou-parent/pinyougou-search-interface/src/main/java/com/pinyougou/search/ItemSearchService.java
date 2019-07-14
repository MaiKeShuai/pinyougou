package com.pinyougou.search;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
    Map<String,Object> search(Map map);

    public void importList(List list);

    public void deleteByGoodsIds(List goodsIdList);
}
