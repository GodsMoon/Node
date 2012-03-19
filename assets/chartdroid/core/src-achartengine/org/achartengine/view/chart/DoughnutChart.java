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

import org.achartengine.model.CategoryMultiSeries;
import org.achartengine.renderer.DefaultRenderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;

/**
 * The doughnut chart rendering class.
 */
public class DoughnutChart extends AbstractChart {

	/** The series dataset. */
	private CategoryMultiSeries mDataset;

	/** The series renderer. */
	private DefaultRenderer mRenderer;


	public DefaultRenderer getRenderer() {
		return mRenderer;
	}

	public CategoryMultiSeries getDataset() {
		return mDataset;
	}

	/**
	 * Builds a new pie chart instance.
	 * 
	 * @param dataset the series dataset
	 * @param renderer the series renderer
	 */
	public DoughnutChart(CategoryMultiSeries dataset, DefaultRenderer renderer) {
		mDataset = dataset;
		mRenderer = renderer;
	}

	/**
	 * The graphical representation of the pie chart.
	 * 
	 * @param canvas the canvas to paint to
	 * @param x the top left x value of the view to draw to
	 * @param y the top left y value of the view to draw to
	 * @param width the width of the view to draw to
	 * @param height the height of the view to draw to
	 */
	@Override
	public void draw(Canvas canvas, int width, int height) {
		Paint paint = new Paint();
		paint.setAntiAlias(getAntiAliased());
		paint.setStyle(Style.FILL);
		paint.setTextSize(10);

		int left = 15;
		int top = 5;
		int right = width - 5;
		int bottom = height;
//		drawBackground(mRenderer, canvas, width, height, paint);

		int cLength = mDataset.getCategoriesCount();
		int mRadius = Math.min(Math.abs(right - left), Math.abs(bottom - top));
		double rCoef = 0.35;
		double decCoef = 0.2 / cLength;
		int radius = (int) (mRadius * rCoef);
		int centerX = (left + right) / 2;
		int centerY = (bottom + top) / 2;
		float shortRadius = radius * 0.9f;
		float longRadius = radius * 1.1f;
		String[] categories = new String[cLength];
		for (int category = 0; category < cLength; category++) {
			int sLength = mDataset.getItemCount(category);
			double total = 0;
			for (int i = 0; i < sLength; i++) {
				total += mDataset.getValues(category).get(i).doubleValue();
			}
			float currentAngle = 0;
			RectF oval = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
			for (int i = 0; i < sLength; i++) {
				paint.setColor(mRenderer.getSeriesRendererAt(i).getColor());
				float value = mDataset.getValues(category).get(i).floatValue();
				float angle = (float) (value / total * 360);
				canvas.drawArc(oval, currentAngle, angle, true, paint);


				String datum_label = mDataset.getTitles(category).get(i);
				if (mRenderer.isShowLabels() && datum_label != null) {
					paint.setColor(mRenderer.getLabelsColor());
					double rAngle = Math.toRadians(90 - (currentAngle + angle / 2));
					double sinValue = Math.sin(rAngle);
					double cosValue = Math.cos(rAngle);
					int x1 = Math.round(centerX + (float) (shortRadius * sinValue));
					int y1 = Math.round(centerY + (float) (shortRadius * cosValue));
					int x2 = Math.round(centerX + (float) (longRadius * sinValue));
					int y2 = Math.round(centerY + (float) (longRadius * cosValue));
					canvas.drawLine(x1, y1, x2, y2, paint);
					int extra = 10;
					paint.setTextAlign(Align.LEFT);
					if (x1 > x2) {
						extra = -extra;
						paint.setTextAlign(Align.RIGHT);
					}
					canvas.drawLine(x2, y2, x2 + extra, y2, paint);
					canvas.drawText(datum_label, x2 + extra, y2 + 5, paint);
				}
				currentAngle += angle;
			}
			radius -= (int) mRadius * decCoef;
			shortRadius -= mRadius * decCoef - 2;
			if (mRenderer.getBackgroundColor() != 0) {
				paint.setColor(mRenderer.getBackgroundColor());
			} else {
				paint.setColor(Color.WHITE);
			}
			paint.setStyle(Style.FILL);
			oval = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
			canvas.drawArc(oval, 0, 360, true, paint);
			radius -= 1;
			categories[category] = mDataset.getCategory(category);
		}
	}
}
