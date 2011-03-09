/*
 * Copyright (c) 2002-2010 Gargoyle Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xapek.zs;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;

/**
 * A completely insecure (yet very easy to use) SSL socket factory. This socket
 * factory will establish connections to any server from any client, regardless
 * of credentials or the lack thereof. This is especially useful when you are
 * trying to connect to a server with expired or corrupt certificates... this
 * class doesn't care!
 *
 * @version $Revision: 5301 $
 * @author Daniel Gredler
 * @see com.gargoylesoftware.htmlunit.WebClient#setUseInsecureSSL(boolean)
 */
public class InsecureSSLProtocolSocketFactory implements
		SecureProtocolSocketFactory {

	private SSLContext context_;

	/**
	 * Creates a new insecure SSL protocol socket factory.
	 *
	 * @throws GeneralSecurityException
	 *             if a security error occurs
	 */
	public InsecureSSLProtocolSocketFactory() throws GeneralSecurityException {
		context_ = SSLContext.getInstance("SSL");
		context_.init(null, new TrustManager[] { new X509TrustManager() {

			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		} }, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public Socket createSocket(final Socket socket, final String host,
			final int port, final boolean autoClose) throws IOException {
		Socket createSocket = context_.getSocketFactory().createSocket(socket,
				host, port, autoClose);
		createSocket.setSoTimeout(1);
		return createSocket;
	}

	/**
	 * {@inheritDoc}
	 */
	public Socket createSocket(final String host, final int port)
			throws IOException, UnknownHostException {
		Socket createSocket = context_.getSocketFactory().createSocket(host,
				port);
		createSocket.setSoTimeout(1);
		return createSocket;
	}

	/**
	 * {@inheritDoc}
	 */
	public Socket createSocket(final String host, final int port,
			final InetAddress localAddress, final int localPort)
			throws IOException {
		Socket createSocket = context_.getSocketFactory().createSocket(host,
				port, localAddress, localPort);
		createSocket.setSoTimeout(1);
		return createSocket;
	}

	/**
	 * {@inheritDoc}
	 */
	public Socket createSocket(final String host, final int port,
			final InetAddress localAddress, final int localPort,
			final HttpConnectionParams params) throws IOException {
		Socket createSocket = context_.getSocketFactory().createSocket(host,
				port, localAddress, localPort);
		createSocket.setSoTimeout(1);
		return createSocket;
	}

}
