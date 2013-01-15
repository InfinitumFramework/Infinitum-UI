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

package com.clarionmedia.infinitum.ui.context.impl;

import java.lang.reflect.Method;

import com.clarionmedia.infinitum.di.AbstractProxy;
import com.clarionmedia.infinitum.di.DexMakerProxy;
import com.clarionmedia.infinitum.event.InfinitumEvent;
import com.clarionmedia.infinitum.orm.Session;
import com.clarionmedia.infinitum.ui.context.InfinitumUiContext;
import com.clarionmedia.infinitum.ui.context.impl.DataEvent.EventType;

/**
 * 
 * @author Tyler Treat
 * @version 1.0 01/13/13
 * @since 1.0
 */
public class SessionProxy extends DexMakerProxy {

	private InfinitumUiContext mContext;

	public SessionProxy(InfinitumUiContext context, Session session) {
		super(context.getAndroidContext(), session);
		mContext = context;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result = method.invoke(mTarget, args);
		if (method.isAnnotationPresent(InfinitumEvent.class)) {
			DataEvent event = getDataEvent(method, args, result);
			if (event != null)
				mContext.putDataEvent(event);
		}
		return result;
	}

	@Override
	public AbstractProxy clone() {
		throw new UnsupportedOperationException();
	}

	private DataEvent getDataEvent(Method method, Object[] args, Object result) {
		DataEvent event = null;
		String eventName = method.getAnnotation(InfinitumEvent.class).value();
		if (eventName.equals("entitySaved") && (Long) result != -1)
			event = new DataEvent(EventType.CREATED, args);
		if (eventName.equals("entityUpdated") && (Boolean) result)
			event = new DataEvent(EventType.UPDATED, args);
		if (eventName.equals("entityDeleted") && (Boolean) result)
			event = new DataEvent(EventType.DELETED, args);
		if (eventName.equals("entitySavedOrUpdated") && (Long) result != -1)
			event = new DataEvent(EventType.UPDATED, args);
		if (eventName.equals("entitiesSavedOrUpdated") && (Long) result > 0)
			event = new DataEvent(EventType.UPDATED, args);
		if (eventName.equals("entitiesSaved") && (Long) result > 0)
			event = new DataEvent(EventType.CREATED, args);
		if (eventName.equals("entitiesDeleted") && (Long) result > 0)
			event = new DataEvent(EventType.DELETED, args);
		return event;
	}

}
