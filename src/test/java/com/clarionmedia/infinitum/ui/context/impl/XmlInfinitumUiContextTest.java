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

import android.content.Context;
import com.clarionmedia.infinitum.context.InfinitumContext;
import com.clarionmedia.infinitum.context.RestfulContext;
import com.clarionmedia.infinitum.context.impl.XmlApplicationContext;
import com.clarionmedia.infinitum.context.impl.XmlRestfulContext;
import com.clarionmedia.infinitum.di.BeanFactory;
import com.clarionmedia.infinitum.event.AbstractEvent;
import com.clarionmedia.infinitum.event.EventPublisher;
import com.clarionmedia.infinitum.event.EventSubscriber;
import com.clarionmedia.infinitum.ui.widget.DataBound;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class XmlInfinitumUiContextTest {

    @Mock
    private XmlApplicationContext mockContext;

    @Mock
    private Context mockAndroidContext;

    @Mock
    private BeanFactory mockBeanFactory;

    @Mock
    private XmlApplicationContext mockApplicationContext;

    @Mock
    private XmlRestfulContext mockRestContext;

    @Mock
    private AbstractEvent mockEvent;

    @Mock
    private EventSubscriber mockEventSubscriber;

    @Mock
    private DataBound mockDataBound;

    @Mock
    private EventPublisher mockEventPublisher;

    @Mock
    private DataEvent mockDataEvent;

    private XmlInfinitumUiContext uiContext;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        uiContext = new XmlInfinitumUiContext(mockContext);
    }

    @Test
    public void testIsDebug() {
        // Setup
        when(mockContext.isDebug()).thenReturn(true);

        // Run
        boolean actual = uiContext.isDebug();

        // Verify
        assertTrue("isDebug should have returned true", actual);
        verify(mockContext).isDebug();
    }

    @Test
    public void testGetAndroidContext() {
        // Setup
        when(mockContext.getAndroidContext()).thenReturn(mockAndroidContext);

        // Run
        Context actual = uiContext.getAndroidContext();

        // Verify
        assertEquals("Result from getAndroidContext should equal the expected value", mockAndroidContext, actual);
        verify(mockContext).getAndroidContext();
    }

    @Test
    public void testGetBeanFactory() {
        // Setup
        when(mockContext.getBeanFactory()).thenReturn(mockBeanFactory);

        // Run
        BeanFactory actual = uiContext.getBeanFactory();

        // Verify
        assertEquals("Result from getBeanFactory should equal the expected value", mockBeanFactory, actual);
        verify(mockContext).getBeanFactory();
    }

    @Test
    public void testGetBean() {
        // Setup
        String beanName = "fooBean";
        Object bean = new Object();
        when(mockContext.getBean(beanName)).thenReturn(bean);

        // Run
        Object actual = uiContext.getBean(beanName);

        // Verify
        assertEquals("Result from getBean should equal the expected value", bean, actual);
        verify(mockContext).getBean(beanName);
    }

    @Test
    public void testGetBean_generic() {
        // Setup
        String beanName = "fooBean";
        String bean = "bean";
        when(mockContext.getBean(beanName, String.class)).thenReturn(bean);

        // Run
        String actual = uiContext.getBean(beanName, String.class);

        // Verify
        assertEquals("getBean should have returned the expected value", bean, actual);
        verify(mockContext).getBean(beanName, String.class);
    }

    @Test
    public void testIsComponentScanEnabled() {
        // Setup
        when(mockContext.isComponentScanEnabled()).thenReturn(true);

        // Run
        boolean actual = uiContext.isComponentScanEnabled();

        // Verify
        assertTrue("isComponentScanEnabled should have returned true", actual);
        verify(mockContext).isComponentScanEnabled();
    }

    @Test
    public void testGetChildContext() {
        // Setup
        when(mockContext.getChildContext(XmlApplicationContext.class)).thenReturn(mockApplicationContext);

        // Run
        InfinitumContext actual = uiContext.getChildContext(XmlApplicationContext.class);

        // Verify
        assertEquals("getChildContext should have returned the expected value", mockApplicationContext, actual);
        verify(mockContext).getChildContext(XmlApplicationContext.class);
    }

    @Test
    public void testGetRestContext() {
        // Setup
        when(mockContext.getRestContext()).thenReturn(mockRestContext);

        // Run
        RestfulContext actual = uiContext.getRestContext();

        // Verify
        assertEquals("getRestContext should have returned the expected value", mockRestContext, actual);
        verify(mockContext).getRestContext();
    }

    @Test
    public void testPublishEvent() {
        // Run
        uiContext.publishEvent(mockEvent);

        // Verify
        verify(mockContext).publishEvent(mockEvent);
    }

    @Test
    public void testpublishDataEvent() {
        // Setup
        Map<EventPublisher, Set<DataBound>> dataBoundsMap = new HashMap<EventPublisher, Set<DataBound>>();
        Set<DataBound> dataBounds = new HashSet<DataBound>();
        dataBounds.add(mockDataBound);
        dataBoundsMap.put(mockEventPublisher, dataBounds);
        uiContext.setDataBounds(dataBoundsMap);
        when(mockContext.isComponentScanEnabled()).thenReturn(true);

        // Run
        uiContext.publishDataEvent(mockDataEvent);

        // Verify
        verify(mockDataBound).updateForEvent(mockDataEvent);
    }

    @Test
    public void testRegisterDataBound() {
        // Setup
        when(mockDataBound.getEventPublisher()).thenReturn(mockEventPublisher);

        // Run
        uiContext.registerDataBound(mockDataBound);

        // Verify
        Map<EventPublisher, Set<DataBound>> dataBounds = uiContext.getDataBounds();
        Set<DataBound> actual = dataBounds.get(mockEventPublisher);
        assertTrue("Expected value should be registered", actual.contains(mockDataBound));
    }

}
