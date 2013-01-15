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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import android.content.Context;
import android.util.Log;

import com.clarionmedia.infinitum.activity.EventPublisher;
import com.clarionmedia.infinitum.activity.EventSubscriber;
import com.clarionmedia.infinitum.activity.LifecycleEvent;
import com.clarionmedia.infinitum.context.InfinitumContext;
import com.clarionmedia.infinitum.context.RestfulContext;
import com.clarionmedia.infinitum.context.impl.XmlApplicationContext;
import com.clarionmedia.infinitum.di.AbstractBeanDefinition;
import com.clarionmedia.infinitum.di.BeanDefinitionBuilder;
import com.clarionmedia.infinitum.di.BeanFactory;
import com.clarionmedia.infinitum.orm.Session;
import com.clarionmedia.infinitum.ui.context.InfinitumUiContext;

/**
 * 
 * @author Tyler Treat
 * @version 1.0 01/13/13
 * @since 1.0
 */
public class XmlInfinitumUiContext implements InfinitumUiContext {

	private XmlApplicationContext mParentContext;
	private List<InfinitumContext> mChildContexts;
	private Map<EventPublisher, Queue<DataEvent>> mDataEvents;

	/**
	 * Creates a new {@code XmlInfinitumUiContext} instance as a child of the
	 * given {@link XmlApplicationContext}.
	 * 
	 * @param parentContext
	 *            the parent of this context
	 */
	public XmlInfinitumUiContext(XmlApplicationContext parentContext) {
		mParentContext = parentContext;
		mChildContexts = new ArrayList<InfinitumContext>();
		parentContext.subscribeForEvents(this);
		mDataEvents = new HashMap<EventPublisher, Queue<DataEvent>>();
	}

	@Override
	public void postProcess(Context context) {
	}

	@Override
	public boolean isDebug() {
		return mParentContext.isDebug();
	}

	@Override
	public Context getAndroidContext() {
		return mParentContext.getAndroidContext();
	}

	@Override
	public BeanFactory getBeanFactory() {
		return mParentContext.getBeanFactory();
	}

	@Override
	public Object getBean(String name) {
		return mParentContext.getBean(name);
	}

	@Override
	public <T> T getBean(String name, Class<T> clazz) {
		return mParentContext.getBean(name, clazz);
	}

	@Override
	public boolean isComponentScanEnabled() {
		return mParentContext.isComponentScanEnabled();
	}

	@Override
	public List<InfinitumContext> getChildContexts() {
		return mChildContexts;
	}

	@Override
	public void addChildContext(InfinitumContext context) {
		mChildContexts.add(context);
	}

	@Override
	public InfinitumContext getParentContext() {
		return mParentContext;
	}

	@Override
	public <T extends InfinitumContext> T getChildContext(Class<T> contextType) {
		return mParentContext.getChildContext(contextType);
	}

	@Override
	public RestfulContext getRestContext() {
		return mParentContext.getRestContext();
	}

	@Override
	public List<AbstractBeanDefinition> getBeans(BeanDefinitionBuilder beanDefinitionBuilder) {
		return new ArrayList<AbstractBeanDefinition>(0);
	}

	@Override
	public void publishEvent(LifecycleEvent event) {
		mParentContext.publishEvent(event);
	}

	@Override
	public void onEventPublished(LifecycleEvent event) {
		Log.e(getClass().getSimpleName(), "Event: " + event.getEventPublisher() + " - " + event.getEventType());
	}

	@Override
	public void subscribeForEvents(EventSubscriber subscriber) {
		mParentContext.subscribeForEvents(subscriber);
	}

	@Override
	public Session getProxiedSession(Session session) {
		return (Session) new SessionProxy(getAndroidContext(), session).getProxy();
	}

	@Override
	public void putDataEvent(EventPublisher eventPublisher, DataEvent eventData) {
		if (mDataEvents.containsKey(eventPublisher)) {
			mDataEvents.get(eventPublisher).add(eventData);
		} else {
			Queue<DataEvent> eventQueue = new LinkedList<DataEvent>();
			eventQueue.add(eventData);
			mDataEvents.put(eventPublisher, eventQueue);
		}
	}

}
