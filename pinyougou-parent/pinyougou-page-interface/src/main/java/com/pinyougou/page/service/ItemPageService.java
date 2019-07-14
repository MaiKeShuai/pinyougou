package com.pinyougou.page.service;

public interface ItemPageService {
    /**
     * 生成item的静态页面
     * @param goodsId
     * @return
     */
    boolean genItemHtml(Long goodsId);
}
