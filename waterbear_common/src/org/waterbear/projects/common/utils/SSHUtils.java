package org.waterbear.projects.common.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.waterbear.core.exception.CLIException;
import org.waterbear.projects.common.ProjectConfiguration;

public class SSHUtils {
	private static SSHConn conn;
	protected static Logger log = Logger.getLogger(SSHUtils.class
			.getSimpleName());

	private static boolean isLocal = false;

	public static void setLocal() {
		isLocal = true;
	}

	public boolean isLocal() {
		return isLocal;
	}

	public static String exec(String... commands) {
		if (isLocal) {
			return doLocalExec(commands);
		} else {
			return doExec(commands);
		}
	}

	/**
	 * Execute with checking if the result is blank. If so, it will retry for
	 * specified times.
	 * 
	 * @param commands
	 * @return
	 */
	public static String fetch(String... commands) {
		if (isLocal) {
			return doLocalExec(commands);
		} else {
			return doExec(commands);
		}
	}

	/**
	 * Commands executed in CLI
	 * 
	 * @param the
	 *            command is array
	 * @return String output
	 * @throws Exception
	 */
	private static String doExec(String[] commands) {
		ProjectConfiguration c = ProjectConfiguration.getInstance();
		final String host = c.getCliHost();
		final String userName = c.getProperty("cli.username");
		final String password = c.getProperty("cli.password");
		final int port = Integer.parseInt(c.getProperty("cli.port"));

		return doExec(host, port, userName, password, commands);
	}

	/**
	 * 
	 * @param host
	 * @param port
	 * @param userName
	 * @param password
	 * @param commands
	 * @return
	 */
	private static String doExec(String host, int port, String userName,
			String password, String[] commands) {
		if (conn == null || !conn.isConnected()) {
			log.info("reconnect " + host + ":" + port + " (" + userName + "/"
					+ password + ")");
			conn = new SSHConn(host, port, userName, password);
			conn.connect();
		}
		String output = conn.execCmds(commands);
		// remove the last '\n'
		output = output.replaceFirst("\\n$", "");
		// sshConn.disconnect();
		return output;
	}

	public static void disconnect() {
		if (conn != null) {
			conn.disconnect();
		}
	}

	private static void scp(String host, int port, String userName,
			String password, String src, String dst) {
		SSHConn sshConn = new SSHConn(host, port, userName, password);
		sshConn.connect();
		sshConn.scp(src, dst);
		sshConn.disconnect();
	}

	public static void scp(String src, String dst) {
		if (!isLocal) {
			ProjectConfiguration c = ProjectConfiguration.getInstance();
			final String host = c.getCliHost();
			final String userName = c.getProperty("cli.username");
			final String password = c.getProperty("cli.password");
			final int port = Integer.parseInt(c.getProperty("cli.port"));
			scp(host, port, userName, password, src, dst);
		} else {
			log.info("Scp operation skipped. Local mode does not support SCP.");
		}
	}

	/**
	 * Test if the given IP address is reachable or not.
	 * 
	 * @param ipAddress
	 * @return
	 */
	public static boolean testIPReachable(String ipAddress) {
		String output = fetch("ping -c 1 " + ipAddress);
		return output.contains("Destination Host Unreachable") ? false : true;
	}

	private static String loadStream(InputStream in) throws IOException {
		int ptr = 0;
		in = new BufferedInputStream(in);
		StringBuffer buffer = new StringBuffer();
		while ((ptr = in.read()) != -1) {
			buffer.append((char) ptr);
		}
		return buffer.toString();
	}

	private static String doLocalExec(String... commands) {
		String oneLineCommand = "";
		for (int i = 0; i < commands.length; i++) {
			oneLineCommand += commands[i];
			if (i != commands.length - 1) {
				oneLineCommand = oneLineCommand + "&&";
			}
		}

		InputStream err = null;
		InputStream in = null;
		try {
			Process p = Runtime.getRuntime().exec(oneLineCommand);
			err = p.getErrorStream();
			String errStr = loadStream(err);
			if (errStr.trim().length() > 0) {
				throw new CLIException(errStr);
			} else {
				in = p.getInputStream();
				String output = loadStream(in).replaceFirst("\\n$", "");
				return output;
			}

		} catch (Exception e) {
			throw new CLIException(e);
		} finally {
			if (err != null) {
				try {
					err.close();
				} catch (IOException e) {
					throw new CLIException(e);
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					throw new CLIException(e);
				}
			}
		}
	}
}
