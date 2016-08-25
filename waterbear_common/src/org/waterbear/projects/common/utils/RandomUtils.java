package org.waterbear.projects.common.utils;

import java.text.NumberFormat;
import java.util.Random;

import org.waterbear.core.exception.AutomationException;

public class RandomUtils {

	public static boolean rndBoolean() {
		return new Random().nextInt(2) == 0;
	}

	/**
	 * 
	 * @param start
	 *            inclusive
	 * @param end
	 *            inclusive
	 * @return
	 */
	public static int rndInteger(int start, int end) {
		return start + new Random().nextInt(end);
	}

	public static String rndSuffix(String str) {
		return str + new Random().nextInt(9999);
	}
    /**
     * specify length of random generate  String
     * @param str
     * @param min    1000
     * @param max    9999
     * @return
     */
	public static String rndSuffix(String str,int min,int max){
		
		return str + Math.round(Math.random()*(max-min)+min) ;
	}
	
	public static String rndPrefix(String str) {
		return new Random().nextInt(9999) + str;
	}

	public static String rndIP() {
		return rndIP(4);
	}

	public static String rndIP6() {
		String base = "2002:0930:9b04:0323:0009:0048:0151:";
		int rndNum = new Random().nextInt(9999);
		return base + addLeftZeros(rndNum, 4);
	}

	private static String addLeftZeros(int number, int len) {
		NumberFormat nf = NumberFormat.getInstance();
		nf.setGroupingUsed(false);
		nf.setMaximumIntegerDigits(len);
		nf.setMinimumIntegerDigits(len);
		return nf.format(number);
	}

	public static String rndIP(int size) {
		Random r = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < size; i++) {
			String part = String.valueOf(r.nextInt(255) + 1);
			sb.append(".").append(part);
		}
		return sb.toString().substring(1);
	}

	public static String rndIP(String leftParts) {
		int size = 4 - leftParts.split("\\.").length;
		return leftParts + "." + rndIP(size);
	}

	/**
	 * Return a random unreachable IP with the given left parts (e.g.
	 * "10.0.100")
	 * 
	 * @param leftParts
	 * @return
	 */
	public static String rndUnreachableIP(String leftParts) {
		int maxTryCount = 10;
		String ip = null;
		do {
			ip = rndIP(leftParts);
			maxTryCount -= 1;
		} while (SSHUtils.testIPReachable(ip) && maxTryCount > 0);
		if (ip == null) {
			throw new AutomationException("Tried " + maxTryCount
					+ " ips but cannot find one unreachable.");
		}
		return ip;
	}

	public static String pickUp(String[] items) {
		return new RandomCollection<String>(items).pickUp().toString();
	}
}
