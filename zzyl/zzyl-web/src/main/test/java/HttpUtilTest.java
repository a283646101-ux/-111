package com.zzyl.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;

public class HttpUtilTest {
    @Test
    public void testGet() {
        // 发送GET请求
        String result = HttpUtil.get("http://www.baidu.com");
        System.out.println(result);
    }

    @Test
    public void testprojectlist() {
        String url = "http://localhost:9995/nursing_project";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("pageNum", 1);
        paramMap.put("pageSize", 10);
        String result = HttpUtil.get(url, paramMap);
        System.out.println(result);
        @Test
    public void testpost(){
        String url = "http://localhost:9995/nursing_project";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", "护理项目测试1");
        paramMap.put("orderNo", 1);
        paramMap.put("unit", "次");
        paramMap.put("price", 100.0);
        paramMap.put("description", "这是一个护理项目测试1");
        paramMap.put("images", "image1.jpg,image2.jpg");
        String result = HttpUtil.post(url, JSONUtil.toJsonStr(paramMap));
        System.out.println(result);
    }
    }
}
