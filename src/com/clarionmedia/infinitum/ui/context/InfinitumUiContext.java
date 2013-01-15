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

package com.clarionmedia.infinitum.ui.context;

import com.clarionmedia.infinitum.context.InfinitumContext;
import com.clarionmedia.infinitum.di.BeanProvider;
import com.clarionmedia.infinitum.event.EventSubscriber;
import com.clarionmedia.infinitum.orm.Session;
import com.clarionmedia.infinitum.ui.context.impl.DataEvent;
import com.clarionmedia.infinitum.ui.widget.DataBound;

/**
 * 
 * @author Tyler Treat
 * @version 1.0 01/13/13
 * @since 1.0
 */
public interface InfinitumUiContext extends InfinitumContext, BeanProvider, EventSubscriber {
	
	Session getProxiedSession(Session session);
	
	void putDataEvent(DataEvent dataEvent);
	
	void registerDataBound(DataBound dataBound);

}
