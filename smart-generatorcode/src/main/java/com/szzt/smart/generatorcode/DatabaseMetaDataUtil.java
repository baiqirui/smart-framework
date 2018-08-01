package com.szzt.smart.generatorcode;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * jdbc获取数据库的元数据信息
 */
public class DatabaseMetaDataUtil
{
    
    private static String url;
    
    private static String username;
    
    private static String password;
    
    /**
     * 初始化
     * 
     * @param driver
     * @param url1
     * @param username1
     * @param password1
     */
    public static void init(String driver, String url1, String username1, String password1)
    {
        // 初始化数据库连接信息
        url = url1;
        username = username1;
        password = password1;

        // 加载数据连接驱动
        try
        {
            Class.forName(driver);
        }
        catch (ClassNotFoundException e)
        {
            System.err.println("装载数据库连接驱动失败，url:  " + driver);
            e.printStackTrace();
        }
    }
    
    /**
     * 获取数据库中的表名称与视图名称
     *
     * @return
     */
    public static List getTablesAndViews(String dataBaseName)
        throws Exception
    {
        Connection conn = DriverManager.getConnection(url, username, password);
        ResultSet rs = null;
        List list = new ArrayList();
        try
        {
            DatabaseMetaData metaData = conn.getMetaData();
            rs = metaData.getTables(null, dataBaseName, null, new String[] {"TABLE", "VIEW"});
            while (rs.next())
            {
                String tableName = rs.getString("TABLE_NAME");
                list.add(tableName);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (rs != null)
            {
                rs.close();
            }
            if (conn != null)
            {
                conn.close();
            }
        }
        return list;
    }
    
}
