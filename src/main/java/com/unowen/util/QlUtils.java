package com.unowen.util;

import cn.hutool.core.io.file.FileAppender;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class QlUtils {
    @Autowired
    private static String baseUrl;

//    static {
//        baseUrl = "1.14.252.104:7501";
//        clientId = "ASAF-2_Evq1R";
//        clientSecret = "6QHoToI7gZ-NAVm5kqvxNOm9";
//    }

    @Autowired
    private static String clientId;

    @Autowired
    private static String clientSecret;

    @Value("${ql.baseUrl}")
    public void setBaseUrl(String baseUrl) {
        baseUrl = baseUrl;
    }

    @Value("${ql.clientId}")
    public void setClientId(String clientId) {
        clientId = clientId;
    }

    @Value("${ql.clientSecret}")
    public void setClientSecret(String clientSecret) {
        clientSecret = clientSecret;
    }

    public static String getToken() throws Exception {
        String getCkUrl = baseUrl + "/open/auth/token";
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("client_id", clientId);
        paramMap.put("client_secret", clientSecret);
        String ckRtn = HttpUtil.get(getCkUrl, paramMap, 5000);

        JSONObject ckJson = JSON.parseObject(ckRtn);
        if (HttpStatus.HTTP_OK != ckJson.getInteger("code")) {
            System.out.println("请求失败：" + ckJson.getString("message"));
            throw new Exception("请求失败：" + ckJson.getString("message"));
        } else {
            JSONObject dataJson = ckJson.getJSONObject("data");
            return dataJson.getString("token");
        }
    }

    public static List<Map> getEnv(String token) throws Exception {
        if (token == null) {
            throw new Exception("token不能为空");
        }
        String getEnvUrl = baseUrl + "/open/envs";
        HttpRequest httpRequest = HttpRequest.get(getEnvUrl);//
        httpRequest.header("Authorization", "Bearer " + token);
        String body = httpRequest.timeout(3000).execute().body();
        System.out.println("body:" + body);
        JSONObject ckArrJsonRtn = JSON.parseObject(body);
        if (HttpStatus.HTTP_OK != ckArrJsonRtn.getInteger("code")) {
            throw new Exception("请求失败：" + ckArrJsonRtn.getString("message"));
        }
        JSONArray ckArrJson = ckArrJsonRtn.getJSONArray("data");
        ArrayList<Map> envList = new ArrayList<Map>();
        FileAppender appender = new FileAppender(new File("env.txt"), 16, true);
        appender.append(StrUtil.format("路径：{}", baseUrl));
        appender.append(StrUtil.format("token：{}", token));

        for (Object o : ckArrJson) {
            JSONObject envJson = JSON.parseObject(o.toString());
            HashMap<String, String> env = new HashMap<String, String>();
            env.put("id", envJson.getString("id"));
            env.put("name", envJson.getString("name"));
            env.put("value", envJson.getString("value"));
            env.put("remarks", envJson.getString("remarks"));
            envList.add(env);

            appender.append(StrUtil.format("name=>{}", envJson.getString("name")));
            appender.append(StrUtil.format("value=>{}", envJson.getString("value")));
            appender.append(StrUtil.format("remarks=>{}", envJson.getString("remarks")));
            appender.append("~~~~~~~~~~~~~~~~~");
        }
        appender.flush();
        return envList;
    }

    public static boolean addEnv(String baseUrl, String token, List<Map> arr) {
        boolean addFlag = false;
        String addUrl = baseUrl + "/open/envs";
        HttpRequest httpRequest = HttpRequest.post(addUrl);
        httpRequest.body(JSON.toJSONString(arr));
        httpRequest.header("Authorization", "Bearer " + token);
        String body = httpRequest.timeout(3000).execute().body();
        JSONObject ckArrJsonRtn = JSON.parseObject(body);

        if (HttpStatus.HTTP_OK != ckArrJsonRtn.getInteger("code")) {
            System.out.println("添加失败");
        } else {
            addFlag = true;
            System.out.println("添加成功");
        }
        return addFlag;
    }

    public static boolean delEnv(String baseUrl, String token, List<String> arr) throws Exception {
        boolean addFlag = false;
        String addUrl = baseUrl + "/open/envs";
        HttpRequest httpRequest = HttpRequest.delete(addUrl);
        httpRequest.header("Authorization", "Bearer " + token);

        httpRequest.body(JSON.toJSONString(arr));
        String body = httpRequest.timeout(3000).execute().body();
        JSONObject ckArrJsonRtn = JSON.parseObject(body);

        if (HttpStatus.HTTP_OK != ckArrJsonRtn.getInteger("code")) {
            throw new Exception("删除失败");
        } else {
            System.out.println("删除成功");
        }
        return addFlag;
    }

}
