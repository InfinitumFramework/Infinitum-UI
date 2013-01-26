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

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.clarionmedia.infinitum.context.InfinitumContext;
import com.clarionmedia.infinitum.event.EventPublisher;
import com.clarionmedia.infinitum.orm.context.InfinitumOrmContext;
import com.clarionmedia.infinitum.orm.criteria.Criteria;
import com.clarionmedia.infinitum.orm.persistence.PersistencePolicy;
import com.clarionmedia.infinitum.ui.context.InfinitumUiContext;
import com.clarionmedia.infinitum.ui.context.impl.DataEvent;
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
public abstract class DataBoundArrayAdapter<T> extends ArrayAdapter<T> implements DataBound {

	private Criteria<T> mCriteria;
	private EventPublisher mEventPublisher;
	private Class<T> mGenericType;
	private PersistencePolicy mPersistencePolicy;
	private LinkedHashMap<Serializable, T> mData;

	/**
	 * Creates a new {@code DataBoundArrayAdapter} instance.
	 * 
	 * @param context
	 *            the {@link InfinitumContext}
	 * @param eventPublisher
	 *            the {@link EventPublisher} this adapter belongs to
	 * @param resource
	 *            the {@link View} layout ID
	 * @param textViewResourceId
	 *            the {@link TextView} ID
	 * @param criteria
	 *            the {@link Criteria} query to use for retrieving data
	 */
	@SuppressWarnings("unchecked")
	public DataBoundArrayAdapter(InfinitumContext context, EventPublisher eventPublisher, int resource, int textViewResourceId,
			Criteria<T> criteria) {
		super(context.getAndroidContext(), resource, textViewResourceId);
		mCriteria = criteria;
		mEventPublisher = eventPublisher;
		context.getChildContext(InfinitumUiContext.class).registerDataBound(this);
		ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
		mGenericType = (Class<T>) type.getActualTypeArguments()[0];
		mPersistencePolicy = context.getChildContext(InfinitumOrmContext.class).getPersistencePolicy();
		mData = new LinkedHashMap<Serializable, T>();
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);
	
	@Override
	public void add(T entity) {
		Serializable pk = mPersistencePolicy.getPrimaryKey(entity);
		mData.put(pk, entity);
		super.add(entity);
	}
	
	@Override
	public void remove(T entity) {
		Serializable pk = mPersistencePolicy.getPrimaryKey(entity);
		mData.remove(pk);
		super.remove(entity);
	}
	
	public void update(T entity) {
		Serializable pk = mPersistencePolicy.getPrimaryKey(entity);
		for (int i = 0; i < getCount(); i++) {
			T item = getItem(i);
			if (pk.equals(mPersistencePolicy.getPrimaryKey(item))) {
				mData.put(pk, entity);
				super.remove(item);
				super.insert(entity, i);
			}
		}
	}

	/**
	 * Sets the {@link Criteria} query to use for binding data.
	 * 
	 * @param criteria
	 *            the {@code Criteria} query
	 */
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
	}

	@Override
	public EventPublisher getEventPublisher() {
		return mEventPublisher;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateForEvent(DataEvent dataEvent) {
		if (!mGenericType.isInstance(dataEvent.getPayload()))
			return;
		switch (dataEvent.getType()) {
		case CREATED:
			add((T) dataEvent.getPayload());
			break;
		case DELETED:
			remove((T) dataEvent.getPayload());
			break;
		case UPDATED:
			update((T) dataEvent.getPayload());
			break;
		}
	}

}
