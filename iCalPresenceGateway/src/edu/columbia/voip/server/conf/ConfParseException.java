/**
 * 
 */
package edu.columbia.voip.server.conf;

/**
 * @author jmoral
 *
 */
public class ConfParseException extends Exception
{

	/**
	 * 
	 */
	public ConfParseException()
	{
	}

	/**
	 * @param arg0
	 */
	public ConfParseException(String arg0)
	{
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public ConfParseException(Throwable arg0)
	{
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ConfParseException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

}
