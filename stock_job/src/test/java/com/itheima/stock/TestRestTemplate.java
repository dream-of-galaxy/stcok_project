package com.itheima.stock;

import com.itheima.stock.service.StockTimerTaskService;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.security.auth.login.AccountException;
import java.util.Map;

/**
 * @author by itheima
 * @Date 2022/1/1
 * @Description
 */
@SpringBootTest
public class TestRestTemplate {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private StockTimerTaskService stockTimerTaskService;

    /**
     * 测试get请求携带url参数，访问外部接口
     */
    @Test
    public void test01(){
        String url="http://localhost:6766/account/getByUserNameAndAddress?userName=itheima&address=shanghai";
        /*
          参数1：url请求地址
          参数2：请求返回的数据类型
         */
        ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);
        //获取响应头
        HttpHeaders headers = result.getHeaders();
        System.out.println(headers.toString());
        //响应状态码
        int statusCode = result.getStatusCodeValue();
        System.out.println(statusCode);
        //响应数据
        String respData = result.getBody();
        System.out.println(respData);
    }
    /*测试响应数据自动封装到vo对象*/
    @Test
    public void test02(){
        String url="http://localhost:6766/account/getByUserNameAndAddress?userName=itheima&address=shanghai";
        /*
        * 参数1:url请求地址
        * 参数2:请求返回的数据类型
        * */
        Account account=restTemplate.getForObject(url,Account.class);
        System.out.println(account);
    }
    @Data
    public static class Account{
        private Integer id ;
        private String userName;
        private String address;
    }

    /*设置头请求设置参数,访问指定接口*/

    @Test
    public void test03(){
        String url="http://localhost:6766/account/getByUserNameAndAddress?userName=itheima&address=shanghai";
        //设置头请求参数
        HttpHeaders headers=new HttpHeaders();
        headers.add("userName","Zhangsan");
        //请求头填充到请求对象下
        HttpEntity<Map> entry = new HttpEntity<>(headers);
        //发送请求
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET,entry, String.class);
        String result = responseEntity.getBody();
        System.out.println(result);
    }

    /*测试国内大盘数据*/
    @Test
    public void testInnerGetMarketInfo(){
        stockTimerTaskService.getInnerMarketInfo();
    }

}