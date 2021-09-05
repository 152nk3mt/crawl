package com.unowen.crawl;

import com.unowen.util.RedisService;
import com.unowen.util.WXPush;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName Crawl
 * @Author cy
 * @Date 2021/9/5 14:53
 * @Description 爬取网站信息 https://906k.cn
 * @Version 1.0
 **/
@Component
@Slf4j
public class XCrawl {

    @Autowired
    private RedisService redisService;

    private String pref = "https://906k.cn";

    public void getLi(String url) {
        Document document;
        try {
            document = Jsoup.connect(url).get();
        } catch (Exception e) {
            log.error(e.getMessage());
            return;
        }

        Elements liEles = document.select("div.content_news_list ul").select("li");

        for (Element liEle : liEles) {
            Element aEle = liEle.child(0);
            List<Node> liChildNodes = aEle.childNodes();
            if (liChildNodes != null && liChildNodes.size() == 3) {
                String title = liChildNodes.get(0).childNode(0).outerHtml().replaceAll("\n", "");
                String timeAgo = liChildNodes.get(2).childNode(0).outerHtml();
                if (!redisService.exists(title)) {
                    String aHref = aEle.attr("href");


                    Document detailDoc;
                    try {
                        detailDoc = Jsoup.connect(pref + aHref).get();
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        return;
                    }
                    String detail = detailDoc.select("div.text").html();
                    redisService.set(title, detail, 30L, TimeUnit.DAYS);
                    HashMap<String, Object> paramMap = new HashMap<>();
                    paramMap.put("标题", title);
                    paramMap.put("时间", timeAgo);
                    paramMap.put("内容", detail);
                    WXPush.sendText(paramMap, "@all", null);
                } else {
                    break;
                }
            }
        }
        return;
    }

}
