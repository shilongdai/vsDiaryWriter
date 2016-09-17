package net.viperfish.journal2.swtGui;

import java.util.Calendar;
import java.util.Date;

public final class TimeUtils {
	private TimeUtils() {

	}

	public static Date truncDate(Date d) {
		Calendar cal = Calendar.getInstance(); // locale-specific
		cal.setTime(d);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
}
