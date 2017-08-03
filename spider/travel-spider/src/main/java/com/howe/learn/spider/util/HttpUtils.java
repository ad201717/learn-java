package com.howe.learn.spider.util;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class HttpUtils {

//    private static final ClientConnectionManager connectionManager = new PoolingClientConnectionManager();
//    private static final HttpClient client = new DefaultHttpClient(connectionManager);
    private static final DynamicUserAgent dynamicUserAgent = new DynamicUserAgent();


    /**
     * 连接池里的最大连接数
     */
    public static final int MAX_TOTAL_CONNECTIONS = 30;

    /**
     * 每个路由的默认最大连接数
     */
    public static final int MAX_ROUTE_CONNECTIONS = 10;

    /**
     * 连接超时时间
     */
    public static final int CONNECT_TIMEOUT = 30000;

    /**
     * 套接字超时时间
     */
    public static final int SOCKET_TIMEOUT = 30000;

    /**
     * 连接池中 连接请求执行被阻塞的超时时间
     */
    public static final long CONN_MANAGER_TIMEOUT = 60000;

    /**
     * http连接相关参数
     */
    private static HttpParams parentParams;

    /**
     * http线程池管理器
     */
    private static PoolingClientConnectionManager cm;

    /**
     * http客户端
     */
    private static DefaultHttpClient client;

    /**
     * 初始化http连接池，设置参数、http头等等信息
     */
    static {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(
                new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        schemeRegistry.register(
                new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));

        cm = new PoolingClientConnectionManager(schemeRegistry);

        cm.setMaxTotal(MAX_TOTAL_CONNECTIONS);

        cm.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);

        parentParams = new BasicHttpParams();
        parentParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

//        parentParams.setParameter(ClientPNames.DEFAULT_HOST, DEFAULT_TARGETHOST);    //设置默认targetHost

        parentParams.setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);

        parentParams.setParameter(ClientPNames.CONN_MANAGER_TIMEOUT, CONN_MANAGER_TIMEOUT);
        parentParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECT_TIMEOUT);
        parentParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, SOCKET_TIMEOUT);

        parentParams.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        parentParams.setParameter(ClientPNames.HANDLE_REDIRECTS, true);

        client = new DefaultHttpClient(cm, parentParams);

    }


    public static InputStream get(String spec) throws Exception {
        HttpUriRequest request = new HttpGet(spec);
        request.setHeader(DynamicUserAgent.KEY_USER_AGENT, dynamicUserAgent.generate());
        HttpResponse response = client.execute(request);
        if (response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK) {
            throw new IllegalStateException("error :" + response.getStatusLine().toString());
        }
        return response.getEntity().getContent();
    }

    public static String post(String spec, Map<String, Object> params) throws Exception {
        if (params.isEmpty()) {
            return post(spec, "");
        }

        StringBuilder sb = new StringBuilder();
        Iterator<Entry<String, Object>> i = params.entrySet().iterator();
        for (; ; ) {
            sb.append(i.next());
            if (!i.hasNext())
                break;
            sb.append("&");
        }

        return post(spec, sb.toString());
    }

    public static String post(String spec, String params) throws Exception {
        URL url = new URL(spec);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");

        PrintWriter out = null;
        try {
            out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream()));
            out.println(params);
            out.flush();
        } finally {
            if (out != null)
                out.close();
        }

        StringBuilder sb = new StringBuilder();
        String line = null;

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            while (null != (line = in.readLine())) {
                sb.append(line);
            }
        } finally {
            if (in != null)
                in.close();
        }

        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
//		String spec = "http://www.mafengwo.cn/mdd/base/routeline/pagedata_routelist";
//		String params = "mddid=12711&page=2&type=2";
//		String response = post(spec, params);
//		System.out.println(response);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("mddid", 12711);
        params.put("page", 2);
        params.put("type", 2);

        String spec = "http://www.mafengwo.cn/mdd/base/routeline/pagedata_routelist";
        String resp = post(spec, params);
        System.out.println(resp);
    }

    static class DynamicUserAgent {
        String[] seeds = new String[]{
                "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36",
                //猎豹浏览器2.0.10.3198 急速模式on Windows 7 x64：
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.71 Safari/537.1 LBBROWSER",
                //猎豹浏览器2.0.10.3198 兼容模式on Windows 7 x64：
                "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; LBBROWSER)",
                //猎豹浏览器2.0.10.3198兼容模式on Windows XP x86 IE6
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 732; .NET4.0C; .NET4.0E; LBBROWSER)",
                //猎豹浏览器1.5.9.2888 急速模式on Windows 7 x64：
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.84 Safari/535.11 LBBROWSER",
                //猎豹浏览器1.5.9.2888 兼容模式 on Windows 7 x64：
                "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E)",
                //QQ浏览器7.0 on Windows 7 x64 IE9：
                "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; QQBrowser/7.0.3698.400)",
                //QQ浏览器7.0 on Windows XP x86 IE6：
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 732; .NET4.0C; .NET4.0E)",
                //360安全浏览器5.0自带IE8内核版 on Windows XP x86 IE6：
                "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; SV1; QQDownload 732; .NET4.0C; .NET4.0E; 360SE)",
                //360安全浏览器5.0 on Windows XP x86 IE6：
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 732; .NET4.0C; .NET4.0E)",
                //360安全浏览器5.0 on Windows 7 x64 IE9：
                "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E)",
                //360急速浏览器6.0 急速模式 on Windows XP x86：
                "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1",
                //360急速浏览器6.0 急速模式 on Windows 7 x64：
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1",
                //360急速浏览器6.0 兼容模式 on Windows XP x86 IE6：
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 732; .NET4.0C; .NET4.0E)",
                //360急速浏览器6.0 兼容模式 on Windows 7 x64 IE9：
                "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E)",
                //360急速浏览器6.0 IE9/IE10模式 on Windows 7 x64 IE9：
                "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E)",
                //搜狗浏览器4.0 高速模式 on Windows XP x86：
                "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.84 Safari/535.11 SE 2.X MetaSr 1.0",
                //搜狗浏览器4.0 兼容模式 on Windows XP x86 IE6：
                "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; SV1; QQDownload 732; .NET4.0C; .NET4.0E; SE 2.X MetaSr 1.0)",
                "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:16.0) Gecko/20121026 Firefox/16.0",
                //Firefox x64 4.0b13pre on Windows 7 x64：
                "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:2.0b13pre) Gecko/20110307 Firefox/4.0b13pre",
                //Firefox x64 on Ubuntu 12.04.1 x64：
                "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:16.0) Gecko/20100101 Firefox/16.0",
                //Firefox x86 3.6.15 on Windows 7 x64：
                "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.15) Gecko/20110303 Firefox/3.6.15",
                //Chrome x64 on Ubuntu 12.04.1 x64：
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11",
                //Chrome x86 23.0.1271.64 on Windows 7 x64：
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11",
                //Chrome x86 10.0.648.133 on Windows 7 x64：
                "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/534.16 (KHTML, like Gecko) Chrome/10.0.648.133 Safari/534.16",
                //IE9 x64 9.0.8112.16421 on Windows 7 x64：
                "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0)",
                //IE9 x86 9.0.8112.16421 on Windows 7 x64：
                "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)",
                //Firefox x64 3.6.10 on Ubuntu 10.10 x64：
                "Mozilla/5.0 (X11; U; Linux x86_64; zh-CN; rv:1.9.2.10) Gecko/20100922 Ubuntu/10.10 (maverick) Firefox/3.6.10",
        };
        Random rand = new Random();

        public static final String KEY_USER_AGENT = "User-agent";

        public String generate(){
            return seeds[rand.nextInt(seeds.length)];
        }
    }
}
