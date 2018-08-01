package com.szzt.smart.framework.web.util;




import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.szzt.smart.framework.web.json.JsonObjectMapper;
import com.szzt.smart.framework.web.model.ResultBody;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * 此类包含了通过HTTP协议的请求响应等方法。 此类属于公共方法，负责提供接口给业务调用。 目前请求支持4种请求方式： 1
 * xml请求报文通过POST方法请求URL，返回响应报文 2 xml请求报文通过PUT方法请求URL，返回响应报文 3
 * 无参数的请求通过GET方法请求URL，返回响应报文 4 无参数的请求通过DELETE方法请求URL，返回操作结果 使用方法：
 * 先得到HttpUtil的实例（使用连接池方式），
 * 通过HttpUtil.getInstance().getHttpClient()返回HttpClient对象 然后调用指定的方法即可

 */
public class HttpUtils
{
    /**
     * http的header中的content-type属性的名字
     */
    private static final String CONTENT_TYPE_NAME = "content-type";

    /**
     * 编码类型 UTF-8
     */
    private static final String ENCODE_UTF_8 = "UTF-8";

    /**
     * HTTP请求消息头中json内容类型的格式
     */
    private static final String CONTENT_TYPE_VALUE_JSON_UTF_8 = "application/json; charset=" + ENCODE_UTF_8;

    private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private ObjectMapper jsonObj = new JsonObjectMapper();

    /**
     * HttpUtil类的实例
     */
    private static HttpUtils instance;

    /**
     * HttpUtil类构造函数
     */
    private HttpUtils()
    {
    }

    public static HttpUtils getInstance()
    {

//        MultiThreadedHttpConnectionManager manager = new MultiThreadedHttpConnectionManager();
//        HttpConnectionManager
//        HttpClientConnectionManager
//        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        if (null == instance)
        {
            instance = new HttpUtils();
        }
        return instance;
    }

    /**
     * 发送HTTP请求
     *
     * @param url URL地址
     * @param paramObj 消息体
     * @return
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public <T> T sendHttpByPostMethod(String url, Object paramObj, Class<T> valueType)
            throws Exception
    {
        return sendHttpByPostMethod(url, jsonObj.writeValueAsString(paramObj), valueType);
    }


    /**
     * 发送HTTP请求
     *
     * @param url URL地址
     * @return
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public <T> T sendHttpByPostMethod(String url, String paramJson, Class<T> valueType)
            throws Exception
    {
        return sendHttpByPostMethod(url, paramJson, valueType, getDefaultHeader());
    }

    /**
     * 发送HTTP请求
     *
     * @param url URL地址
     * @param paramObj 消息体
     * @return
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public <T> T sendHttpByPutMethod(String url, Object paramObj, Class<T> valueType)
            throws Exception
    {
        return sendHttpByPutMethod(url, jsonObj.writeValueAsString(paramObj), valueType);
    }


    /**
     * 发送HTTP请求
     *
     * @param url URL地址
     * @return
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public <T> T sendHttpByPutMethod(String url, String jsonStr, Class<T> valueType)
            throws Exception
    {
        return sendHttpByPutMethod(url, jsonStr, valueType, getDefaultHeader());
    }

    public <T> T sendHttpByPutMethod(String url, String jsonString, Class<T> valueType, Map<String, String> headerMap)
            throws Exception
    {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.APPLICATION_JSON_UTF8;
        headers.setContentType(type);

        for (String s : headerMap.keySet()) {
            headers.add(s, headerMap.get(s));
        }
        String resultStr = restTemplate.exchange(url, HttpMethod.PUT, new org.springframework.http.HttpEntity(jsonString,headers), String.class).getBody();

        if (valueType == String.class) {
            return (T)resultStr;
        }
        return jsonObj.readValue(resultStr, valueType);
    }
    /**
     * 发送HTTP请求
     *
     * @param url URL地址
     * @param jsonString 消息体
     * @return
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    @SuppressWarnings("unchecked")
    public <T> T sendHttpByPutMethod_(String url, String jsonString, Class<T> valueType, Map<String, String> headerMap)
            throws Exception
    {
        HttpPut httpPut = new HttpPut(url);
        logger.debug("send http start " + url);
        try
        {
            if (MapUtils.isNotEmpty(headerMap))
            {
                // 设置参数数据
                for (Map.Entry<String, String> entry : headerMap.entrySet())
                {
                    httpPut.addHeader(entry.getKey(), entry.getValue());
                }
            }
            headerMap.put(CONTENT_TYPE_NAME, CONTENT_TYPE_VALUE_JSON_UTF_8);
            HttpEntity entity = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
            httpPut.setEntity(entity);

            // 响应失败，则抛出异常码进行处理
//            HttpResponse httpResoponse = httpClient.execute(httpPut);
            HttpResponse httpResoponse = execute(httpPut);
            String resultStr = EntityUtils.toString(httpResoponse.getEntity(), "UTF-8");
            if (HttpStatus.SC_OK != httpResoponse.getStatusLine().getStatusCode()
                    && HttpStatus.SC_CREATED != httpResoponse.getStatusLine().getStatusCode())
            {
                logger.error("send http request error：" + "url：" + url + " param：" + jsonString);
                ResultBody error = jsonObj.readValue(resultStr, ResultBody.class);
//                throw new BusinessException(error.getError());
//                throw new BusinessException("The response code is error! url: " + url + "  cause：" + resultStr);
            }
            EntityUtils.consume(httpResoponse.getEntity());
            if (valueType.equals(String.class))
            {
                return (T)resultStr;
            }
            return jsonObj.readValue(resultStr, valueType);
        }
        catch (Exception ex)
        {
            logger.error("send http request error!", ex);
            throw ex;
        }
        finally
        {
            httpPut.abort();
            logger.debug("send http end " + url);
        }
    }

    RestTemplate restTemplate = new RestTemplate();

    /**
     * 发送HTTP请求
     * @param url   URL地址
     * @param jsonString    消息体
     * @param valueType 返回对象类型
     * @param headerMap 请求头
     * @param <T>
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public <T> T sendHttpByPostMethod(String url, String jsonString, Class<T> valueType, Map<String, String> headerMap)
            throws Exception
    {

        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.APPLICATION_JSON_UTF8;
        headers.setContentType(type);

        for (String s : headerMap.keySet()) {
            headers.add(s, headerMap.get(s));
        }

        logger.debug("send http start " + url);
        String resultStr = restTemplate.postForObject(url, new org.springframework.http.HttpEntity(jsonString,headers), String.class);
        logger.debug("send http end " + url);

        if (valueType == String.class) {
            return (T)resultStr;
        }
        return jsonObj.readValue(resultStr, valueType);
    }

    @SuppressWarnings("unchecked")
    public <T> T sendHttpByPostMethod_(String url, String jsonString, Class<T> valueType, Map<String, String> headerMap)
            throws Exception
    {
        HttpPost httpPost = new HttpPost(url);
        logger.debug("send http start " + url);
        try
        {
            headerMap.put(CONTENT_TYPE_NAME, CONTENT_TYPE_VALUE_JSON_UTF_8);
            if (MapUtils.isNotEmpty(headerMap))
            {
                // 设置参数数据
                for (Map.Entry<String, String> entry : headerMap.entrySet())
                {
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                }
            }
            if (StringUtils.isNotBlank(jsonString))
            {
                HttpEntity entity = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
                httpPost.setEntity(entity);
            }

            // 响应失败，则抛出异常码进行处理
//            HttpResponse httpResoponse = httpClient.execute(httpPost);
            HttpResponse httpResoponse = execute(httpPost);
            String resultStr = EntityUtils.toString(httpResoponse.getEntity(), "UTF-8");
            if (HttpStatus.SC_OK != httpResoponse.getStatusLine().getStatusCode()
                    && HttpStatus.SC_CREATED != httpResoponse.getStatusLine().getStatusCode())
            {
                logger.error("send http request error：" + "url：" + url + " param：" + jsonString);
                ResultBody error = jsonObj.readValue(resultStr, ResultBody.class);
//                throw new BusinessException(error.getError());
            }
            EntityUtils.consume(httpResoponse.getEntity());
            if (valueType.equals(String.class))
            {
                return (T)resultStr;
            }
            return jsonObj.readValue(resultStr, valueType);
        }
        catch (Exception ex)
        {
            logger.error("send http request error!", ex);
            throw ex;
        }
        finally
        {
            httpPost.abort();
            logger.debug("send http end " + url);
        }
    }

    /**
     * 通过GET方式发起http请求
     *
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    public <T> T sendHttpByGetMethod(String url, Class<T> valueType)
            throws Exception
    {
        return sendHttpByGetMethod(url, valueType, getDefaultHeader());
    }

    public <T> T sendHttpByGetMethod(String url, Class<T> valueType, Map<String, String> headerMap)
            throws Exception
    {
        HttpHeaders headers = new HttpHeaders();
//        MediaType type = MediaType.APPLICATION_JSON_UTF8;
//        headers.setContentType(type);

        for (String s : headerMap.keySet()) {
            headers.add(s, headerMap.get(s));
        }
        String resultStr = restTemplate.exchange(url, HttpMethod.GET, new org.springframework.http.HttpEntity(headers), String.class).getBody();

        if (valueType == String.class) {
            return (T)resultStr;
        }
        return jsonObj.readValue(resultStr, valueType);
    }
    /**
     * 通过GET方式发起http请求
     *
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @SuppressWarnings("unchecked")
    public <T> T sendHttpByGetMethod_(String url, Class<T> valueType, Map<String, String> headerMap)
            throws Exception
    {
        HttpGet httpGet = null;
        logger.debug("send http start " + url);
        try
        {
            // 用get方法发送http请求
            httpGet = new HttpGet(url);

            if (MapUtils.isNotEmpty(headerMap))
            {
                // 设置参数数据
                for (Map.Entry<String, String> entry : headerMap.entrySet())
                {
                    httpGet.addHeader(entry.getKey(), entry.getValue());
                }
            }
            // 发送get请求
//            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpResponse httpResponse = execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            String resultStr = EntityUtils.toString(entity, "UTF-8");
            // response实体
            if (HttpStatus.SC_OK != httpResponse.getStatusLine().getStatusCode()
                    && HttpStatus.SC_CREATED != httpResponse.getStatusLine().getStatusCode())
            {
                logger.error("send http request error：" + "url：" + url);
                ResultBody error = jsonObj.readValue(resultStr, ResultBody.class);
//                throw new BusinessException(error.getError());
//                throw new BusinessException("The response code is error! url: " + url + "  cause：" + renStr);
            }
            EntityUtils.consume(entity);
            httpGet.abort();
            if (valueType.equals(String.class))
            {
                return (T)resultStr;
            }
            return jsonObj.readValue(resultStr, valueType);
        }
        catch (Exception e)
        {
            logger.error("执行请求失败", e);
            throw e;
        }
        finally
        {
            httpGet.abort();
            logger.debug("send http end " + url);
        }
    }

    /**
     * 发送HTTP请求（并返回结果集类型为List）
     *
     * @param url URL地址
     * @return
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public <T> List<T> sendHttpByGetMethodReturnList(String url, Class<T> valueClass) throws Exception
    {
        return sendHttpByGetMethodReturnList(url, valueClass, getDefaultHeader());
    }
    public <T> List<T> sendHttpByGetMethodReturnList(String url, Class<T> valueClass, Map<String, String> headerMap)
            throws Exception
    {
        HttpHeaders headers = new HttpHeaders();

        for (String s : headerMap.keySet()) {
            headers.add(s, headerMap.get(s));
        }
        String result = restTemplate.exchange(url, HttpMethod.GET, new org.springframework.http.HttpEntity(headers), String.class).getBody();

        JavaType javaType = jsonObj.getTypeFactory().constructParametricType(List.class, valueClass);
        return jsonObj.readValue(result, javaType);
    }
    /**
     * 发送HTTP请求（并返回结果集类型为List）
     *
     * @param url URL地址
     * @return
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public <T> List<T> sendHttpByGetMethodReturnList_(String url, Class<T> valueClass, Map<String, String> headerMap)
            throws Exception
    {
        HttpGet httpGet = new HttpGet(url);
        logger.debug("send http start " + url);
        try
        {
            // 设置参数数据
            if (MapUtils.isNotEmpty(headerMap))
            {
                for (Map.Entry<String, String> entry : headerMap.entrySet())
                {
                    httpGet.addHeader(entry.getKey(), entry.getValue());
                }
            }

//            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpResponse httpResponse = execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            String resultStr = EntityUtils.toString(entity, "UTF-8");
            // 响应失败，则抛出异常码进行处理
            if (HttpStatus.SC_OK != httpResponse.getStatusLine().getStatusCode()
                    && HttpStatus.SC_CREATED != httpResponse.getStatusLine().getStatusCode())
            {
                logger.error("send http request error：" + "url：" + url);
                ResultBody error = jsonObj.readValue(resultStr, ResultBody.class);
//                throw new BusinessException(error.getError());
//                throw new BusinessException("The response code is error! url: " + url);
            }

            EntityUtils.consume(httpResponse.getEntity());
            JavaType javaType = jsonObj.getTypeFactory().constructParametricType(List.class, valueClass);
            return jsonObj.readValue(resultStr, javaType);
        }
        catch (Exception ex)
        {
            logger.error("send http request error!", ex);
            throw ex;
        }
        finally
        {
            httpGet.abort();
            logger.debug("send http end " + url);
        }
    }

    /**
     * 发送HTTP请求（并返回结果集类型为List）
     *
     * @param url URL地址
     * @return
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public <T> List<T> sendHttpByPostMethodReturnList(String url, Object obj, Class<T> valueClass,
                                                      Map<String, String> headerMap)
            throws Exception
    {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.APPLICATION_JSON_UTF8;
        headers.setContentType(type);
        for (String s : headerMap.keySet()) {
            headers.add(s, headerMap.get(s));
        }

        logger.debug("send http start " + url);
        String result = restTemplate.exchange(url, HttpMethod.POST, new org.springframework.http.HttpEntity(obj,headers), String.class).getBody();
        logger.debug("send http end " + url);

        JavaType javaType = jsonObj.getTypeFactory().constructParametricType(List.class, valueClass);
        return jsonObj.readValue(result, javaType);
    }
    public <T> List<T> sendHttpByPostMethodReturnList_(String url, Object obj, Class<T> valueClass,
                                                       Map<String, String> headerMap)
            throws Exception
    {
        HttpPost httpPost = new HttpPost(url);
        logger.debug("send http start " + url);
        try
        {
            // 设置参数数据
            if (MapUtils.isNotEmpty(headerMap))
            {
                for (Map.Entry<String, String> entry : headerMap.entrySet())
                {
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                }
            }
            headerMap.put(CONTENT_TYPE_NAME, CONTENT_TYPE_VALUE_JSON_UTF_8);
            String requsetJsonStr = jsonObj.writeValueAsString(obj);
            HttpEntity entity = new StringEntity(requsetJsonStr, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);

            // 响应失败，则抛出异常码进行处理
//            HttpResponse httpResoponse = httpClient.execute(httpPost);
            HttpResponse httpResoponse = execute(httpPost);
            String resultStr = EntityUtils.toString(httpResoponse.getEntity(), "UTF-8");
            if (HttpStatus.SC_OK != httpResoponse.getStatusLine().getStatusCode()
                    && HttpStatus.SC_CREATED != httpResoponse.getStatusLine().getStatusCode())
            {
                logger.error("send http request error：" + "url：" + url + " param：" + requsetJsonStr);
                ResultBody error = jsonObj.readValue(resultStr, ResultBody.class);
//                throw new BusinessException(error.getError());
            }

            EntityUtils.consume(httpResoponse.getEntity());
            JavaType javaType = jsonObj.getTypeFactory().constructParametricType(List.class, valueClass);
            return jsonObj.readValue(resultStr, javaType);
        }
        catch (Exception ex)
        {
            logger.error("send http request error!", ex);
            throw ex;
        }
        finally
        {
            httpPost.abort();
            logger.debug("send http end " + url);
        }
    }

    /**
     * 发送HTTP请求（并返回结果集类型为List）
     *
     * @param url URL地址
     * @return
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public <T> List<T> sendHttpByPostMethodReturnList(String url, Object obj, Class<T> valueClass)
            throws Exception
    {
        return sendHttpByPostMethodReturnList(url, obj, valueClass, getDefaultHeader());
    }

    private Map<String, String> getDefaultHeader()
    {
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("interal", "interal");
        return headerMap;
    }


    /**
     * 2.4 add by yidi
     * setConnectTimeout：连接超时时间，单位毫秒
     * setSocketTimeout：请求获取数据的超时时间，单位毫秒
     */
    private HttpResponse execute(HttpRequestBase httpRequest) throws ClientProtocolException, IOException
    {
        HttpClient httpClient = HttpClients.createDefault();
        return httpClient.execute(httpRequest);
    }
}
