package com.unowen.schedul;

import com.unowen.crawl.XCrawl;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;


/**
 * @ClassName XCrawlSchedul
 * @Author cy
 * @Date 2021/9/5 15:41
 * @Description TODO
 * @Version 1.0
 **/
//@Component
public class XCrawlSchedul {

    @Autowired
    private XCrawl xCrawl;

//    @Scheduled(cron = "0/30 * * * * *")
    public void execute(){
        xCrawl.getLi("https://906k.cn");
        System.out.println(new Date());
    }
}
