package org.waterbear.projects.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.waterbear.core.exception.CLIException;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SSHConn {
	private String host;
	private int port;
	private String userName;
	private String password;
	private Session session;

	protected Logger log = Logger.getLogger(getClass().getSimpleName());

	public SSHConn(String host, int port, String userName, String password) {
		super();
		this.host = host;
		this.port = port;
		this.userName = userName;
		this.password = password;
	}

	public void connect() {
		final int CONNECT_MAX_TRY = 5;
		int tryCounter = 0;
		while (tryCounter < CONNECT_MAX_TRY) {
			try {
				tryCounter = tryCounter + 1;
				// log.info("Connect " + host + ":" + port +
				// " using the account "+ userName + "/" + password);
				session = new JSch().getSession(userName, host, port);
				session.setPassword(password);
				Properties config = new Properties();
				config.setProperty("StrictHostKeyChecking", "no");
				session.setConfig(config);
				session.connect();
				break;
			} catch (Exception e) {
				log.error("Failed to establish SSH the connection. [" + host
						+ ":" + port + "," + userName + "/" + password + "]", e);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					log.error(e1);
					throw new CLIException(e1);
				}
			}
		}
		if (tryCounter >= CONNECT_MAX_TRY) {
			throw new CLIException("After having tried " + tryCounter
					+ " times, " + host + " was not connected.");
		}
	}

	public String execCmds(String[] commands) {
		String oneLineCommand = "";
		for (int i = 0; i < commands.length; i++) {
			oneLineCommand += commands[i];
			if (i != commands.length - 1) {
				oneLineCommand = oneLineCommand + "&&";
			}
		}
		return execCmd(oneLineCommand);
	}

	private String readInputStream(InputStream is, ChannelExec channel,
			boolean isErrStream) {
		StringBuffer sb = new StringBuffer();
		try {
			byte[] tmp = new byte[1024];
			while (true) {
				while (is.available() > 0) {
					int i = is.read(tmp, 0, 1024);
					if (i < 0) {
						break;
					} else {
						sb.append(new String(tmp, 0, i));
					}
				}
				if (isErrStream) {
					break;
				} else {
					if (channel.isClosed()) {
						log.debug("Exit-status: " + channel.getExitStatus());
						break;
					}
				}
			}
		} catch (IOException e) {
			throw new CLIException("Failed to read input stream", e);
		}
		return sb.toString();
	}

	public String execCmd(String command) {
		int status = -1;
		if (session == null) {
			throw new CLIException(
					"Before you execute any commands, please connect the session.");
		}
		try {
			log.info("Executing the command [" + command + "]");
			ByteArrayOutputStream outStr = new ByteArrayOutputStream();
			ByteArrayOutputStream errStr = new ByteArrayOutputStream();
			ChannelExec channel = (ChannelExec) session.openChannel("exec");
			channel.setOutputStream(outStr);
			channel.setErrStream(errStr);

			channel.setCommand(command);
			channel.connect();

			// Gather the output from the command execution
			while (!channel.isClosed()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					throw new CLIException(e);
				}
			}

			// Cleanup the SSH connection
			status = channel.getExitStatus();
			channel.disconnect();

			final String errMessage = errStr.toString().trim();
			if (errMessage.length() > 0) {
				throw new CLIException("Failed to execute the command ["
						+ command + "].", errMessage);
			}

			if (status != 0) {
				throw new CLIException("Failed to execute the command ["
						+ command + "].", errMessage);
			}

			return outStr.toString();
		} catch (JSchException e) {
			throw new CLIException("Failed to execute the command [" + command
					+ "]", e);
		}
	}

	public String execCmd_old(String command) {
		if (session == null) {
			throw new CLIException(
					"Before you execute any commands, please connect the session.");
		}
		try {
			log.info("Executing the command [" + command + "]");
			ChannelExec exec = (ChannelExec) session.openChannel("exec");
			InputStream errIn = exec.getErrStream();
			InputStream in = exec.getInputStream();

			exec.setCommand(command);
			exec.connect();

			// Thread.sleep(1000);// sleep a little time to ensure the data is
			// available.

			String output = readInputStream(in, exec, false);
			log.debug("Execution result:\n" + output);

			int exitStatus = exec.getExitStatus();
			if (exitStatus != 0) {
				String err = readInputStream(errIn, exec, true);
				if (err.trim().length() > 0) {
					throw new CLIException("Failed to execute the command ["
							+ command + "].", err);
				} else {
					// in SONAS, some commands returns 1 but there is no problem
					// at all, so just log this instead of throwing an
					// exception.
					log.warn("The exit status is " + exitStatus
							+ ", but no error messages were found.");
				}
			}

			exec.disconnect();
			return output;
		} catch (CLIException e) {
			throw e;
		} catch (Exception e) {
			throw new CLIException("Failed to execute the command [" + command
					+ "]", e);
		}
	}

	public boolean isConnected() {
		return session != null && session.isConnected();
	}

	public void disconnect() {
		if (isConnected()) {
			session.disconnect();
			session = null;
		}
	}

	private int checkAck(InputStream in) throws IOException {
		int b = in.read();
		// b may be 0 for success,
		// 1 for error,
		// 2 for fatal error,
		// -1
		if (b == 0)
			return b;
		if (b == -1)
			return b;

		if (b == 1 || b == 2) {
			StringBuffer sb = new StringBuffer();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			} while (c != '\n');
			if (b == 1) { // error
				throw new CLIException(sb.toString());
			}
			if (b == 2) { // fatal error
				throw new CLIException(sb.toString());
			}
		}
		return b;
	}

	public void scp(String src, String dst) {
		FileInputStream fis = null;
		if (session == null) {
			throw new CLIException(
					"Before you execute any commands, please connect the session.");
		}
		try {
			boolean ptimestamp = false;
			// exec 'scp -t rfile' remotely
			String command = "scp " + (ptimestamp ? "-p" : "") + " -t " + dst;
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			// get I/O streams for remote scp
			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();

			channel.connect();
			checkAck(in);

			File _lfile = new File(src);

			if (ptimestamp) {
				command = "T " + (_lfile.lastModified() / 1000) + " 0";
				// The access time should be sent here,
				// but it is not accessible with JavaAPI ;-<
				command += (" " + (_lfile.lastModified() / 1000) + " 0\n");
				out.write(command.getBytes());
				out.flush();
				checkAck(in);
			}

			// send "C0644 filesize filename", where filename should not include
			// '/'
			long filesize = _lfile.length();
			command = "C0644 " + filesize + " ";
			if (src.lastIndexOf('/') > 0) {
				command += src.substring(src.lastIndexOf('/') + 1);
			} else {
				command += src;
			}
			command += "\n";
			out.write(command.getBytes());
			out.flush();
			checkAck(in);

			// send a content of src
			fis = new FileInputStream(src);
			byte[] buf = new byte[1024];
			while (true) {
				int len = fis.read(buf, 0, buf.length);
				if (len <= 0)
					break;
				out.write(buf, 0, len); // out.flush();
			}
			fis.close();
			fis = null;
			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();
			if (checkAck(in) != 0) {
				System.exit(0);
			}
			out.close();

			channel.disconnect();

			// Verify the remote file
			String flag = execCmd("if [ -f '" + dst
					+ "' ];then echo 'yes';else echo 'no';fi");
			if (!flag.replace("\n", "").equals("yes")) {
				throw new CLIException(
						"SCP might fail. Did not find the remote file [" + dst
								+ "].");
			} else {
				log.info("The local file [" + src + "] was uploaded to ["
						+ host + "[port:" + port + "]:" + dst
						+ "] successfully.");
			}
		} catch (Exception e) {
			try {
				if (fis != null)
					fis.close();
			} catch (Exception ee) {
				throw new CLIException(ee);
			}
		}
	}
}
