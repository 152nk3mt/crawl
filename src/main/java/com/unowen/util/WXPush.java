package com.unowen.util;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.unowen.constant.SysConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName WXPush
 * @Author cy
 * @Date 2021/6/25 16:19
 * @Description TODO
 * @Version 1.0
 **/
@Component
@Slf4j
public class WXPush {
    @Autowired
    private static RedisService redisService;
    @Autowired
    public void setRedisService(RedisService redisService) {
        WXPush.redisService = redisService;
    }

    //企业ID
    public static String CORPID;
    @Value("${wxpush.corpid}")
    public void setRedisService(String corpid) {
        CORPID = corpid;
    }

    //企业应用的id
    public static Integer AGENTID;
    @Value("${wxpush.agentid}")
    public void setAgentid(Integer agentid) {
        AGENTID = agentid;
    }

    //应用的凭证密钥
    public static String CORPSECRET;
    @Value("${wxpush.corpsecret}")
    public void setCorpsecret(String corpsecret){
        CORPSECRET = corpsecret;
    }

    //get请求
    private static String getTokenUrl = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";

    //post请求
    private static String sendUrl = "https://qyapi.weixin.qq.com/cgi-bin/message/send";

    /**
     *
     * @param map 一个key占一行
     * @return
     */
    public static boolean sendText(Map<String,Object> map, String touser,String toparty) {
        String accessToken = getAccessToken();
        JSONObject text = setContent(map);
        String resp = HttpUtil.post(sendUrl + "?access_token=" + accessToken, oprateParam(touser,toparty, text));
        JSONObject respJson = JSONUtil.parseObj(resp);
        if (respJson.getInt("errcode") != 0) {
            log.error("发送消息失败！");
            log.error(resp);
           return false;
        }
        return true;
    }

    /**
     * 用于组装发送参数
     * @param touser 发给谁
     * @param text 内容
     * @return 拼装后的参数字符串
     */
    private static String oprateParam(String touser,String toparty, Object text) {
        JSONObject param = new JSONObject();
        param.putOnce("msgtype", "text");
        param.putOnce("agentid", AGENTID);
        param.putOnce("text", text);
        if (touser != null)
        param.putOnce("touser", touser);
        if (toparty != null)
        param.putOnce("toparty", toparty);
        return param.toString();
    }

    /**
     * 转换参数：将map转为微信消息类型字符串
     * @param map
     * @return
     */
    private static JSONObject setContent(Map<String,Object> map){
        JSONObject jsonObject = new JSONObject();
        StringBuffer bf = new StringBuffer();
        Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Object> next = iterator.next();
            bf.append(next.getKey()).append("：").append(next.getValue()).append("\n");
        }
        jsonObject.putOnce("content", bf.toString());
        return jsonObject;
    }

    private synchronized static String getAccessToken() {
        if (redisService.exists(SysConstant.WXPushToken)){
            return (String) redisService.get(SysConstant.WXPushToken);
        }
        String accessToken = null;
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("corpid", CORPID);
        paramMap.put("corpsecret", CORPSECRET);
        String resp = HttpUtil.get(getTokenUrl, paramMap);
        JSONObject respJson = JSONUtil.parseObj(resp);
        if (respJson.getInt("errcode") == 0) {
            accessToken = respJson.getStr("access_token");
            Long expiresIn = respJson.getLong("expires_in");
            redisService.set(SysConstant.WXPushToken, accessToken, expiresIn, TimeUnit.SECONDS);
        }else{
            log.error("wxpush获取token失败：" + resp);
        }
        return accessToken;
    }


}