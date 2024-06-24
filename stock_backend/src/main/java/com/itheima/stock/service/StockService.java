package com.itheima.stock.service;
import com.itheima.stock.pojo.domain.InnerMarketDomain;

import com.itheima.stock.pojo.domain.StockUpdownDomain;
import com.itheima.stock.vo.resp.PageResult;
import com.itheima.stock.vo.resp.R;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author by itheima
 * @Date 2021/12/19
 * @Description 定义股票服务接口
 */
public interface StockService {
    //其它省略......
    /**
     * 获取国内大盘的实时数据
     * @return
     */
    R<List<InnerMarketDomain>> innerIndexAll();

    /**
     * 分页查询股票最新数据，并按照涨幅排序查询
     * @param page
     * @param pageSize
     * @return
     */
    R<PageResult> getStockPageInfo(Integer page, Integer pageSize);

    /*统计最新交易日下股票每分钟涨停的数量*/
    R<Map> getStockUpdownCount();

    /*涨幅榜功能实现*/
    R<PageResult>  getStockIncreaseCount();

    /*
    * 将指定页的股票数据导出到excel下
    * @param response
    * @param page 当前页
    * @param pageSize 每页大小*/
    void stockExport(HttpServletResponse response, Integer page, Integer pageSize);

    /**
     * @param
     * @return
     * @author zhangzhaohua
     * @description 统计大盘T日和T-1日每分钟交易量的统计
     * @date 2024/05/21
     */
    R<Map> stockTradeVol4InnerMarket();
}