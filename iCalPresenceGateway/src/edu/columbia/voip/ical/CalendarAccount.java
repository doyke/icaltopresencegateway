package edu.columbia.voip.ical;

public class CalendarAccount {

	private String _username = null;
	private String _password = null;
	private String _host = null;
	private String _uri = null;
	private int    _port;
	private boolean _ssl_enabled = false; 
	
	public CalendarAccount(String user, String pass, String host, String uri, int port, boolean ssl)
	{
		setUsername(user);
		setPassword(pass);
		setHost(host);
		setUri(uri);
		setPort(port);
		isSSLEnabled(ssl);
	}

	public void setUsername(String _username) {
		this._username = _username;
	}

	public String getUsername() {
		return _username;
	}

	public void setPassword(String _password) {
		this._password = _password;
	}

	public String getPassword() {
		return _password;
	}

	public void setUri(String _uri) {
		this._uri = _uri;
	}

	public String getUri() {
		return _uri;
	}

	public void setPort(int _port) {
		this._port = _port;
	}

	public int getPort() {
		return _port;
	}

	public void isSSLEnabled(boolean _ssl_enabled) {
		this._ssl_enabled = _ssl_enabled;
	}

	public boolean isSSLEnabled() {
		return _ssl_enabled;
	}

	public void setHost(String _host) {
		this._host = _host;
	}

	public String getHost() {
		return _host;
	}
}
