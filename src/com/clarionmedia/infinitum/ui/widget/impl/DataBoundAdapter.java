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

import com.clarionmedia.infinitum.context.InfinitumContext;
import com.clarionmedia.infinitum.event.EventPublisher;
import com.clarionmedia.infinitum.orm.criteria.Criteria;
import com.clarionmedia.infinitum.ui.context.InfinitumUiContext;
import com.clarionmedia.infinitum.ui.widget.DataBound;

/**
 * <p>
 * Implementation of {@link ArrayAdapter} where the data is loaded using the
 * provided {@link Criteria} query.
 * </p>
 * 
 * @author Tyler Treat
 * @version 1.0 01/13/13
 * @since 1.0
 */
public abstract class DataBoundAdapter<T> extends ArrayAdapter<T> implements DataBound {

	private Criteria<T> mCriteria;
	private EventPublisher mEventPublisher;

	public DataBoundAdapter(InfinitumContext context, EventPublisher eventPublisher, int resource, int textViewResourceId,
			Criteria<T> criteria) {
		super(context.getAndroidContext(), resource, textViewResourceId);
		mCriteria = criteria;
		mEventPublisher = eventPublisher;
		context.getChildContext(InfinitumUiContext.class).registerDataBound(this);
	}

	public abstract View getView(int position, View convertView, ViewGroup parent);

	public void setCriteria(Criteria<T> criteria) {
		mCriteria = criteria;
	}

	@Override
	public void bind() {
		if (!mCriteria.getSession().isOpen())
			mCriteria.getSession().open();
		clear();
		for (T item : mCriteria.list())
			add(item);
		mCriteria.getSession().close();
		notifyDataSetChanged();
	}

	@Override
	public EventPublisher getEventPublisher() {
		return mEventPublisher;
	}

}
