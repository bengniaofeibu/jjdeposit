package com.jiujiu.utils;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by t_hz on 2019/5/15.
 */
public class HttpClientUtil {

    private static final String APPLICATION_JSON = "application/json";

    private static final String CONTENT_TYPE_TEXT_JSON = "text/json";



    private static final CloseableHttpClient httpclient = HttpClients.createDefault();

    public static RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(5000)
            .setConnectTimeout(5000)
            .setConnectionRequestTimeout(5000)
            .setStaleConnectionCheckEnabled(true)
            .build();




    public static String sendPost(String url, Map<String, Object> map) {
        List<NameValuePair> formparams = new ArrayList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            formparams.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        HttpPost httppost = new HttpPost(url);
        httppost.setEntity(entity);
        httppost.setHeader(HTTP.CONTENT_TYPE,APPLICATION_JSON);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity1 = response.getEntity();
        String result = null;
        try {
            result = EntityUtils.toString(entity1);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String httpPostWithJSON(String url, String json){
        String result = "-1";
        try{
            // 将JSON进行UTF-8编码,以便传输中文
            //String encoderJson = URLEncoder.encode(json, "UTF-8");

            HttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
            httpPost.setHeader("Accept", "application/json");
            StringEntity se = new StringEntity(json, Charset.forName("UTF-8"));
            se.setContentType(CONTENT_TYPE_TEXT_JSON);
            //    se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
            se.setContentEncoding("UTF-8");
            httpPost.setEntity(se);

            HttpResponse response = httpClient.execute(httpPost);
            if(200 == response.getStatusLine().getStatusCode()){
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, Consts.UTF_8);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(StringUtils.isEmpty(result)){
            result = "-1";
        }
        return result;
    }

}
