package thuonghth.utils;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * @author thuonghth
 */

public final class DateTimeUtil {

	private static final String ZONE_ID = "Asia/Ho_Chi_Minh";

	private static final String DATE_FORMAT = "yyyy-MM-dd";

	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd  [ hh:mm:ss ]";

	public static Date getCurrentDate() {
		return Date.valueOf(LocalDate.now(ZoneId.of(ZONE_ID)));
	}

	public static Date getDate(String dateStr) {
		return Date.valueOf(dateStr);
	}

	public static String getDateStr(Date date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
		return formatter.format(date.toLocalDate());
	}

	public static String getCurrentDateStr() {
		return getDateStr(getCurrentDate());
	}

	public static Timestamp getCurrentDateTime() {
		return Timestamp.valueOf(LocalDateTime.now(ZoneId.of(ZONE_ID)));
	}

	public static String getDateTimeStr(Timestamp dateTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
		return formatter.format(dateTime.toLocalDateTime());
	}

	public static boolean isValidDate(String dateStr) {
		try {
			LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(DATE_FORMAT));
		} catch (DateTimeParseException e) {
			return false;
		}
		return true;
	}

	public static boolean isValidDateInterval(String beginDateStr, String endDateStr) {
		return LocalDate.parse(beginDateStr).isEqual(LocalDate.parse(endDateStr))
				|| LocalDate.parse(beginDateStr).isBefore(LocalDate.parse(endDateStr));
	}

	public static boolean isAfterToday(String dateStr) {
		return !LocalDate.parse(dateStr).isBefore(LocalDate.now(ZoneId.of(ZONE_ID)));
	}
	
	public static boolean isFromAfterToday(String dateStr) {
		return LocalDate.parse(dateStr).isAfter(LocalDate.now(ZoneId.of(ZONE_ID)));
	}
	
	public static Date getNextPeriodDays(Date date, int numberOfDays) {
		return Date.valueOf(date.toLocalDate().plus(Period.ofDays(numberOfDays)));
	}

	public static Date getDateFromTimestamp(Timestamp date) {
		return new Date(date.getTime());
	}
}
