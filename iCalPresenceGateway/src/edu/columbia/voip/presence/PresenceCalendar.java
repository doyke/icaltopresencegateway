/**
 * 
 */
package edu.columbia.voip.presence;

/**
 * Struct for storing on limited set of calendar tags that we're passing 
 * to the SIP presence server. 
 * @author jmoral
 *
 */
public class PresenceCalendar
{
	private String _summary = null;
	private String _description = null;
	private String _location = null;
	private String _category = null;
	private String _starttime = null;
	private String _endtime = null;
	
	/**
	 * @return the _summary
	 */
	public String getSummary() { return _summary; }

	/**
	 * @return the _description
	 */
	public String getDescription() { return _description; }

	/**
	 * @return the _location
	 */
	public String getLocation() { return _location; }

	/**
	 * @return the _category
	 */
	public String getCategory() { return _category; }

	/**
	 * @return the _starttime
	 */
	public String getStarttime() { return _starttime; }

	/**
	 * @return the _endtime
	 */
	public String getEndtime() { return _endtime; }

	public PresenceCalendar(String summary, 
							String description, 
							String location, 
							String category,
							String starttime,
							String endtime)
	{
		this._summary = summary;
		this._description = description;
		this._location = location;
		this._category = category;
		this._starttime = starttime;
		this._endtime = endtime;
	}
	
}
