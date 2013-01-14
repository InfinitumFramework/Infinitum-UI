package com.clarionmedia.infinitum.ui.context.impl;

import java.lang.reflect.Method;

import android.util.Log;

import com.clarionmedia.infinitum.di.AbstractProxy;
import com.clarionmedia.infinitum.di.JdkDynamicProxy;
import com.clarionmedia.infinitum.orm.Session;

public class SessionProxy extends JdkDynamicProxy {

	public SessionProxy(Session session) {
		super(session, new Class<?>[] { Session.class });
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Log.e(getClass().getSimpleName(), "Session method intercepted");
		return method.invoke(mTarget, args);
	}

	@Override
	public AbstractProxy clone() {
		throw new UnsupportedOperationException();
	}

}
