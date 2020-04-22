package chay.http.transport.client;

import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.InitializingBean;

public class HttpClientHolder implements InitializingBean {

	private int maxTotal = 2000;
	private int maxPerRoute = 200;
	private int connTimeout = 5000;
	private int requestTimeout = 5000;
	private int keepaliveTiemout = 1000;

	private CloseableHttpClient client;

	public static HttpClientHolder getInstance( ) {
		return InstanceHolder.instance;
	}

	private HttpClientHolder( ) {}


	public CloseableHttpClient getClient( ) {
		return client;
	}

	public int getMaxTotal( ) {
		return maxTotal;
	}

	public HttpClientHolder setMaxTotal( int maxTotal ) {
		this.maxTotal = maxTotal;
		return this;
	}

	public int getMaxPerRoute( ) {
		return maxPerRoute;
	}

	public HttpClientHolder setMaxPerRoute( int maxPerRoute ) {
		this.maxPerRoute = maxPerRoute;
		return this;
	}

	public int getConnTimeout( ) {
		return connTimeout;
	}

	public HttpClientHolder setConnTimeout( int connTimeout ) {
		this.connTimeout = connTimeout;
		return this;
	}

	public int getRequestTimeout( ) {
		return requestTimeout;
	}

	public HttpClientHolder setRequestTimeout( int requestTimeout ) {
		this.requestTimeout = requestTimeout;
		return this;
	}

	public int getKeepaliveTiemout( ) {
		return keepaliveTiemout;
	}

	public HttpClientHolder setKeepaliveTiemout( int keepaliveTiemout ) {
		this.keepaliveTiemout = keepaliveTiemout;
		return this;
	}

	@Override
	public void afterPropertiesSet( ) throws Exception {
		RequestConfig requestConfig = RequestConfig.custom( ).setSocketTimeout( requestTimeout ).setConnectTimeout( connTimeout ).build( );
		HttpClientBuilder builder = HttpClientBuilder.create( ).setDefaultRequestConfig( requestConfig );
		builder.evictExpiredConnections( ).evictIdleConnections( keepaliveTiemout, TimeUnit.MILLISECONDS );
		builder.setMaxConnTotal( maxTotal ).setMaxConnPerRoute( maxPerRoute );
		builder.setSSLSocketFactory( createSSLConnSocketFactory( ) );
		client = builder.build( );
	}


	private static SSLConnectionSocketFactory createSSLConnSocketFactory( ) throws GeneralSecurityException {
		SSLContext sslContext = new SSLContextBuilder( ).loadTrustMaterial( null, new TrustStrategy( ) {

			public boolean isTrusted( X509Certificate[] chain, String authType ) throws java.security.cert.CertificateException {
				return true;
			}
		} ).build( );
		HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
		return new SSLConnectionSocketFactory( sslContext, hostnameVerifier );
	}

	private static final class InstanceHolder {
		public static final HttpClientHolder instance = new HttpClientHolder( );
	}

}
