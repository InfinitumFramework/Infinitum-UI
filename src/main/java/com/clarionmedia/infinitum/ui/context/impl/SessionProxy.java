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

import com.clarionmedia.infinitum.di.AbstractProxy;
import com.clarionmedia.infinitum.di.DexMakerProxy;
import com.clarionmedia.infinitum.event.EventPublisher;
import com.clarionmedia.infinitum.event.annotation.Event;
import com.clarionmedia.infinitum.orm.Session;
import com.clarionmedia.infinitum.ui.context.InfinitumUiContext;
import com.clarionmedia.infinitum.ui.context.impl.DataEvent.EventType;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Proxy for a {@link Session} which intercepts {@code Session} method
 * invocations. Successful "data-modifying" method invocations, such as
 * {@link Session#save(Object)}, {@link Session#update(Object)}, and
 * {@link Session#delete(Object)}, will add a {@link DataEvent} to the
 * {@link EventPublisher} queues, allowing them to react accordingly on
 * qualifying lifecycle events.
 * </p>
 * 
 * @author Tyler Treat
 * @version 1.0 01/13/13
 * @since 1.0
 */
public class SessionProxy extends DexMakerProxy {

	private InfinitumUiContext mContext;
	private Session mSession;

	/**
	 * Creates a new {@code SessionProxy} instance.
	 * 
	 * @param context
	 *            the {@link InfinitumUiContext}
	 * @param session
	 *            the {@link Session} to proxy
	 */
	public SessionProxy(InfinitumUiContext context, Session session) {
		super(context.getAndroidContext(), session);
		mContext = context;
		mSession = session;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result = method.invoke(mTarget, args);
		if (method.isAnnotationPresent(Event.class)) {
			DataEvent event = getDataEvent(method, args, result);
			if (event != null)
				mContext.publishDataEvent(event);
		}
		if (Session.class.isAssignableFrom(result.getClass()))
			return this.getProxy();
		return result;
	}

	@Override
	public AbstractProxy clone() {
		throw new UnsupportedOperationException();
	}

	private DataEvent getDataEvent(Method method, Object[] args, Object result) {
		DataEvent event = null;
		Object entity = args.length > 0 ? args[0] : null;
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("entity", entity);
		String eventName = method.getAnnotation(Event.class).value();
		if (eventName.equals("entitySaved") && (Long) result != -1)
			event = new DataEvent(mSession, EventType.CREATED, payload);
		if (eventName.equals("entityUpdated") && (Boolean) result)
			event = new DataEvent(mSession, EventType.UPDATED, payload);
		if (eventName.equals("entityDeleted") && (Boolean) result)
			event = new DataEvent(mSession, EventType.DELETED, payload);
		if (eventName.equals("entitySavedOrUpdated") && (Long) result != -1) {
			EventType type = (Long) result == 0 ? EventType.UPDATED : EventType.CREATED;
			event = new DataEvent(mSession, type, payload);
		}
		return event;
	}

}
