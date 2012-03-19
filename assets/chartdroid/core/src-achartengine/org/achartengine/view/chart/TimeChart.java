/**
 * Copyright (C) 2009 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.achartengine.view.chart;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.achartengine.model.XYMultiSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * The time chart rendering class.
 */
public class TimeChart extends LineChart {
  /** The number of milliseconds in a day. */
  public static final long DAY_MS = 24 * 60 * 60 * 1000;


  /**
   * Builds a new time chart instance.
   * 
   * @param dataset the multiple series dataset
   * @param renderer the multiple series renderer
   */
  public TimeChart(XYMultiSeries dataset, XYMultipleSeriesRenderer renderer) {
    super(dataset, renderer);
  }

  /**
   * Returns the date format pattern to be used for formatting the X axis labels.
   * @return the date format pattern for the X axis labels
   */
  public String getDateFormat() {
    return getXFormat();
  }
  
  /**
   * Sets the date format pattern to be used for formatting the X axis labels.
   * @param format the date format pattern for the X axis labels.
   * If null, an appropriate default format will be used.
   */
  public void setDateFormat(String format) {
	  setXFormat(format);
  }
  
  /**
   * The graphical representation of the labels on the X axis.
   * @param xLabels the X labels values
   * @param xTextLabelLocations the X text label locations
   * @param canvas the canvas to paint to
   * @param paint the paint to be used for drawing
   * @param left the left value of the labels area
   * @param top the top value of the labels area
   * @param bottom the bottom value of the labels area
   * @param xPixelsPerUnit the amount of pixels per one unit in the chart labels
   * @param minX the minimum value on the X axis in the chart
   */
  @Override
  protected void drawXLabels(List<Double> xLabels, Double[] xTextLabelLocations, Canvas canvas, Paint paint, int left,
      int top, int bottom, double xPixelsPerUnit, double minX, float hash_mark_height, float max_text_height) {
    int length = xLabels.size();
    boolean showLabels = mRenderer.isShowLabels();

    DateFormat format = getDateFormat(xLabels.get(0), xLabels.get(length - 1));
    for (int i = 0; i < length; i++) {
      long label = Math.round(xLabels.get(i));
      float xLabel = (float) (left + xPixelsPerUnit * (label - minX));
      if (showLabels) {
        paint.setColor(mRenderer.getLabelsColor());
        canvas.drawLine(xLabel, bottom, xLabel, bottom + hash_mark_height, paint);
        drawText(canvas, format.format(new Date(label)), xLabel, bottom + hash_mark_height + max_text_height, paint, 0);
      }
      if (mRenderer.isShowGrid() && mRenderer.isShowGridVerticalLines()) {
        paint.setColor(GRID_COLOR);
        canvas.drawLine(xLabel, bottom, xLabel, top, paint);
      }
    }
  }

  /**
   * Returns the date format pattern to be used, based on the date range.
   * @param start the start date in milliseconds
   * @param end the end date in milliseconds
   * @return the date format
   */
  private DateFormat getDateFormat(double start, double end) {
    if (getDateFormat() != null) {
      SimpleDateFormat format = null;
      try {
        format = new SimpleDateFormat(getDateFormat());
        return format;
      } catch (Exception e) {
        // do nothing here
      }
    }
    DateFormat format = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);
    double diff = end - start;
    if (diff > DAY_MS && diff < 5 * DAY_MS) {
      format = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT);
    } else if (diff < DAY_MS) {
      format = SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM);
    }
    return format;
  }
}
