package com.itheima.stock.service.impl;



import com.alibaba.excel.EasyExcel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.stock.mapper.StockRtInfoMapper;
import com.itheima.stock.pojo.domain.InnerMarketDomain;
import com.itheima.stock.mapper.StockBusinessMapper;
import com.itheima.stock.mapper.StockMarketIndexInfoMapper;

import com.itheima.stock.pojo.domain.StockUpdownDomain;
import com.itheima.stock.service.StockService;
import com.itheima.stock.utils.DateTimeUtil;
import com.itheima.stock.pojo.domain.StockInfoConfig;
import com.itheima.stock.vo.resp.PageResult;
import com.itheima.stock.vo.resp.R;
import com.itheima.stock.vo.resp.ResponseCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.*;

import static cn.hutool.core.convert.Convert.toDate;
import static org.reflections.Reflections.log;

/**
 * @author by itheima
 * @Date 2021/12/19
 * @Description
 */
@ApiModel(description = "")
@Service("stockService")
public class StockServiceImpl implements StockService {

    @ApiModelProperty(hidden = true)
    @Autowired
    private StockBusinessMapper stockBusinessMapper;

    @ApiModelProperty(hidden = true)
    @Autowired
    private StockMarketIndexInfoMapper stockMarketIndexInfoMapper;

    @ApiModelProperty(hidden = true)
    @Autowired
    private StockInfoConfig stockInfoConfig;

    @ApiModelProperty(hidden = true)
    @Autowired
    //注入股票的实时信息表
    private StockRtInfoMapper stockRtInfoMapper;


    /**
     * 获取国内大盘的实时数据
     *
     * @return
     */
    @Override
    public R<List<InnerMarketDomain>> innerIndexAll() {
        //1.获取国内A股大盘的id集合
        List<String> inners = stockInfoConfig.getInner();
        //2.获取最近股票交易日期
        Date lastDate = DateTimeUtil.getLastDate4Stock(DateTime.now()).toDate();
        //TODO mock测试数据，后期数据通过第三方接口动态获取实时数据 可删除
        lastDate = DateTime.parse("2022-01-02 09:32:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //3.将获取的java Date传入接口
        List<InnerMarketDomain> list = stockMarketIndexInfoMapper.getMarketInfo(inners, lastDate);
        //4.返回查询结果
        return R.ok(list);
    }

    /**
     * 分页查询股票最新数据，并按照涨幅排序查询
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public R<PageResult> getStockPageInfo(Integer page, Integer pageSize) {
        //1.设置PageHelper分页参数
        PageHelper.startPage(page,pageSize);
        //2.获取当前最新的股票交易时间点
        Date curDate = DateTimeUtil.getLastDate4Stock(DateTime.now()).toDate();
        //todo
        curDate= DateTime.parse("2022-06-07 15:00:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //3.调用mapper接口查询
        List<StockUpdownDomain> infos= stockRtInfoMapper.getNewestStockInfo(curDate);
        if (CollectionUtils.isEmpty(infos)) {
            return R.error(ResponseCode.NO_RESPONSE_DATA);
        }
        //4.组装PageInfo对象，获取分页的具体信息,因为PageInfo包含了丰富的分页信息，而部分分页信息是前端不需要的
        //PageInfo<StockUpdownDomain> pageInfo = new PageInfo<>(infos);
        //PageResult<StockUpdownDomain> pageResult = new PageResult<>(pageInfo);
        PageResult<StockUpdownDomain> pageResult = new PageResult<>(new PageInfo<>(infos));
        //5.封装响应数据
        return R.ok(pageResult);
    }

    /**
     * 统计最新交易日下股票在各个时间点涨跌停的数量
     * @return
     */
    @Override
    public R<Map> getStockUpdownCount() {
        //1.获取最新的交易时间范围 openTime curTime
        //1.1 获取最新股票交易时间点
        DateTime curDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());


        Date curTime = curDateTime.toDate();
        //TODO
        curTime= DateTime.parse("2022-01-06 14:25:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //1.2 获取最新交易时间对应的开盘时间
        DateTime openDate = DateTimeUtil.getOpenDate(curDateTime);
        Date openTime = openDate.toDate();
        //TODO
        openTime= DateTime.parse("2022-01-06 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //2.查询涨停数据
        //约定mapper中flag入参： 1-》涨停数据 0：跌停
        List<Map> upCounts=stockRtInfoMapper.getStockUpdownCount(openTime,curTime,1);
        //3.查询跌停数据
        List<Map> dwCounts=stockRtInfoMapper.getStockUpdownCount(openTime,curTime,0);
        //4.组装数据
        HashMap<String, List> mapInfo = new HashMap<>();
        mapInfo.put("upList",upCounts);
        mapInfo.put("downList",dwCounts);
        //5.返回结果
        return R.ok(mapInfo);
    }

    @Override
    public R<PageResult> getStockIncreaseCount() {
       // R<PageResult> incresePageResult =getStockPageInfo(1,3);
        return R.ok(getStockPageInfo(1,1).getData());
    }

    /*接口实现
    * 将指定页的股票数据导出到excel表下
    * @param response
    * @param page 当前页
    * @param pageSize 每页大小*/
    @Override
    public void stockExport(HttpServletResponse response, Integer page, Integer pageSize) {
        //1.获取最近最新的一次股票有效交易时间点（精确分钟）
        Date curDate = DateTimeUtil.getLastDate4Stock(DateTime.now()).toDate();
        //因为对于当前来说，我们没有实现股票信息实时采集的功能，所以最新时间点下的数据
        //在数据库中是没有的，所以，先临时指定一个假数据,后续注释掉该代码即可
        curDate = DateTime.parse("2022-01-05 09:47:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //2.设置分页参数 底层会拦截mybatis发送的sql，并动态追加limit语句实现分页
        PageHelper.startPage(page, pageSize);
        //3.查询
        List<StockUpdownDomain> infos = stockRtInfoMapper.getAllStockUpDownByTime(curDate);
        response.setCharacterEncoding("utf-8");
        try {
            //2.判断分页数据是否为空，为空则响应json格式的提示信息
            if (CollectionUtils.isEmpty(infos)) {
                R<Object> error = R.error(ResponseCode.NO_RESPONSE_DATA);
                //将error转化成json格式字符串
                String jsonData = new ObjectMapper().writeValueAsString(error);
                //设置响应的数据格式 告知浏览器传入的数据格式
                response.setContentType("application/json");
                //设置编码格式
                //            response.setCharacterEncoding("utf-8");
                //响应数据
                response.getWriter().write(jsonData);
                return;
            }
            //3.调动EasyExcel数据导出
            // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
            response.setContentType("application/vnd.ms-excel");
//        response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("股票涨幅数据表格", "UTF-8");
            //指定excel导出时默认的文件名称，说白了就是告诉浏览器下载文件时默认的名称为：股票涨幅数据表格
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), StockUpdownDomain.class).sheet("股票涨幅信息").doWrite(infos);
        } catch (Exception e) {
            log.error("导出时间：{},当初页码：{}，导出数据量：{}，发生异常信息：{}", curDate, page, pageSize, e.getMessage());
        }

    }

    @Override
    public R<Map> stockTradeVol4InnerMarket() {
         //1.获取T日和T-1日的开始时间和结束时间
        //1.1 获取最近股票有效交易时间点--T日时间范围
        DateTime lastDateTime=DateTimeUtil.getLastDate4Stock(DateTime.now());
        DateTime openDateTime=DateTimeUtil.getOpenDate(lastDateTime);
        //转化成java中Date,这样jdbc默认识别
        Date startTime4T=openDateTime.toDate();
        Date endTime4T = lastDateTime.toDate();
        //TODO mock数据
        startTime4T=DateTime.parse("2022-01-03 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        endTime4T=DateTime.parse("2022-01-03 14:40:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();

        //1.2 获取T-1日的区间范围
        //获取lastDateTime的上一个股票有效交易日
        DateTime preLastDateTime = DateTimeUtil.getPreviousTradingDay(lastDateTime);
        DateTime preOpenDateTime = DateTimeUtil.getOpenDate(preLastDateTime);
        //转化成java中Date,这样jdbc默认识别
        Date startTime4PreT = preOpenDateTime.toDate();
        Date endTime4PreT=preLastDateTime.toDate();
        //TODO  mock数据
        startTime4PreT=DateTime.parse("2022-01-02 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        endTime4PreT=DateTime.parse("2022-01-02 14:40:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();

        //2.获取上证和深证的配置的大盘id
        //2.1 获取大盘的id集合
        List<String> markedIds = stockInfoConfig.getInner();
        //3.分别查询T日和T-1日的交易量数据，得到两个集合
        //3.1 查询T日大盘交易统计数据
        List<Map> data4T=stockMarketIndexInfoMapper.getStockTradeVol(markedIds,startTime4T,endTime4T);
        if (CollectionUtils.isEmpty(data4T)) {
            data4T=new ArrayList<>();
        }
        //3.2 查询T-1日大盘交易统计数据
        List<Map> data4PreT=stockMarketIndexInfoMapper.getStockTradeVol(markedIds,startTime4PreT,endTime4PreT);
        if (CollectionUtils.isEmpty(data4PreT)) {
            data4PreT=new ArrayList<>();
        }
        //4.组装响应数据
        HashMap<String, List> info = new HashMap<>();
        info.put("amtList",data4T);
        info.put("yesAmtList",data4PreT);
        //5.返回数据
        return R.ok(info);
    }
    }




