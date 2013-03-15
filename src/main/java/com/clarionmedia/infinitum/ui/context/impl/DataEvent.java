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

import com.clarionmedia.infinitum.event.AbstractEvent;
import com.clarionmedia.infinitum.event.EventPublisher;
import com.clarionmedia.infinitum.event.impl.LifecycleEvent;
import com.clarionmedia.infinitum.orm.Session;

import java.util.Map;

/**
 * <p> Concrete implementation of {@link AbstractEvent} representing the notion of a "data event", which is the
 * creation, modification, or deletion of an entity. A {@code DataEvent} signals to the UI that it potentially needs to
 * be updated. For example, if an entity is deleted in {@code Activity A} and then {@code Activity B}, which contains a
 * list of entities, is resumed, the deleted entity should be removed from the list in {@code Activity B}. </p> <p>
 * {@code DataEvent}s are added to event queues that belong to each registered {@link EventPublisher}. When an {@code
 * Activity} or {@code Fragment} lifecycle event is published, such as {@code onCreate}, {@code onStart}, or {@code
 * onResume}, the {@code DataEvent}s in the queue will be consumed, possibly resulting in a UI update. </p>
 *
 * @author Tyler Treat
 * @version 1.0 01/14/13
 * @see LifecycleEvent
 * @since 1.0
 */
public class DataEvent extends AbstractEvent {

    /**
     * Indicates what kind of {@code DataEvent} occurred.
     */
    public static enum EventType {
        CREATED, DELETED, UPDATED
    }

    ;

    private EventType mType;

    /**
     * Creates a new {@code DataEvent} instance.
     *
     * @param session the {@link Session} that published the event
     * @param type    the {@code EventType} of the {@code DataEvent}
     * @param payload the payload containing the entities the {@code DataEvent} corresponds to
     */
    public DataEvent(Session session, EventType type, Map<String, Object> payload) {
        super(type.name(), session, payload);
        mType = type;
    }

    /**
     * Returns the {@code EventType} for this {@code DataEvent}.
     *
     * @return {@code DataEvent}
     */
    public EventType getType() {
        return mType;
    }

}
