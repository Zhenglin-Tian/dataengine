package com.tcredit.engine.util.httpClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcredit.engine.exception.CustomedConnectionException;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.*;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

/**
 *
 * 2016年1月27日 上午9:31:48</br>
 *
 * @description
 */
public class HttpClientUtil {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(HttpClientUtil.class);

    private static ObjectMapper om = new ObjectMapper();
    private static PoolingHttpClientConnectionManager connManager = null;
    private static CloseableHttpClient httpclient = null;
    private static int timeout = 55000;

    static {
        try {
            RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder
                    .<ConnectionSocketFactory>create();
            ConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();
            registryBuilder.register("http", plainSF);
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            TrustStrategy anyTrustStrategy = new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            };
            SSLContext sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(trustStore, anyTrustStrategy)
                    .build();

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslContext)).build();

            connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            // Create socket configuration
            SocketConfig socketConfig = SocketConfig.custom().setRcvBufSize(2048).setSndBufSize(2048)
                    .setSoKeepAlive(true).setTcpNoDelay(true).setSoTimeout(2 * 60 * 1000).build();
            connManager.setDefaultSocketConfig(socketConfig);
            // Create message constraints
            MessageConstraints messageConstraints = MessageConstraints.custom().setMaxHeaderCount(200)
                    .setMaxLineLength(2000).build();
            // Create connection configuration
            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                    .setMalformedInputAction(CodingErrorAction.IGNORE)
                    .setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Consts.UTF_8)
                    .setMessageConstraints(messageConstraints).build();
            connManager.setDefaultConnectionConfig(connectionConfig);
            connManager.setMaxTotal(60000);
            connManager.setDefaultMaxPerRoute(500);
            httpclient = HttpClients.custom().setConnectionManager(connManager).build();

        } catch (KeyManagementException e) {
            LOGGER.error("KeyManagementException", e);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("NoSuchAlgorithmException", e);
        } catch (KeyStoreException e) {
            LOGGER.error("", e);
        }
    }


    public static String invokeGet(String url, Map<String, String> params, String encode, int connectTimeout) {
        String responseString = null;
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(connectTimeout)
                .setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout).build();

        StringBuilder sb = new StringBuilder();
        sb.append(url);
        int i = 0;
        for (Entry<String, String> entry : params.entrySet()) {
            if (i == 0 && !url.contains("?")) {
                sb.append("?");
            } else {
                sb.append("&");
            }
            sb.append(entry.getKey());
            sb.append("=");
            String value = entry.getValue();
            try {
                sb.append(URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                LOGGER.info("encode http get params error, value is " + value, e);
                sb.append(URLEncoder.encode(value));
            }
            i++;
        }
        LOGGER.info("[HttpUtils Get] begin invoke:" + sb.toString());
        HttpGet get = new HttpGet(sb.toString());
        get.setConfig(requestConfig);

        try {
            CloseableHttpResponse response = httpclient.execute(get);
            try {
                HttpEntity entity = response.getEntity();
                try {
                    if (entity != null) {
                        responseString = EntityUtils.toString(entity, encode);
                    }
                } finally {
                    if (entity != null) {
                        entity.getContent().close();
                    }
                }
            } catch (Exception e) {
                LOGGER.error(String.format("[HttpUtils Get]get response error, url:%s", sb.toString()), e);
                return responseString;
            } finally {
                if (response != null) {
                    response.close();
                }
            }
            LOGGER.info(String.format("[HttpUtils Get]Debug url:%s , response string %s:", sb.toString(), responseString));
        } catch (SocketTimeoutException e) {
            LOGGER.error(String.format("[HttpUtils Get]invoke get timout error, url:%s", sb.toString()), e);
            return responseString;
        } catch (Exception e) {
            LOGGER.error(String.format("[HttpUtils Get]invoke get error, url:%s", sb.toString()), e);
        } finally {
            get.releaseConnection();
        }
        return responseString;
    }


    public static String httpGet(String url, Map<String, String> params, int timeout) {
        String content = null;

        StringBuilder sb = new StringBuilder();
        sb.append(url);

        if (params != null && !params.isEmpty()) {
            int i = 0;
            for (Entry<String, String> entry : params.entrySet()) {
                if (i == 0 && !url.contains("?")) {
                    sb.append("?");
                } else {
                    sb.append("&");
                }
                sb.append(entry.getKey());
                sb.append("=");
                String value = entry.getValue();
                try {
                    sb.append(URLEncoder.encode(value, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    return null;
                }
                i++;
            }
        }
        CloseableHttpClient closeableHttpClient = null;
        try {
            BasicHttpClientConnectionManager clientConnectionManager = new BasicHttpClientConnectionManager();
            SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(timeout).build();
            clientConnectionManager.setSocketConfig(socketConfig);

            closeableHttpClient = HttpClientBuilder.create().setConnectionManager(clientConnectionManager).build();
            RequestConfig config = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout).setExpectContinueEnabled(false).build();
            HttpGet httpPost = new HttpGet(sb.toString());
            httpPost.setConfig(config);
            HttpResponse response = closeableHttpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            content = EntityUtils.toString(entity);

        } catch (Exception e) {
            LOGGER.error("发送httpPost异常，异常信息：",e);
            throw new CustomedConnectionException(e.getMessage());
        } finally {
            try {
                closeableHttpClient.close();    //关闭连接
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }

    /**
     * 使用代理的方式发送post请求
     *
     * @param url
     * @param params
     * @param timeout 毫秒
     * @return
     */
    public static String httpPost(String url, Map<String, String> params, int timeout) {
        CloseableHttpClient closeableHttpClient = null;
        String content = null;
        try {

            BasicHttpClientConnectionManager clientConnectionManager = new BasicHttpClientConnectionManager();
            SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(timeout).build();

            /**
             * SoLinger 设置当有数据没有接收完成时 默认阻塞60s
             */
//            SocketConfig socketConfig = SocketConfig.custom().setRcvBufSize(2048).setSndBufSize(2048)
//                    .setSoKeepAlive(true).setTcpNoDelay(true).setSoLinger(60).setSoTimeout(timeout).build();

            clientConnectionManager.setSocketConfig(socketConfig);
            closeableHttpClient = HttpClientBuilder.create().setConnectionManager(clientConnectionManager).build();
            RequestConfig config = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout).setExpectContinueEnabled(false).build();

            //创建httppost对象
            HttpPost post = new HttpPost(url);
            //设置配置
            post.setConfig(config);
            //设置参数
            ArrayList<NameValuePair> postParam = new ArrayList<>();

            for (Entry<String, String> entry : params.entrySet()) {
                postParam.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));

            }

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParam, HTTP.UTF_8);
            post.setEntity(entity);
            //执行post请求
            HttpResponse response = closeableHttpClient.execute(post);
            HttpEntity responseEntity = response.getEntity();
            content = EntityUtils.toString(responseEntity);

        } catch (Exception e) {
            LOGGER.error("发送httpPost异常，异常信息：",e);
            if (e instanceof SocketTimeoutException) {
                throw new CustomedConnectionException(e.getMessage());
            }
        } finally {
            try {
                closeableHttpClient.close();//关闭连接
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }


}
