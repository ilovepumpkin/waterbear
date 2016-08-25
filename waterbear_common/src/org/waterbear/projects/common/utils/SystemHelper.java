package org.waterbear.projects.common.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Properties;

import org.waterbear.core.exception.AutomationException;

public final class SystemHelper {
	public static Properties props = System.getProperties();
	public static String OS_NAME = getPropertery("os.name");
	public static String OS_LINE_SEPARATOR = getPropertery("line.separator");
	public static String OS_FILE_SEPARATOR = getPropertery("file.separator");

	public static InetAddress getSystemLocalIp() throws UnknownHostException {
		InetAddress inet = null;
		String osname = getSystemOSName();
		try {
			if (osname.startsWith("Windows")) {
				inet = getWinLocalIp();
			} else if (osname.equalsIgnoreCase("Linux")) {
				inet = getUnixLocalIp();
			}
			if (null == inet) {
				throw new AutomationException("OS ["+osname+"] is not supported.");
			}
		} catch (SocketException e) {
			throw new AutomationException(e);
		}
		return inet;
	}

	public static String getHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			throw new AutomationException(e);
		}
	}

	public static String getSystemOSName() {
		String osname = props.getProperty("os.name");
		return osname;
	}

	public static String getPropertery(String propertyName) {
		return props.getProperty(propertyName);
	}

	private static InetAddress getWinLocalIp() throws UnknownHostException {
		InetAddress inet = InetAddress.getLocalHost();
		return inet;
	}

	private static InetAddress getUnixLocalIp() throws SocketException {
		Enumeration<NetworkInterface> netInterfaces = NetworkInterface
				.getNetworkInterfaces();
		while (netInterfaces.hasMoreElements()) {
			NetworkInterface ni = (NetworkInterface) netInterfaces
					.nextElement();
			Enumeration<?> inetAddresses = ni.getInetAddresses();
			while (inetAddresses.hasMoreElements()) {
				InetAddress ip = (InetAddress) inetAddresses.nextElement();
				if (!ip.isLoopbackAddress()
						&& ip.getHostAddress().indexOf(":") == -1) {
					return ip;
				}
			}
		}
		return null;
	}

	public static final String getRAMinfo() {
		Runtime rt = Runtime.getRuntime();
		return "RAM: " + rt.totalMemory() / 1024 / 1024 + " MB total, "
				+ rt.freeMemory() / 1024 / 1024 + " MB free.";
	}
}
