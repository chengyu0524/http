package chay.http.transport.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;

public class FilterServer extends Server {
	@Override
	public void handle( HttpChannel connection ) throws IOException, ServletException {
		Request request = connection.getRequest( );
		Response response = connection.getResponse( );
		if("TRACE".equals( request.getMethod( ) )){
			request.setHandled( true );
			response.setStatus( HttpServletResponse.SC_METHOD_NOT_ALLOWED );
		}else{
			super.handle( connection );
		}
	}
}
