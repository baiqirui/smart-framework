package com.szzt.smart.framework.metrics;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InfluxdbHttp implements Influxdb
{
    
    private static final String URL_STR = "http://%s:%d/write?db=%s&u=%s&p=%s";
    
    private static final int MAX_SIZE = 10;// 防止一次网络传输大量数据，减少数据过多造成网络拥堵
    
    private static final int BAD_REQUEST_CODE = 400;// influxdb返回400以上状态码都被认为是数据异常
    
    private long pointsCount;
    
    private final StringBuilder pointsBuffer = new StringBuilder();// 缓存即将发送的point数据
    
    private final String url;
    
    private String host;
    
    private int port;
    
    private String dbName;
    
    private String username;
    
    private String password;
    
    private Integer maxSize;
    
    public InfluxdbHttp(Builder builder)
    {
        this.host = builder.host;
        this.port = builder.port;
        this.dbName = builder.dbName;
        this.username = builder.username;
        this.password = builder.password;
        this.url = String.format(URL_STR, host, port, dbName, username, password);
        this.maxSize = builder.maxSize;
    }
    
    public static Builder newBuilder()
    {
        return new Builder();
    }
    
    public static class Builder
    {
        
        private String host;
        
        private int port;
        
        private String dbName;
        
        private String username;
        
        private String password;
        
        private int maxSize;
        
        private Builder()
        {
            this.port = 8086;
            this.dbName = "inFluxDB_Metrics";
            this.username = "";
            this.password = "";
            this.maxSize = MAX_SIZE;
        }
        
        /**
         * dbhost
         */
        public Builder host(String host)
        {
            this.host = host;
            return this;
        }
        
        /**
         * dbport
         */
        public Builder port(int port)
        {
            this.port = port;
            return this;
        }
        
        /**
         * 最大多少上传
         */
        public Builder maxSize(int maxSize)
        {
            this.maxSize = maxSize;
            return this;
        }
        
        /**
         * db名称
         */
        public Builder dbName(String dbName)
        {
            this.dbName = dbName;
            return this;
        }
        
        /**
         * influxdb 用户名，没有可以不填
         */
        public Builder username(String username)
        {
            this.username = username;
            return this;
        }
        
        /**
         * influxdb 密码，没有可以不填
         */
        public Builder password(String password)
        {
            this.password = password;
            return this;
        }
        
        public InfluxdbHttp build()
        {
            if (host == null)
            {
                throw new IllegalArgumentException("Influxdb host can not be null");
            }
            return new InfluxdbHttp(this);
        }
        
    }
    
    @Override
    public void writeData(SinglePoint singlePoint)
        throws InfluxdbException
    {
        if (singlePoint.getFieldMap().isEmpty())
        {
            return;
        }
        pointsBuffer.append(singlePoint.toString()).append("\n");
        if (++pointsCount > this.maxSize)
        {
            flush();
        }
    }
    
    @Override
    public void flush()
        throws InfluxdbException
    {
        try
        {
            String data = pointsBuffer.toString().trim();
            if (data.length() == 0)
            {
                return;
            }
            request(data);
            if (log.isDebugEnabled())
            {
                log.debug("Writing Data To Influxdb Succesfully With Data :\n {}", data);
            }
        }
        catch (Exception e)
        {
            if (e instanceof IOException)
            {
                throw new InfluxdbException(
                    "Can not send Request to remote Influxdb,please ensure influxdb has started. " + e.getMessage(), e);
            }
            throw new InfluxdbException(e.getMessage(), e);
        }
        finally
        {
            pointsCount = 0;
            pointsBuffer.setLength(0);// clear the buffer
        }
    }
    
    private void request(String dataBody)
        throws IOException
    {
        HttpURLConnection conn = null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int resultCode;
        try
        {
            URL a = new URL(url);
            conn = (HttpURLConnection)a.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            OutputStream wr = conn.getOutputStream();
            wr.write(dataBody.getBytes("utf-8"));
            wr.flush();
            wr.close();
            InputStream is = conn.getInputStream();
            int ret;
            while ((ret = is.read(buf)) > 0)
            {
                os.write(buf, 0, ret);
            }
            is.close(); // close the inputstream
            resultCode = conn.getResponseCode();
            if (!isSuccessCode(resultCode))
            {
                throw new IOException("influxdb returned unexpected http code:" + resultCode);
            }
        }
        catch (IOException e)
        {
            String msg = e.getMessage();
            if (conn != null)
            {
                InputStream es = ((HttpURLConnection)conn).getErrorStream();
                int ret;
                while (es != null && (ret = es.read(buf)) > 0)
                { // read the response body
                    os.write(buf, 0, ret);
                }
                if (es != null)
                {
                    es.close();// close the errorstream
                }
                msg = msg + ";Reason:" + new String(os.toByteArray(), "utf-8");
            }
            throw new IOException(msg, e.getCause());
        }
    }
    
    private boolean isSuccessCode(int httpCode)
    {
        return httpCode < BAD_REQUEST_CODE;
    }
}