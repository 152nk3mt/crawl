package com.unowen.schedul;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.file.FileReader;
import com.unowen.util.ConfigUtil;
import com.unowen.util.QlUtils;
import com.unowen.util.WXPush;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName QLEnvCheck
 * @Author cy
 * @Date 2022/5/21
 * @Description
 * @Version 1.0
 **/
@Component
public class QLEnvCheck {

    @Value("${ql.logFilePath}")
    private String logFilePath;

    @Scheduled(cron = "0 15 0,3,8,11,17 * * ?")
    public void execute() throws Exception {
        // 读取log文件
        File file = new File(logFilePath);
        List<String> fileNameList = Arrays.asList(file.list()).stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(fileNameList)) {
            String name = fileNameList.get(0);
            String logFileName = logFilePath + File.separator + name;
            FileReader fileReader = new FileReader(logFileName);
            List<String> fileLines = fileReader.readLines();
            for (int i = 0; i < fileLines.size(); i++) {
                String item = fileLines.get(i);
                if (item.endsWith("个账号cookie过期")) {
                    System.out.println(item);
                    int index = Convert.toInt(item.replace("第", "").replace("个账号cookie过期", "")) - 1;
                    System.out.println("过期ck的index:" + index);
                    String token = QlUtils.getToken();
                    List<Map> envList = QlUtils.getEnv(token);
                    Map envMap = envList.get(index);
                    String remarks = String.valueOf(envMap.get("remarks"));
                    String envVlue = String.valueOf(envMap.get("value"));
                    //通过remarks查找微信企业id
                    Object wxId = ConfigUtil.get(remarks);
                    HashMap<String, Object> paramMap = new HashMap<>();
                    if (wxId != null) {
                        paramMap.put(remarks, envVlue);
                    } else {
                        paramMap.put("第" + index + "个账号过期：", remarks);
                        wxId = "WangJinJin";
                    }
                    WXPush.sendText(paramMap, Convert.toStr(wxId), null);
                }
            }

        }

    }

}
