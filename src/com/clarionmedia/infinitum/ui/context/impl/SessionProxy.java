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

import android.content.Context;
import android.util.Log;

import com.clarionmedia.infinitum.di.AbstractProxy;
import com.clarionmedia.infinitum.di.DexMakerProxy;
import com.clarionmedia.infinitum.orm.Session;

/**
 * 
 * @author Tyler Treat
 * @version 1.0 01/13/13
 * @since 1.0
 */
public class SessionProxy extends DexMakerProxy {
	
	public SessionProxy(Context context, Session session) {
		super(context, session);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Log.e(getClass().getSimpleName(), "Session method intercepted");
		// TODO Intercept data-modifying operations and notify subscribers on success
		// Maybe create an annotation to mark data-modifying methods in Session implementations?
		return method.invoke(mTarget, args);
	}

	@Override
	public AbstractProxy clone() {
		throw new UnsupportedOperationException();
	}

}
