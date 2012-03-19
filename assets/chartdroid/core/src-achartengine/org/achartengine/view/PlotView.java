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
package org.achartengine.view;

import org.achartengine.view.chart.AbstractChart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

/**
 * The view that encapsulates the graphical chart.
 */
public class PlotView extends View {
  /** The chart to be drawn. */
  private AbstractChart mChart;
  /** The view bounds. */
  private Rect mRect = new Rect();
  /** The user interface thread handler. */
  private Handler mHandler;

  /**
   * Creates a new graphical view.
   * 
   * @param context the context
   * @param chart the chart to be drawn
   */
  public PlotView(Context context, AttributeSet attributes) {
	    super(context, attributes);
	    
	    mHandler = new Handler();
	  }
  
  public PlotView(Context context, AbstractChart chart) {

	  super(context);

	    mHandler = new Handler();
	    setChart(chart);
  }
  

  public void setChart(AbstractChart chart) {

	    mChart = chart;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.getClipBounds(mRect);

    int width = mRect.width();
    int height = mRect.height();
    if (mChart != null)
    	mChart.draw(canvas, width, height);
  }

  /**
   * Schedule a user interface repaint.
   */
  public void repaint() {
    mHandler.post(new Runnable() {
      public void run() {
        invalidate();
      }
    });
  }
}