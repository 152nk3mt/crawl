package com.unowen.util;

import cn.hutool.core.io.watch.SimpleWatcher;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.watchers.DelayWatcher;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.setting.dialect.Props;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName ConfigUtil
 * @Author cy
 * @Date 2022/5/21
 * @Description
 * @Version 1.0
 **/
public class ConfigUtil {
    private static Props props;

    static {
        File file = new File(".", "config.properties");
        props = new Props(file, CharsetUtil.UTF_8);
        WatchMonitor watchMonitor = WatchMonitor.create(file, WatchMonitor.ENTRY_MODIFY);
        watchMonitor.setWatcher(new DelayWatcher(new SimpleWatcher(){
            @Override
            public void onModify(WatchEvent<?> event, Path currentPath) {
                Console.log("EVENT modify");
                props = new Props(file, CharsetUtil.UTF_8);
            }
        }, 5000)).start();
    }

    public static Object get(String key) {
        return props.get(key);
    }

    public static void main(String[] args) throws InterruptedException {
        while (true) {
            TimeUnit.SECONDS.sleep(3);
            System.out.println(ConfigUtil.get("CY2"));
        }
    }
}
