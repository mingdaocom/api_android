package com.mingdao.sdk;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;


/**
 * Mason Liu 天盟
 */
public class MDUtil {

	private static final String CHARSET = "UTF-8";
	private static final String PREFIX = "--", LINEND = "\r\n";
	private static final String MULTIPART_FROM_DATA = "multipart/form-data";
	public static boolean IS_CMWAP = false;
	private static final String CMWAP_HOST = "10.0.0.172";
	private static final int CMWAP_PORT = 80;

	private static void abortConnection(final HttpRequestBase hrb,
			final HttpClient httpclient) {
		if (hrb != null) {
			try {
				hrb.abort();
			} catch (Exception e) {
			}
		}
		if (httpclient != null) {
			try {
				httpclient.getConnectionManager().shutdown();
			} catch (Exception e) {
			}
		}
	}
	
	public static String httpByGet2StringSSL(String url, String paramsCharset,
			String resultCharset) {
		if (url == null || "".equals(url)) {
			return null;
		}
		String responseStr = null;
		HttpClient httpClient = null;
		HttpGet hg = null;
		try {
			httpClient = getNewHttpClient();
			hg = new HttpGet(url);
			HttpResponse response = httpClient.execute(hg);
			if (resultCharset == null || "".equals(resultCharset)) {
				responseStr = EntityUtils.toString(response.getEntity(),
						CHARSET);
			} else {
				responseStr = EntityUtils.toString(response.getEntity(),
						resultCharset);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			abortConnection(hg, httpClient);
		}
		return responseStr;
	}
	
	public static class SSLSocketFactoryEx extends SSLSocketFactory {

		SSLContext sslContext = SSLContext.getInstance("TLS");

		public SSLSocketFactoryEx(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] chain,
						String authType)
						throws java.security.cert.CertificateException {
				}

				@Override
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] chain,
						String authType)
						throws java.security.cert.CertificateException {
				}
			};
			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

	public static HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);
			DefaultHttpClient httpClient = new DefaultHttpClient(ccm, params);
			if (IS_CMWAP) {
				HttpHost proxy = new HttpHost(CMWAP_HOST, CMWAP_PORT);
				httpClient.getParams().setParameter(
						ConnRoutePNames.DEFAULT_PROXY, proxy);
			}
			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}
	public static String sendFile(String actionUrl, Map<String, String> params,
			MDFile[] files) {

		String BOUNDARY = java.util.UUID.randomUUID().toString();

		URL uri = null;

		HttpsURLConnection conn = null;
		DataOutputStream outStream = null;
		String result = "";
		try {
			X509TrustManager xtm = new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				@Override
				public void checkClientTrusted(X509Certificate[] arg0,
						String arg1) throws CertificateException {
					// TODO Auto-generated method stub

				}
				@Override
				public void checkServerTrusted(X509Certificate[] arg0,
						String arg1) throws CertificateException {
					// TODO Auto-generated method stub

				}
			};
			HostnameVerifier hnv = new HostnameVerifier() {
				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					// TODO Auto-generated method stub
					return true;
				}
			};
			SSLContext sslContext = null;
			try {
				sslContext = SSLContext.getInstance("TLS");
				X509TrustManager[] xtmArray = new X509TrustManager[] { xtm };
				sslContext.init(null, xtmArray,
						new java.security.SecureRandom());
			} catch (GeneralSecurityException gse) {
			}

			if (sslContext != null) {
				HttpsURLConnection.setDefaultSSLSocketFactory(sslContext
						.getSocketFactory());
			}
			HttpsURLConnection.setDefaultHostnameVerifier(hnv);
			uri = new URL(actionUrl);

			if (IS_CMWAP) {
				InetSocketAddress addr = new InetSocketAddress(CMWAP_HOST,CMWAP_PORT);
				Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
				conn = (HttpsURLConnection) uri.openConnection(proxy);
			} else {
				conn = (HttpsURLConnection) uri.openConnection();
			}
			conn.setInstanceFollowRedirects(true);
			conn.setReadTimeout(30 * 1000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Charsert", CHARSET);
			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);
			conn.connect();
			StringBuilder sb = new StringBuilder();
			if (params != null)
				for (Map.Entry<String, String> entry : params.entrySet()) {
					sb.append(PREFIX);
					sb.append(BOUNDARY);
					sb.append(LINEND);
					sb.append("Content-Disposition: form-data; name=\""
							+ entry.getKey() + "\"" + LINEND);
					sb.append("Content-Type: text/plain; charset=" + CHARSET
							+ LINEND);
					sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
					sb.append(LINEND);
					sb.append(entry.getValue());
					sb.append(LINEND);
				}

			outStream = new DataOutputStream(conn.getOutputStream());
			outStream.write(sb.toString().getBytes());
			// 发送文件数据
			for (MDFile file : files) {
				StringBuilder split = new StringBuilder();
				split.append("--");
				split.append(BOUNDARY);
				split.append(LINEND);
				split.append("Content-Disposition: form-data;name=\""
						+ file.getFormname() + "\";filename=\""
						+ file.getFilename() + "\"" + LINEND);
				split.append("Content-Type: " + file.getContentType() + LINEND
						+ LINEND);
				outStream.write(split.toString().getBytes());
				outStream.write(file.getData(), 0, file.getData().length);
				outStream.write(LINEND.getBytes());
			}
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
			outStream.write(end_data);
			outStream.flush();
			int res = conn.getResponseCode();
			if (res != HttpStatus.SC_OK
					&& res != HttpStatus.SC_MOVED_PERMANENTLY
					&& res != HttpStatus.SC_MOVED_TEMPORARILY)
				if (res == HttpStatus.SC_MOVED_PERMANENTLY
						|| res == HttpStatus.SC_MOVED_TEMPORARILY) {

					result = httpByGet2StringSSL(
							(conn.getURL().getProtocol() + "://"
									+ conn.getURL().getHost() + "/" + conn
									.getHeaderField("Location")),
							null, null);
					return result;
				}
			ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
			InputStream in = conn.getInputStream();
			byte[] buffer = new byte[1024 * 4];
			int read_len = 0;
			while ((read_len = in.read(buffer)) != -1) {
				out.write(buffer, 0, read_len);
			}
			result = new String(out.toByteArray(), CHARSET);
			in.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				outStream.close();
			} catch (Exception e) {
			}
			try {
				conn.disconnect();
			} catch (Exception e) {
			}
		}
		return result;
	}
}