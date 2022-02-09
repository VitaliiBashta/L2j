package com.l2jserver.gameserver.util;

import com.l2jserver.gameserver.model.PageResult;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

/** A class containing useful methods for constructing HTML */
public class HtmlUtil {
  private HtmlUtil() {}

  /**
   * Gets the HTML representation of HP gauge.
   *
   * @param width the width
   * @param current the current value
   * @param max the max value
   * @param displayAsPercentage if {@code true} the text in middle will be displayed as percent else
   *     it will be displayed as "current / max"
   * @return the HTML
   */
  public static String getHpGauge(int width, long current, long max, boolean displayAsPercentage) {
    return getGauge(
        width,
        current,
        max,
        displayAsPercentage,
        "L2UI_CT1.Gauges.Gauge_DF_Large_HP_bg_Center",
        "L2UI_CT1.Gauges.Gauge_DF_Large_HP_Center",
        17,
        -13);
  }

  /**
   * Gets the HTML representation of MP Warn gauge.
   *
   * @param width the width
   * @param current the current value
   * @param max the max value
   * @param displayAsPercentage if {@code true} the text in middle will be displayed as percent else
   *     it will be displayed as "current / max"
   * @return the HTML
   */
  public static String getMpGauge(int width, long current, long max, boolean displayAsPercentage) {
    return getGauge(
        width,
        current,
        max,
        displayAsPercentage,
        "L2UI_CT1.Gauges.Gauge_DF_Large_MP_bg_Center",
        "L2UI_CT1.Gauges.Gauge_DF_Large_MP_Center",
        17,
        -13);
  }

  public static <T> PageResult createPage(
      Collection<T> elements,
      int page,
      int elementsPerPage,
      Function<Integer, String> pagerFunction,
      Function<T, String> bodyFunction) {
    return createPage(
        elements, elements.size(), page, elementsPerPage, pagerFunction, bodyFunction);
  }

  public static <T> PageResult createPage(
      T[] elements,
      int page,
      int elementsPerPage,
      Function<Integer, String> pagerFunction,
      Function<T, String> bodyFunction) {
    return createPage(
        Arrays.asList(elements),
        elements.length,
        page,
        elementsPerPage,
        pagerFunction,
        bodyFunction);
  }

  private static <T> PageResult createPage(
      Iterable<T> elements,
      int size,
      int page,
      int elementsPerPage,
      Function<Integer, String> pagerFunction,
      Function<T, String> bodyFunction) {
    int pages = size / elementsPerPage;
    if ((elementsPerPage * pages) < size) {
      pages++;
    }

    final StringBuilder pagerTemplate = new StringBuilder();
    if (pages > 1) {
      int breakit = 0;
      for (int i = 0; i < pages; i++) {
        pagerTemplate.append(pagerFunction.apply(i));
        breakit++;

        if (breakit > 5) {
          pagerTemplate.append("</tr><tr>");
          breakit = 0;
        }
      }
    }

    if (page >= pages) {
      page = pages - 1;
    }

    int start = 0;
    if (page > 0) {
      start = elementsPerPage * page;
    }

    final StringBuilder sb = new StringBuilder();
    int i = 0;
    for (T element : elements) {
      if (i++ < start) {
        continue;
      }

      sb.append(bodyFunction.apply(element));

      if (i >= (elementsPerPage + start)) {
        break;
      }
    }
    return new PageResult(pages, pagerTemplate, sb);
  }

  /**
   * Gets the HTML representation of a gauge.
   *
   * @param width the width
   * @param current the current value
   * @param max the max value
   * @param displayAsPercentage if {@code true} the text in middle will be displayed as percent else
   *     it will be displayed as "current / max"
   * @param backgroundImage the background image
   * @param image the foreground image
   * @param imageHeight the image height
   * @param top the top adjustment
   * @return the HTML
   */
  private static String getGauge(
      int width,
      long current,
      long max,
      boolean displayAsPercentage,
      String backgroundImage,
      String image,
      long imageHeight,
      long top) {
    current = Math.min(current, max);
    final StringBuilder sb = new StringBuilder();
    StringUtil.append(
        sb,
        "<table width=",
        String.valueOf(width),
        " cellpadding=0 cellspacing=0><tr><td background=\"" + backgroundImage + "\">");
    StringUtil.append(
        sb,
        "<img src=\"" + image + "\" width=",
        String.valueOf((long) (((double) current / max) * width)),
        " height=",
        String.valueOf(imageHeight),
        ">");
    StringUtil.append(
        sb,
        "</td></tr><tr><td align=center><table cellpadding=0 cellspacing=",
        String.valueOf(top),
        "><tr><td>");
    if (displayAsPercentage) {
      StringUtil.append(
          sb,
          "<table cellpadding=0 cellspacing=2><tr><td>",
          String.format("%.2f%%", ((double) current / max) * 100),
          "</td></tr></table>");
    } else {
      final String tdWidth = String.valueOf((width - 10) / 2);
      StringUtil.append(
          sb,
          "<table cellpadding=0 cellspacing=0><tr><td width=" + tdWidth + " align=right>",
          String.valueOf(current),
          "</td>");
      StringUtil.append(
          sb,
          "<td width=10 align=center>/</td><td width=" + tdWidth + ">",
          String.valueOf(max),
          "</td></tr></table>");
    }
    StringUtil.append(sb, "</td></tr></table></td></tr></table>");
    return sb.toString();
  }
}
