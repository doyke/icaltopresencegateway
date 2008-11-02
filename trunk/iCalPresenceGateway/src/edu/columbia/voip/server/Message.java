package edu.columbia.voip.server;

import java.net.Socket;

public abstract class Message {
	
	// timestamp (milliseconds) when message was received
	protected long _timestamp = -1L;
	public long getTimestamp() 					{ return _timestamp; }
	public void setTimestamp(long timestamp) 	{ this._timestamp = timestamp; }
	
	// from whom message is from. could be network IP or actually person's name? 
	protected String _from = null;
	public String getFrom()						{ return _from; }
	public void setFrom(String from)			{ this._from = from; }
	
	// from whom message is from. could be network IP or actually person's name?
	protected String _to = null;
	public String getTo()						{ return _to; }
	public void setTo(String to)				{ this._to = to; }
	
	// destination or receiving socket
	protected Socket _clientSocket = null;
	public Socket getClientSocket()				{ return _clientSocket; }
	public void setClientSocket(Socket socket)	{ this._clientSocket = socket; }
	
	
	public abstract void send();
	
	public abstract boolean isValid();
	
	public String toString() {
		return 	"[MESSAGE TimeStamp:" + _timestamp + 
				", From:" + _from + 
				", To:" + _to + 
				", ClientSocket:" + ((_clientSocket == null) ? ("null]") : (_clientSocket.getInetAddress().getHostAddress() + ":" + _clientSocket.getPort())) + "]";
	}
	
}
