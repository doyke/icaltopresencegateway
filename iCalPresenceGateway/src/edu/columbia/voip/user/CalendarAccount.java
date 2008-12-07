package edu.columbia.voip.user;

import java.io.Serializable;

public class CalendarAccount extends GenericAccount implements Serializable
{
	private static final long serialVersionUID = -2343482468103106634L;
	
	private String 	_uri = null;
	
	public CalendarAccount(String user, char[] pass, String host, String uri, int port, boolean ssl)
	{
		setUsername(user);
		setPassword(pass);
		setHost(host);
		setUri(uri);
		setPort(port);
		isSSLEnabled(ssl);
	}

	public void setUri(String _uri) {
		this._uri = _uri;
	}

	public String getUri() {
		return _uri;
	}
}
