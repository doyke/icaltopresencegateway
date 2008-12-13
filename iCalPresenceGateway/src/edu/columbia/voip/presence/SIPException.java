/**
 * 
 */
package edu.columbia.voip.presence;

/**
 * @author jmoral
 *
 */
public class SIPException extends Exception
{

	/**
	 * 
	 */
	public SIPException()
	{
	}

	/**
	 * @param arg0
	 */
	public SIPException(String arg0)
	{
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public SIPException(Throwable arg0)
	{
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public SIPException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}
}
