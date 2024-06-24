package com.itheima.stock.service;

import com.itheima.stock.pojo.entity.SysUser;
import com.itheima.stock.vo.req.LoginReqVo;
import com.itheima.stock.vo.resp.LoginRespVo;
import com.itheima.stock.vo.resp.R;

import java.util.Map;

/**
 * @author : itheima
 * @date : 2022/9/19 16:22
 * @description : 定义操纵用户的服务接口
 */
public interface UserService {
    /**
     * 根据账户名称查询用户信息
     * @param userName
     * @return
     */
    SysUser getUserByUserName(String userName);

    /**
     * 用户登录功能实现
     * @param vo
     * @return
     */
    R<LoginRespVo> login(LoginReqVo vo);


    /**
     * 登录校验码生成服务方法
     * @return
     */
    R<Map> getCaptchaCode();
}
