package com.howe.learn.spider.util;

import java.io.File;
import java.io.IOException;

/**
 * @Author Karl
 * @Date 2017/1/10 15:00
 */
public class FileUtil {

    public static void ensurePath(String file){
        ensurePath(new File(file));
    }

    public static void ensureFile(String file){
        ensureFile(new File(file));
    }

    public static void ensurePath(File file){
        if(file.exists()){
            return;
        }
        file.mkdirs();
    }

    public static void ensureFile(File file){
        if(file.exists()){
            return;
        }
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
