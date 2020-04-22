package chay.http.transport.client;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ejtone.mars.kernel.util.CharsetUtil;
import com.ejtone.mars.kernel.util.JsonUtil;
import com.ejtone.mars.kernel.util.MixUtil;

public class HttpClient {

	private static final Logger logger = LoggerFactory.getLogger( HttpClient.class );

	public String client( String url, Object object ) {

		String res = "";
		HttpPost post = new HttpPost( url );
		ContentType contenttype = ContentType.APPLICATION_JSON.withCharset( Charset.forName( CharsetUtil.UTF_8 ) );
		HttpEntity entity = EntityBuilder.create( ).setText( JsonUtil.toJsonString( object ) ).setContentType( contenttype ).build( );
		post.setEntity( entity );

		logger.info( "test, url = {}", url );
		CloseableHttpClient client = HttpClientBuilder.create( ).build( );
		CloseableHttpResponse response = null;
		try {
			response = client.execute( post );
			logger.info( "statusCode = {}, line = {}", response.getStatusLine( ).getStatusCode( ), response.getStatusLine( ).getReasonPhrase( ) );
			HttpEntity resEntity = response.getEntity( );
			if( resEntity == null ) {
				return "";
			}

			Charset resCharset = ContentType.getOrDefault( resEntity ).getCharset( );
			String contentCharset = resCharset == null ? HTTP.DEF_CONTENT_CHARSET.name( ) : resCharset.name( );
			byte[] resData = EntityUtils.toByteArray( resEntity );
			res = new String( resData, contentCharset );

			logger.info( "HttpClient = {} ", res );
		} catch( ClientProtocolException e ) {

			e.printStackTrace( );
		} catch( IOException e ) {
			e.printStackTrace( );
		} finally {
			MixUtil.safeClose( response );
			MixUtil.safeClose( client );
		}

		return res;

	}
}
