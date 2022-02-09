package com.l2jserver.gameserver.script;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

import static java.time.format.DateTimeFormatter.ofPattern;

public class DateRange {
  private static final List<DateTimeFormatter> PATTERNs =
      List.of(ofPattern("dd MM yyyy"), ofPattern("dd MMM yyyy"));
  private final LocalDateTime startDate;
  private final LocalDateTime endDate;

  private DateRange(LocalDateTime from, LocalDateTime to) {
    startDate = from;
    endDate = to;
  }

  public static DateRange parse(String dateRange) {
    String[] date = dateRange.split("-");
    return PATTERNs.stream()
        .map(p -> tryParse(p, date))
        .filter(Objects::nonNull)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Illegal time interval:" + dateRange));
  }

  private static DateRange tryParse(DateTimeFormatter pattern, String[] date) {
    try {
      var start = LocalDate.parse(date[0], pattern).atStartOfDay();
      var end = LocalDate.parse(date[1], pattern).plusDays(1).atStartOfDay().minusSeconds(1);
      return new DateRange(start, end);
    } catch (DateTimeParseException e) {
      return null;
    }
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
