package com.szzt.smart.framework.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Hikaru on 17/8/8.
 */
public interface IpUtil
{
    
    String[] IP_HEADS = new String[] {"x-forwarded-for", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP",
        "HTTP_X_FORWARDED_FOR",};
    
    static String getRemoteAddress(HttpServletRequest request)
    {
        for (String header : IP_HEADS)
        {
            String ip = request.getHeader(header);
            if (StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip))
            {
                if (ip.contains(","))
                {
                    ip = ip.substring(0, ip.indexOf(","));
                }
                return ip;
            }
        }
        return request.getRemoteAddr();
    }
    
    static InetAddress getLocalHostAddress()
    {
        try
        {
            InetAddress candidateAddress = null;
            for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces
                .hasMoreElements();)
            {
                NetworkInterface iface = ifaces.nextElement();
                for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();)
                {
                    InetAddress inetAddr = inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress())
                    {
                        if (inetAddr.isSiteLocalAddress())
                        {
                            return inetAddr;
                        }
                        else if (candidateAddress == null)
                        {
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }
            if (candidateAddress != null)
            {
                return candidateAddress;
            }
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null)
            {
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to determine LAN address: " + e);
        }
    }
    
}
