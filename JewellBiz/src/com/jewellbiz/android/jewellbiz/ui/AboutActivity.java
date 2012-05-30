/*
 * Copyright (C) 2012 Jewell Biz Today, jewellbiz.com
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
package com.jewellbiz.android.jewellbiz.ui;

import com.jewellbiz.android.jewellbiz.R;
import com.jewellbiz.android.jewellbiz.R.id;
import com.jewellbiz.android.jewellbiz.R.layout;
import com.jewellbiz.android.jewellbiz.R.string;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        // Be sure to call the super class.
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.custom_dialog);
        
        TextView aboutTextView = (TextView)findViewById(R.id.custom_dialog_text_view);
        aboutTextView.setText(Html.fromHtml(getString(R.string.dialog_about_text_html)));
        
	}
	
	
}
