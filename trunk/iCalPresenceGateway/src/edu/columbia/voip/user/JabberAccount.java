/**
 * 
 */
package edu.columbia.voip.user;

import java.io.Serializable;

/**
 * @author jmoral
 *
 */
public class JabberAccount extends GenericAccount implements Serializable
{

	private static final long serialVersionUID = 2134836793511034215L;

	/**
	 * 
	 */
	public JabberAccount(String user, char[] pass, String host)
	{
		setUsername(user);
		setPassword(pass);
		setHost(host);
	}

}
