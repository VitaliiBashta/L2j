package com.l2jserver.gameserver.script;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class DateRange {
  private static final Logger _log = LogManager.getLogger(DateRange.class.getName());
  private static final DateTimeFormatter DEFAULT_PATTERN =
      DateTimeFormatter.ofPattern("dd MM yyyy", Locale.US);
  private final LocalDateTime startDate;
  private final LocalDateTime endDate;

  private DateRange(LocalDateTime from, LocalDateTime to) {
    startDate = from;
    endDate = to;
  }

  public static DateRange parse(String dateRange, String format) {
    var formatter = DateTimeFormatter.ofPattern(format, Locale.US);

    return parse(dateRange, formatter);
  }

  public static DateRange parse(String dateRange) {
    return parse(dateRange, DEFAULT_PATTERN);
  }

  public static DateRange parse(String dateRange, DateTimeFormatter formatter) {
    String[] date = dateRange.split("-");
    if (date.length == 2) {
      try {
        var start = LocalDateTime.parse(date[0], formatter);
        var end = LocalDateTime.parse(date[1], formatter);

        return new DateRange(start, end);
      } catch (DateTimeParseException e) {
        _log.warn("Invalid Date Format.", e);
      }
    }
    return new DateRange(null, null);
  }

  public boolean isValid() {
    return (startDate != null) && (endDate != null) && startDate.isBefore(endDate);
  }

  public boolean isWithinRange(LocalDateTime date) {
    return (date.isEqual(startDate) || date.isAfter(startDate)) //
        && (date.isEqual(endDate) || date.isBefore(endDate));
  }

  public LocalDateTime getEndDate() {
    return endDate;
  }

  public LocalDateTime getStartDate() {
    return startDate;
  }

  @Override
  public String toString() {
    return "DateRange: From: " + startDate + " To: " + endDate;
  }
}
