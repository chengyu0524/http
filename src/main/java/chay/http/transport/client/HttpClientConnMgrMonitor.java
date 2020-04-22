package chay.http.transport.client;

import java.util.concurrent.TimeUnit;

import org.apache.http.conn.HttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ejtone.mars.kernel.util.monitor.AbstractMonitor;

public class HttpClientConnMgrMonitor extends AbstractMonitor {
	private static final Logger logger = LoggerFactory.getLogger( HttpClientConnMgrMonitor.class );

	private final HttpClientConnectionManager connMgr;

	public HttpClientConnMgrMonitor( HttpClientConnectionManager connMgr ) {
		this.connMgr = connMgr;
	}

	public HttpClientConnectionManager getConnMgr( ) {
		return connMgr;
	}

	@Override
	protected Runnable getMonitorTask( ) {
		return new MgrTask( );
	}


	private final class MgrTask implements Runnable {
		@Override
		public void run( ) {
			try {
				HttpClientConnMgrMonitor.this.connMgr.closeExpiredConnections( ); // 关闭过期连接
				HttpClientConnMgrMonitor.this.connMgr.closeIdleConnections( 30, TimeUnit.SECONDS ); // 关闭超时连接
			} catch( Exception e ) {
				logger.error( "", e );
			}
		}
	}


}
