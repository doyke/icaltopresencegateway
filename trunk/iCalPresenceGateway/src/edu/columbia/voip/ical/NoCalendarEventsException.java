package edu.columbia.voip.ical;

public class NoCalendarEventsException extends Exception {

	private static final long serialVersionUID = -5277335152490925705L;

	public NoCalendarEventsException() {
		this("The user does not contain any calendar events");
	}

	public NoCalendarEventsException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public NoCalendarEventsException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public NoCalendarEventsException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
