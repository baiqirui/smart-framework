package com.szzt.smart.framework.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostIpUtil {

  public static String hostIp = "";

  static {
    initHostIp();
  }

  public static void initHostIp() {
    InetAddress netAddress = IpUtil.getLocalHostAddress();
    hostIp = getHostIp(netAddress);
  }

  public static InetAddress getInetAddress() {
    try {
      return InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      System.out.println("unknown host!");
    }
    return null;

  }

  public static String getHostIp(InetAddress netAddress) {
    if (null == netAddress) {
      return null;
    }
    String ip = netAddress.getHostAddress(); //get the ip address
    return ip;
  }

}
