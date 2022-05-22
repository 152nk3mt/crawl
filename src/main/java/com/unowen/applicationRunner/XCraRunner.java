package com.unowen.applicationRunner;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

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
        System.out.println("启动成功");
    }
}
