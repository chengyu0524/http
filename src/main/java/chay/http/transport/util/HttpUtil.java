package chay.http.transport.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

import chay.http.transport.client.HttpClientHolder;

public class HttpUtil {

	private static final Logger logger = LoggerFactory.getLogger( HttpUtil.class );

	private static final HttpClientBuilder httpClientBuilder = HttpClientBuilder.create( );

	public static CloseableHttpResponse post( String url, String dat, ContentType type ) throws Exception {
		HttpPost post = new HttpPost( url );
		HttpEntity entity = EntityBuilder.create( ).setText( dat ).setContentType( type ).build( );
		post.setEntity( entity );
		CloseableHttpClient client = HttpClientHolder.getInstance( ).getClient( );
		CloseableHttpResponse resp = client.execute( post );
		return resp;

	}
	/**带有请求头的httpPost*/
	public static CloseableHttpResponse post(String url, String dat, Map<String, String> headers, ContentType type) throws Exception {
    HttpPost post = new HttpPost(url);
    HttpEntity entity = EntityBuilder.create().setText(dat).setContentType(type).build();
    post.setEntity(entity);
    if (headers != null && headers.size() > 0) {
        Set<String> keySet = headers.keySet();
        Iterator<String> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            post.setHeader(next, headers.get(next));
        }
    }
    CloseableHttpResponse resp = HttpClientHolder.getInstance().getClient().execute(post);
    return resp;
	}

	public static String readHttpResponse( HttpResponse response ) throws IOException {
		HttpEntity resEntity = response.getEntity( );
		if( resEntity == null ) {
			return null;
		}
		try {
			Charset charset = ContentType.getOrDefault( resEntity ).getCharset( );
			String contentCharset = charset == null ? HTTP.DEF_CONTENT_CHARSET.name( ) : charset.name( );
			byte[] data = EntityUtils.toByteArray( resEntity );

			return new String( data, contentCharset );
		} finally {
			EntityUtils.consume( resEntity );
		}
	}

	public static String getPostData( HttpServletRequest req ) throws IOException {

		byte[] data = FileCopyUtils.copyToByteArray( req.getInputStream( ) );
		ContentType ctype = StringUtils.isBlank( req.getContentType( ) ) ? ContentType.APPLICATION_JSON : ContentType.parse( req.getContentType( ) );

		Charset charset = ctype.getCharset( ) != null ? ctype.getCharset( ) : Consts.UTF_8;

		return new String( data, charset );
	}

	public static void writeHttpResponse( HttpServletResponse resp, int code, String error ) {
		logger.debug( "send raw http response, code = {}, error = {}", code, error );
		try {
			resp.sendError( code, error );
		} catch( IOException e ) {
			logger.error( "", e );
		}
	}

	public static void writeResponse( HttpServletRequest req, HttpServletResponse resp, String msg ) throws IOException {
		if( logger.isDebugEnabled( ) ) {
			logger.debug( "send raw http response - content:[{}]", msg );
		}
		ContentType ctype = StringUtils.isBlank( req.getContentType( ) ) ? ContentType.APPLICATION_JSON : ContentType.parse( req.getContentType( ) );
		Charset charset = ctype.getCharset( ) != null ? ctype.getCharset( ) : Consts.UTF_8;

		resp.setContentType( ctype.toString( ) );
		resp.getOutputStream( ).write( msg.getBytes( charset ) );
		resp.getOutputStream( ).flush( );
	}

	public static void writeResponse( HttpServletResponse resp, String msg, ContentType ctype ) throws IOException {
		if( logger.isDebugEnabled( ) ) {
			logger.debug( "send raw http response - content:[{}]", msg );
		}
		Charset charset = ctype.getCharset( ) != null ? ctype.getCharset( ) : Consts.UTF_8;

		resp.setContentType( ctype.toString( ) );
		resp.getOutputStream( ).write( msg.getBytes( charset ) );
		resp.getOutputStream( ).flush( );
	}

	public static String postFile( File file, String url, String tag ) throws ClientProtocolException, IOException {
		HttpClient httpclient = httpClientBuilder.build( );
		HttpPost httppost = new HttpPost( url );

		MultipartEntityBuilder b = MultipartEntityBuilder.create( );
		b.addBinaryBody( "file1", file );

		httppost.setEntity( b.build( ) );
		logger.debug( "execute = {} ", httppost.getRequestLine( ) );
		HttpResponse response = httpclient.execute( httppost );
		logger.debug( "statusCode = {}, line = {}", response.getStatusLine( ).getStatusCode( ), response.getEntity( ) );
		HttpEntity resEntity = response.getEntity( );
		if( resEntity != null ) {
			logger.debug( "response content-length {}, content-type {} ", resEntity.getContentLength( ), resEntity.getContentType( ) );
			Charset charset = ContentType.getOrDefault( resEntity ).getCharset( );
			String contentCharset = charset == null ? HTTP.DEF_CONTENT_CHARSET.name( ) : charset.name( );
			byte[] data = EntityUtils.toByteArray( resEntity );

			String res = new String( data, contentCharset );

			logger.debug( "response = {} ", res );
		}
		if( resEntity != null ) {
			EntityUtils.consume( resEntity );
		}
		return null;
	}

	/**
	 * post file
	 * 
	 * @param file
	 * @param url
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String postFile( File file, String url ) throws ClientProtocolException, IOException {
		return postFile( file, url, "upload" );
	}

	public static void safeClose( CloseableHttpResponse o ) {
		if( o == null ) {
			return;
		}

		if( o.getEntity( ) != null ) {
			try {
				EntityUtils.consume( o.getEntity( ) );
			} catch( IOException e ) {
				logger.error( "", e );
			}
		}
		
		try {
			o.close( );
		} catch( Throwable t ) {
			logger.error( "", t );
		}
	}
	
	public static void main( String[] args ) {
		String url = "http://114.113.159.230:9005/upload";
		try {
			HttpUtil.postFile( new File( "./res/aaa.png" ), url );
		} catch( ClientProtocolException e ) {
			e.printStackTrace( );
		} catch( IOException e ) {
			e.printStackTrace( );
		}
	}

}
