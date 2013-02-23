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

package com.clarionmedia.infinitum.ui.widget;

import com.clarionmedia.infinitum.event.EventPublisher;
import com.clarionmedia.infinitum.ui.context.impl.DataEvent;

/**
 * <p>
 * Exposes methods for binding data from a datastore.
 * </p>
 * 
 * @author Tyler Treat
 * @version 1.0 01/14/13
 * @since 1.0
 */
public interface DataBound {

	/**
	 * Binds the data source data to the widget. This will result in some sort
	 * of datastore query.
	 */
	void bind();

	/**
	 * Updates the {@code DataBound} structure according to the given
	 * {@link DataEvent}, which could be the creation, modification, or deletion
	 * of an entity.
	 * 
	 * @param dataEvent
	 *            the {@code DataEvent} to respond to
	 */
	void updateForEvent(DataEvent dataEvent);

	/**
	 * Returns the {@link EventPublisher} associated with this {@code DataBound}
	 * .
	 * 
	 * @return {@code EventPublisher}
	 */
	EventPublisher getEventPublisher();

}
