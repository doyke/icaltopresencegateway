package edu.columbia.voip.ical;

public class NoCalendarEventsException extends Exception {

	private static final long serialVersionUID = -5277335152490925705L;

	public NoCalendarEventsException() {
		this("The user does not contain any calendar events");
	}

	public NoCalendarEventsException(String message) {
		super(message);
	}

	public NoCalendarEventsException(Throwable cause) {
		super(cause);
	}

	public NoCalendarEventsException(String message, Throwable cause) {
		super(message, cause);
	}

}
