/*
 * Copyright (C) 2010 Karl Ostmo
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

package com.kostmo.commute.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.kostmo.commute.CalendarPickerConstants;

public class EventContentProvider extends ContentProvider {
	
	static final String TAG = "EventContentProvider";
	
	// This must be the same as what as specified as the Content Provider authority
	// in the manifest file.
	public static final String AUTHORITY = "com.kostmo.kanji.provider.events";

	static Uri BASE_URI = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(AUTHORITY).path("events").build();


   public static Uri constructUri(long data_id) {
       return ContentUris.withAppendedId(BASE_URI, data_id);
   }

   

	public static final String COLUMN_QUANTITY0 = "X";
	public static final String COLUMN_QUANTITY1 = "Y";
	
   
   
   @Override
   public boolean onCreate() {
       return true;
   }

   @Override
   public String getType(Uri uri) {
	   return CalendarPickerConstants.CalendarEventPicker.CONTENT_TYPE_CALENDAR_EVENT;
   }

   @Override
   public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

	   long calendar_id = ContentUris.parseId(uri);
	   
	   DatabaseCommutes database = new DatabaseCommutes(getContext());
	   return database.getCalendarPlayTimesGrouped(projection, selection, selectionArgs, sortOrder);
   }

   @Override
   public int delete(Uri uri, String s, String[] as) {
       throw new UnsupportedOperationException("Not supported by this provider");
   }

   @Override
   public Uri insert(Uri uri, ContentValues contentvalues) {
       throw new UnsupportedOperationException("Not supported by this provider");
   }
   
   @Override
   public int update(Uri uri, ContentValues contentvalues, String s, String[] as) {
       throw new UnsupportedOperationException("Not supported by this provider");
   }
}
