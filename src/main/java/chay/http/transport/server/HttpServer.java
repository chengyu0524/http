package chay.http.transport.server;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.MultipartConfigElement;

import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ejtone.mars.kernel.util.MixUtil;
import com.ejtone.mars.kernel.util.config.ConfigUtils;

public class HttpServer extends AbstractLifeCycle {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger( HttpServer.class );

	private String name = HttpServer.class.getName( );
	private FilterServer server;
	private int port = 8080;
	private ServletContextHandler context = new ServletContextHandler( ServletContextHandler.SESSIONS );

	public String getName( ) {
		return name;
	}

	public void setName( String name ) {
		if( isRunning( ) ) {
			logger.error( "can not set port after server initialized" );
			throw new IllegalStateException( );
		}
		this.name = name;
	}

	public void setPort( int port ) {
		if( isRunning( ) ) {
			logger.error( "can not set port after server initialized" );
			throw new IllegalStateException( );
		}

		this.port = port;
	}

	public void setServices( List<HttpService> services ) {
		synchronized( context ) {
			Iterator<HttpService> i = services.iterator( );
			while( i.hasNext( ) ) {
				HttpService service = i.next( );
				// String url = "/" + service.getUrl( ) + "/*";
				String url = MixUtil.mergeUrl( "/", service.getUrl( ) );
				logger.info( "regist url {} to service {}", url, service.getClass( ).getName( ) );
				ServletHolder holder = new ServletHolder( service );
				if( service.isSupportMultiPart( ) ) {
					long maxFileSize = ConfigUtils.getInt( "http.maxFileSize", 1048576 );
					long maxReqSize = ConfigUtils.getInt( "http.maxReqSize", 1048576 );
					int fileSizeThreshold = ConfigUtils.getInt( "http.fileSizeThreshold", 1048576 );
					holder.getRegistration( ).setMultipartConfig( new MultipartConfigElement( "data/tmp", maxFileSize, maxReqSize, fileSizeThreshold ) );
				}
				context.addServlet( holder, url );
			}
		}
	}



	public void doStart( ) throws Exception {
		logger.info( "http server bind port {}, starting...", port );
		server = new FilterServer( );
		ServerConnector connector = new ServerConnector( server );
		connector.setPort( port );
		server.addConnector( connector );
		server.setHandler( context );
		long maxFormContentSize = ConfigUtils.getInt( "http.maxFormContentSize", 200000 );
		server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", maxFormContentSize);
		server.start( );
	}

	public void doStop( ) throws Exception {
		server.stop( );
	}

	public static HttpServer createServer( String serverName, String serverUrl ) throws MalformedURLException {
		try {
			URL url = new URL( serverUrl );
			HttpServer httpTransportServer = new HttpServer( );
			DefaultHttpService service = new DefaultHttpService( );
			service.setUrl( url.getPath( ) );
			List<HttpService> services = new ArrayList<HttpService>( );
			services.add( service );

			httpTransportServer.setPort( url.getPort( ) );
			httpTransportServer.setName( serverName );
			httpTransportServer.setServices( services );
			return httpTransportServer;
		} catch( MalformedURLException e ) {
			logger.error( "serverUrl:{} is invalid", serverUrl );
			logger.error( "exception:", e );
			throw e;
		}
	}

}
