package com.unowen.applicationRunner;

import com.unowen.util.WXPush;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * @ClassName XCraRunner
 * @Author cy
 * @Date 2021/9/5 15:24
 * @Description TODO
 * @Version 1.0
 **/
@Configuration
public class XCraRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("恭喜", "启动成功");
        WXPush.sendText(paramMap,"@all",null);
    }
}
