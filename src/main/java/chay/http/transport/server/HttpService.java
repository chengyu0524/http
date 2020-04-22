package chay.http.transport.server;

import javax.servlet.Servlet;


public interface HttpService  extends Servlet{
	public String getUrl( );

	public boolean isSupportMultiPart( );
}
