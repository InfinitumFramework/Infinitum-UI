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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;

import com.clarionmedia.infinitum.context.InfinitumContext;
import com.clarionmedia.infinitum.context.RestfulContext;
import com.clarionmedia.infinitum.context.impl.XmlApplicationContext;
import com.clarionmedia.infinitum.di.AbstractBeanDefinition;
import com.clarionmedia.infinitum.di.BeanDefinitionBuilder;
import com.clarionmedia.infinitum.di.BeanFactory;
import com.clarionmedia.infinitum.event.AbstractEvent;
import com.clarionmedia.infinitum.event.EventPublisher;
import com.clarionmedia.infinitum.event.EventSubscriber;
import com.clarionmedia.infinitum.event.impl.LifecycleEvent;
import com.clarionmedia.infinitum.event.impl.LifecycleEvent.LifecycleHook;
import com.clarionmedia.infinitum.orm.Session;
import com.clarionmedia.infinitum.ui.context.InfinitumUiContext;
import com.clarionmedia.infinitum.ui.widget.DataBound;

/**
 * <p>
 * Implementation of {@link InfinitumUiContext} which is initialized through XML
 * as a child of an {@link XmlApplicationContext} instance.
 * </p>
 * 
 * @author Tyler Treat
 * @version 1.0 01/13/13
 * @since 1.0
 */
public class XmlInfinitumUiContext implements InfinitumUiContext {

	private XmlApplicationContext mParentContext;
	private List<InfinitumContext> mChildContexts;
	private Map<EventPublisher, Set<DataBound>> mDataBounds;

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
		mDataBounds = new HashMap<EventPublisher, Set<DataBound>>();
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
	public void publishEvent(AbstractEvent event) {
		mParentContext.publishEvent(event);
	}

	@Override
	public void onEventPublished(AbstractEvent event) {
		if (!(event instanceof LifecycleEvent))
			return;
		LifecycleEvent lifecycleEvent = (LifecycleEvent) event;
		if (lifecycleEvent.getLifecycleHook() == LifecycleHook.ON_DESTROY) {
			EventPublisher eventPublisher = lifecycleEvent.getPublisher();
			mDataBounds.remove(eventPublisher);
		}
	}

	@Override
	public void subscribeForEvents(EventSubscriber subscriber) {
		mParentContext.subscribeForEvents(subscriber);
	}

	@Override
	public Session getProxiedSession(Session session) {
		return (Session) new SessionProxy(this, session).getProxy();
	}

	@Override
	public void publishDataEvent(DataEvent dataEvent) {
		// TODO do this asynchronously?
		for (Set<DataBound> dataBounds : mDataBounds.values()) {
			for (DataBound dataBound : dataBounds) {
				dataBound.updateForEvent(dataEvent);
			}
		}
	}

	@Override
	public void registerDataBound(DataBound dataBound) {
		EventPublisher eventPublisher = dataBound.getEventPublisher();
		if (!mDataBounds.containsKey(eventPublisher)) {
			Set<DataBound> dataBounds = new HashSet<DataBound>();
			dataBounds.add(dataBound);
			mDataBounds.put(eventPublisher, dataBounds);
		} else {
			mDataBounds.get(eventPublisher).add(dataBound);
		}
	}

}
