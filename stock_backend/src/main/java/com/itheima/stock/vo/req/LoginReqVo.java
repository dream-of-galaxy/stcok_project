package com.itheima.stock.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author : itheima
 * @date : 2022/9/19 17:21
 * @description : 登录时请求参数封装vo-value object view-object
 */
@Data
@ApiModel
public class LoginReqVo {
    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    private String username;
    /**
     * 密码
     */
    @ApiModelProperty(value = "明文密码")
    private String password;

    /*验证码*/
    @ApiModelProperty(value = "验证码")
    private String code;

    //会话id
    @ApiModelProperty(value = "会话id")
    private String session;

//    private String rkey;
}
