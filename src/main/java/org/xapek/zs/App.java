package org.xapek.zs;

import java.io.File;
import java.io.FileInputStream;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Properties;

import jline.ConsoleReader;
import jline.Terminal;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.protocol.Protocol;

public class App {

	public static void main(String[] args) throws Exception {
		HttpClient client;
		String[] usernamePassword;

		setupHTTPS();

		// Lese Benutzername / Passwort
		usernamePassword = getUsernamePassword();

		// Präventiv abmelden
		client = new HttpClient();
		GetMethod logout = new GetMethod(
				"https://zerberus.ba-horb.de/logout.php");
		client.executeMethod(logout);

		// Hole Cookie
		client = new HttpClient();
		PostMethod method = new PostMethod(
				"https://zerberus.ba-horb.de/login-exec.php");
		client.executeMethod(method);
		Header cookie = method.getResponseHeader("Set-Cookie");
		assert cookie != null && cookie.getValue() != null : "Habe keine Set-Cookie Anweisung bekommen";

		// Es scheint, dass kaka Zerberus das so braucht
		client.executeMethod(new GetMethod(
				"https://zerberus.ba-horb.de/index.php"));

		// System.out.println(Arrays.toString(method.getResponseHeaders()));

		client = new HttpClient();
		PostMethod postLogin = new PostMethod(
				"https://zerberus.ba-horb.de/login-exec.php");
		postLogin.setParameter("username", usernamePassword[0]);
		postLogin.setParameter("password", usernamePassword[1]);
		postLogin.setParameter("submit", "Log on");
		postLogin
				.addRequestHeader(
						"Accept",
						"application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
		postLogin.addRequestHeader("Accept-Charset",
				"ISO-8859-1,utf-8;q=0.7,*;q=0.3");
		postLogin.addRequestHeader("Accept-Encoding", "gzip,deflate,sdch");
		postLogin.addRequestHeader("Accept-Language",
				"de-DE,de;q=0.8,en-US;q=0.6,en;q=0.4");
		postLogin.addRequestHeader("Cache-Control", "max-age=0");
		postLogin.addRequestHeader("Connection", "keep-alive");
		postLogin.addRequestHeader("Content-Type",
				"application/x-www-form-urlencoded");
		postLogin.addRequestHeader("Cookie", cookie.getElements()[0].getName()
				+ "=" + cookie.getElements()[0].getValue());
		postLogin.addRequestHeader("Host", "zerberus.ba-horb.de");
		postLogin.addRequestHeader("Origin", "https://zerberus.ba-horb.de");
		postLogin.addRequestHeader("Referer", "https://zerberus.ba-horb.de/");
		postLogin
				.addRequestHeader(
						"User-Agent",
						"Mozilla/5.0 (X11; U; Linux x86_64; en-US) AppleWebKit/534.13 (KHTML, like Gecko) Chrome/9.0.597.19 Safari/534.13");
		client.executeMethod(postLogin);

		// System.out.println(Arrays.toString(postLogin.getRequestHeaders()));
		// System.out.println(Arrays.toString(postLogin.getParameters()));
		// System.out.println(postLogin.getResponseBodyAsString());
		// System.out.println("Response:");
		// System.out.println(Arrays.toString(postLogin.getResponseHeaders()));

		ConsoleReader r = new ConsoleReader();

		for (int counter = 0;; counter++) {
			try {
				client = new HttpClient();
				GetMethod getDrei = new GetMethod(
						"https://zerberus.ba-horb.de/online.php");
				getDrei.addRequestHeader(
						"Cookie",
						cookie.getElements()[0].getName() + "="
								+ cookie.getElements()[0].getValue());
				client.executeMethod(getDrei);
				// System.out.println(Arrays.toString(getDrei.getRequestHeaders()));
				// System.out.println(Arrays.toString(getDrei.getResponseHeaders()));
				Thread.sleep(1500);

				switch (counter % 8) {
				case 0:
				case 4:
					r.printString("\r/");
					break;
				case 1:
				case 5:
					r.printString("\r-");
					break;
				case 2:
				case 6:
					r.printString("\r\\");
					break;
				case 3:
				case 7:
					r.printString("\r|");
					break;
				}
				r.flushConsole();
			} catch (Exception e) {
				System.err.println(new Date() + " - "
						+ e.getClass().getSimpleName() + ": " + e.getMessage());
				Thread.sleep(2000);
			}
		}
	}

	private static void setupHTTPS() throws GeneralSecurityException {
		@SuppressWarnings("deprecation")
		Protocol easyhttps = new Protocol("https",
				new InsecureSSLProtocolSocketFactory(), 443);
		Protocol.registerProtocol("https", easyhttps);
	}

	private static String[] getUsernamePassword() throws Exception {
		String username = null;
		String password = null;
		try {
			Properties p = new Properties();
			p.load(new FileInputStream(new File(
					System.getProperty("user.home"), ".zerberus")));
			username = p.getProperty("username");
			if ("".equals(username))
				username = null;
			password = p.getProperty("password");
			if ("".equals(password))
				password = null;
		} catch (Exception e) {
		}
		if (username == null || password == null) {
			// Terminal terminal =
			Terminal.setupTerminal();
			ConsoleReader r = new ConsoleReader();
			if (username == null) {
				username = r.readLine("Username: ");
				if (username == null)
					throw new Exception("Failed to read username");
			}
			if (password == null) {
				password = r.readLine("Password: ", '*');
				if (password == null)
					throw new Exception("Failed to read password");
			}
		}
		return new String[] { username, password };
	}
}