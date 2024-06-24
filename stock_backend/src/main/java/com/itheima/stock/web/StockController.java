package com.itheima.stock.web;

import cn.hutool.http.server.HttpServerResponse;
import com.itheima.stock.pojo.domain.InnerMarketDomain;
import com.itheima.stock.pojo.domain.StockUpdownDomain;
import com.itheima.stock.service.StockService;
import com.itheima.stock.vo.resp.PageResult;
import com.itheima.stock.vo.resp.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author by itheima
 * @Date 2021/12/19
 * @Description
 */
@Api(value = "/api/quot", tags = {"统计最新交易日下股票每分钟涨停的数量 */"})
@RestController
@RequestMapping("/api/quot")
public class StockController {

    @Autowired
    private StockService stockService;

    //其它省略.....
    /**
     * 获取国内最新大盘指数
     * @return
     */
    @ApiOperation(value = "获取国内最新大盘指数", notes = "获取国内最新大盘指数", httpMethod = "GET")
    @GetMapping("/index/all")
    public R<List<InnerMarketDomain>> innerIndexAll(){
        return stockService.innerIndexAll();
    }

    /**
     * 分页查询股票最新数据，并按照涨幅排序查询
     * @param page
     * @param pageSize
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = ""),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "")
    })
    @ApiOperation(value = "分页查询股票最新数据，并按照涨幅排序查询", notes = "分页查询股票最新数据，并按照涨幅排序查询", httpMethod = "GET")
    @GetMapping("/stock/all")
    public R<PageResult> getStockPageInfo(@RequestParam(name = "page",required = false,defaultValue = "1") Integer page,
                                          @RequestParam(name = "pageSize",required = false,defaultValue = "20") Integer pageSize){
        return stockService.getStockPageInfo(page,pageSize);
    }

    /*
    统计最新交易日下股票每分钟涨停的数量
    * */

    @ApiOperation(value = "", notes = "", httpMethod = "GET")
    @GetMapping("/stock/updown/count")
    public R<Map> getStockUpdownCount(){
        return stockService.getStockUpdownCount();
    }

    /*涨幅榜功能实现*/
    @ApiOperation(value = "涨幅榜功能实现*/", notes = "涨幅榜功能实现*/", httpMethod = "GET")
    @GetMapping("stock/increase")
    public R<PageResult> getStockIncreaseCount(){
        return stockService.getStockIncreaseCount();
    }

    /*将指定页的股票数据导出到excel表下
    * @param response
    * @param page
    * @param pageSize*/
    @ApiOperation(value = "将指定页的股票数据导出到excel表下", notes = "将指定页的股票数据导出到excel表下", httpMethod = "GET")
    @GetMapping("/stock/export")
    public void stockExport(HttpServletResponse response, Integer page, Integer pageSize){
        stockService.stockExport(response,page,pageSize);
    }


    /**
     * @param
     * @return
     * @author zhangzhaohua
     * @description 统计大盘T日和T-1日每分钟交易量的统计
     * @date 2024/05/21
     */
    @ApiOperation(value = "统计大盘T日和T-1日每分钟交易量的统计", notes = "统计大盘T日和T-1日每分钟交易量的统计", httpMethod = "GET")
    @GetMapping("/stock/tradeAmt")
    public R<Map> stockTradeVol4InnerMarket(){
        return stockService.stockTradeVol4InnerMarket();

    }
    }
