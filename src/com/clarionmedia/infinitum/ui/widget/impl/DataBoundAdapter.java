/*
 * Copyright (C) 2012 Clarion Media, LLC
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.clarionmedia.infinitum.ui.widget.impl;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.clarionmedia.infinitum.orm.context.InfinitumOrmContext;
import com.clarionmedia.infinitum.orm.criteria.Criteria;

public abstract class DataBoundAdapter<T> extends ArrayAdapter<T> {
	
	private Criteria<T> mCriteria;
	
	public DataBoundAdapter(InfinitumOrmContext ormContext, int resource, int textViewResourceId, Criteria<T> criteria) {
		super(ormContext.getAndroidContext(), resource, textViewResourceId);
		mCriteria = criteria;
	}
	
	public abstract View getView(int position, View convertView, ViewGroup parent);
	
	public void setCriteria(Criteria<T> criteria) {
		mCriteria = criteria;
	}
	
	public void bind() {
		if (!mCriteria.getSession().isOpen())
			mCriteria.getSession().open();
		clear();
		for (T item : mCriteria.list())
			add(item);
		mCriteria.getSession().close();
		notifyDataSetChanged();
	}

}
