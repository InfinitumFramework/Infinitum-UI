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
import com.clarionmedia.infinitum.exception.InfinitumRuntimeException;
import com.clarionmedia.infinitum.orm.context.InfinitumOrmContext;
import com.clarionmedia.infinitum.orm.criteria.Criteria;
import com.clarionmedia.infinitum.orm.persistence.PersistencePolicy;
import com.clarionmedia.infinitum.ui.context.InfinitumUiContext;
import com.clarionmedia.infinitum.ui.context.impl.DataEvent;
import com.clarionmedia.infinitum.ui.widget.DataBound;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

/**
 * <p>
 * Implementation of {@link android.widget.ArrayAdapter} where the data is loaded using the
 * provided {@link com.clarionmedia.infinitum.orm.criteria.Criteria} query.
 * </p>
 *
 * @author Tyler Treat
 * @version 1.0.3b 03/20/13
 * @since 1.0
 */
public abstract class DataBoundCriteriaArrayAdapter<T> extends ArrayAdapter<T> implements DataBound {

	private Criteria<T> mCriteria;
	private Class<T> mGenericType;
	private PersistencePolicy mPersistencePolicy;

	/**
	 * Creates a new {@code DataBoundArrayAdapter} instance.
	 *
	 * @param context
	 *            the {@link com.clarionmedia.infinitum.context.InfinitumContext}
	 * @param resource
	 *            the {@link android.view.View} layout ID
	 * @param textViewResourceId
	 *            the {@link android.widget.TextView} ID
	 * @param criteria
	 *            the {@link com.clarionmedia.infinitum.orm.criteria.Criteria} query to use for retrieving data
	 */
	@SuppressWarnings("unchecked")
	public DataBoundCriteriaArrayAdapter(InfinitumContext context, int resource, int textViewResourceId,
                                         Criteria<T> criteria) {
		super(context.getAndroidContext(), resource, textViewResourceId);
		mCriteria = criteria;
		ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
		mPersistencePolicy = context.getChildContext(InfinitumOrmContext.class).getPersistencePolicy();
		mGenericType = (Class<T>) type.getActualTypeArguments()[0];
        if (!mPersistencePolicy.isPersistent(mGenericType)) {
            throw new InfinitumRuntimeException("Generic must be a domain type");
        }
		context.getChildContext(InfinitumUiContext.class).registerDataBound(this);
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);

	public void update(T entity) {
		Serializable pk = mPersistencePolicy.getPrimaryKey(entity);
		for (int i = 0; i < getCount(); i++) {
			T item = getItem(i);
			if (pk.equals(mPersistencePolicy.getPrimaryKey(item))) {
				super.remove(item);
				super.insert(entity, i);
				break;
			}
		}
	}

	/**
	 * Sets the {@link com.clarionmedia.infinitum.orm.criteria.Criteria} query to use for binding data.
	 * 
	 * @param criteria
	 *            the {@code Criteria} query
	 */
	public void setCriteria(Criteria<T> criteria) {
		mCriteria = criteria;
	}

	@Override
	public void bind() {
		boolean close = false;
		if (!mCriteria.getSession().isOpen()) {
			mCriteria.getSession().open();
			close = true;
		}
		clear();
		for (T item : mCriteria.list())
			add(item);
		if (close)
		    mCriteria.getSession().close();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateForEvent(DataEvent dataEvent) {
		if (!mGenericType.isInstance(dataEvent.getPayloadValue("entity")))
			return;
		switch (dataEvent.getType()) {
		case CREATED:
			add((T) dataEvent.getPayloadValue("entity"));
			break;
		case DELETED:
			remove((T) dataEvent.getPayloadValue("entity"));
			break;
		case UPDATED:
			update((T) dataEvent.getPayloadValue("entity"));
			break;
		}
	}

}
