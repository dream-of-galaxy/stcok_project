package com.itheima.stock.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author by itheima
 * @Date 2021/12/30
 * @Description
 */
@ApiModel(description = "")
@ConfigurationProperties(prefix = "stock")
@Data
public class StockInfoConfig {
    //A股大盘ID集合
    @ApiModelProperty("A股大盘ID集合")
    private List<String> inner;
    //外盘ID集合
    @ApiModelProperty("外盘ID集合")
    private List<String> outer;

    /*股票涨幅区间标题集合*/
    @ApiModelProperty("股票涨幅区间标题集合*/")
    private List<String> upDownRange;

    /*大盘 外盘 个股 的公共URL*/
    @ApiModelProperty("大盘 外盘 个股 的公共URL*/")
    private String marketUrl;

    /*板块采集URL*/
    @ApiModelProperty("板块采集URL*/")
    private String blockUrl;

}