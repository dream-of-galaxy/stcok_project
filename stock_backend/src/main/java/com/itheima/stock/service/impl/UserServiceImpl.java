package com.itheima.stock.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.itheima.stock.mapper.SysUserMapper;
import com.itheima.stock.pojo.entity.SysUser;
import com.itheima.stock.service.UserService;
import com.itheima.stock.utils.IdWorker;
import com.itheima.stock.vo.req.LoginReqVo;
import com.itheima.stock.vo.resp.LoginRespVo;
import com.itheima.stock.vo.resp.R;
import com.itheima.stock.vo.resp.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author : itheima
 * @date : 2022/9/19 16:23
 * @description :
 */
@Service("userService")
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 根据用户名称查询用户信息
     * @param userName 用户名称
     * @return
     */
    @Override
    public SysUser getUserByUserName(String userName) {
        return sysUserMapper.getUserByUserName(userName);
    }

    /**
     * 用户登录功能
     * @param reqVo
     * @return
     */
    @Override
    public R<LoginRespVo> login(LoginReqVo reqVo) {
        //判断输入参数的合法性
        if (reqVo==null || StringUtils.isBlank(reqVo.getUsername()) || StringUtils.isBlank(reqVo.getPassword())) {
            return R.error(ResponseCode.USERNAME_OR_PASSWORD_ERROR);
        }
        System.out.println("11111");
        //根据用户名查询用户信息
        SysUser dbUser = sysUserMapper.getUserByUserName(reqVo.getUsername());
        System.out.println("22222");
        //判断用户是否存在
        if (dbUser==null || ! passwordEncoder.matches(reqVo.getPassword(),dbUser.getPassword())) {
            return R.error(ResponseCode.USERNAME_OR_PASSWORD_ERROR);
        }
        System.out.println("33333");
        //构建响应相对
        LoginRespVo respVo = new LoginRespVo();
//        respVo.setId(dbUser.getId());
//        respVo.setNickName(dbUser.getNickName());
        //我们发现respVo与dbUser下具有相同的属性，所以直接复制即可
        BeanUtils.copyProperties(dbUser,respVo);
        System.out.println("4444");
        return R.ok(respVo);
    }

    @Override
    public R<Map> getCaptchaCode() {
        //参数分别是宽,搞,验证码长度,干扰线数量
        LineCaptcha captcha=CaptchaUtil.createLineCaptcha(250,40,1,2);
        //设置背景颜色清灰
        captcha.setBackground(Color.lightGray);
        //获取图片中的验证码,默认生成的校验码包含文字和数字,长度为5
        String checkCode = captcha.getCode();
        //获取经过base64编码处理的图片数据
        String imageData = captcha.getImageBase64();
        //2.生成sessionId
        /*为什么要转换成String,因为这个sessionId以后要传给前端,session过
        * 长传给前端会造成精度丢失,转换成String避免精度丢失*/
        String sessionId = String.valueOf(idWorker.nextId());
        log.info("当前生成的图片校验码:{},会话id:{}",checkCode,sessionId);
        //3.将sessionId作为key,校验码作为value保存在redis中
        //使用redis模拟session的行为,通过过期时间设置
        redisTemplate.opsForValue().set("CK"+sessionId,checkCode,5, TimeUnit.MINUTES);
        //4.组装数据,给前端返回
        Map<String,String> data=new HashMap();
        data.put("imageData",imageData);
        data.put("sessionId",sessionId);
        //5.响应数据
        return R.ok(data);


    }





}
