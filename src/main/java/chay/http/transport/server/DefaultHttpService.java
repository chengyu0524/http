package chay.http.transport.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class DefaultHttpService extends HttpServlet implements HttpService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8306763040246470287L;

	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger( DefaultHttpService.class );
	private String url;
	private boolean supportMultiPart;

	@Override
	protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
		// do stuff
		
		response( resp, "{\"success\":true}", ContentType.APPLICATION_JSON);
		return;
	}

//	public void writeResponse( HttpServletResponse resp, int code, String error ) {
//		try {
//			resp.sendError( code, error );
//			resp.getOutputStream( ).flush( );
//		} catch( IOException e ) {
//			logger.error( "", e );
//		}
//	}
//
//
	public void setUrl( String url ) {
		this.url = StringUtils.trimTrailingCharacter( StringUtils.trimLeadingCharacter( url, '/' ), '/' );
	}

	public String getUrl( ) {
		return url;
	}

	public boolean isSupportMultiPart( ) {
		return supportMultiPart;
	}

	public void setSupportMultiPart( boolean supportMultiPart ) {
		this.supportMultiPart = supportMultiPart;
	}

	protected void response( HttpServletResponse rsp, String rspText, ContentType contentType ) throws IOException {
		logger.debug( "send raw http response, data = {}", rspText );
		rsp.setContentType( contentType.toString( ) );
		rsp.getOutputStream( ).write( rspText.getBytes( contentType.getCharset( ) ) );
		rsp.getOutputStream( ).flush( );
	}

	protected void response( HttpServletResponse resp, int code, String error ) {
		logger.debug( "send raw http response, code = {}, error = {}", code, error );
		try {
			resp.sendError( code, error );
		} catch( IOException e ) {
			logger.error( "", e );
		}
	}
}
