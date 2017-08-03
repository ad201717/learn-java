package com.howe.learn.spider.util;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.jsoup.helper.DataUtil;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Author Karl
 * @Date 2017/1/5 10:46
 */
public class URLUtils {

    public static String truncateParam(String url) {
        if(null != url && url.length() > 0 && url.indexOf("?") > -1) {
            return url.split("\\?")[0];
        }
        return url;
    }

    public static String getParam(String url, String key) {
        if(StringUtils.isEmpty(url) || StringUtils.isEmpty(key) || url.indexOf("?") == -1){
            return null;
        }
        String[] pairs = url.split("\\?", 2)[1].split("&");
        for(String pair : pairs){
            if(pair.indexOf("=")>0 && key.equals(pair.substring(0, pair.indexOf("=")))) {
                return pair.substring(pair.indexOf("=") + 1);
            }
        }
        return null;
    }

    public static List<String> downloadImgs(List<String> imgs) {
        List<String> list = new ArrayList<String>();
        for (String img : imgs) {
            try {
                list.add(downloadImg(img));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return list;
    }

    public static String downloadImg(String img) throws Exception {
        if(img.startsWith("//")){
            img = "http:" + img;
        }
        URL url = new URL(img);
        URLConnection conn = url.openConnection();
        InputStream in = conn.getInputStream();
        String tarFilename = DateFormatUtils.format(new Date(), "yyyyMMdd") + "/" +UUID.randomUUID().toString().replaceAll("-","") + "." + getExtension(new File(URLUtils.truncateParam(img)).getName());
        String path = "/usr/local/nginx-1.9.9/uploadfile/photo/scenicspot/";
        String tarFile = path + tarFilename;
        FileUtil.ensurePath(new File(tarFile).getParent());
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(tarFile));
            byte[] b = new byte[1024];
            int len;
            while ((len = in.read(b)) != -1) {
                out.write(b, 0, len);
                out.flush();
            }
        } finally {
            if(null != out)
                out.close();
        }
        return tarFilename;
    }

    public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int index = filename.indexOf('.');
        if (index == -1) {
            return "jpg";
        } else {
            return filename.substring(index + 1);
        }

    }

    public static void main(String[] args) throws Exception {
        downloadImg("//gss0.baidu.com/6b1IcTe9R1gBo1vgoIiO_jowehsv/maps/services/thumbnails?width=525&height=295&quality=100&align=middle,middle&src=http://gss0.baidu.com/7LsWdDW5_xN3otqbppnN2DJv/lvpics/pic/item/060828381f30e924a71955904c086e061c95f7d6.jpg");
        downloadImg("http://a4-q.mafengwo.net/s7/M00/AA/78/wKgB6lTPDgGAMKIBAARPH_6kXH809.jpeg?imageMogr2%2Fthumbnail%2F%211020x540r%2Fgravity%2FCenter%2Fcrop%2F%211020x540%2Fquality%2F100");
    }
}
