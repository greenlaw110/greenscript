package com.greenlaw110.rythm.play;

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.template.JavaTagBase;
import play.Play;
import play.classloading.ApplicationClasses;
import play.classloading.enhancers.ControllersEnhancer;
import play.exceptions.UnexpectedException;
import play.mvc.*;
import play.mvc.results.RenderTemplate;
import play.mvc.results.Result;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Enable call controller action as tag
 */
public class ActionTagBridge extends JavaTagBase {

    private Method method = null;
    private int paramNumber;
    private String name;

    private ActionTagBridge(Class controller, Method action) {
        this(controller, action, false);
    }

    private ActionTagBridge(Class controller, Method action, boolean stripController) {
        name = controller.getName() + "." + action.getName();
        if (stripController) name = name.replaceFirst("controllers\\.", "");
        paramNumber = action.getParameterTypes().length;
        method = action;
    }

    @Override
    protected void call(ParameterList params, Body body) {
        Object[] oa = new Object[paramNumber];
        try {
            for (int i = 0; i < paramNumber; ++i) {
                oa[i] = params.get(i).value;
            }
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("Action call failed: parameter number does not match");
        }

        RythmPlugin.setActionCallFlag();
        Http.Request request = Http.Request.current();
        String oldAction = request.action;
        request.action = name.replaceFirst("controllers.", "");
        try {
            method.invoke(null, oa);
        } catch (IllegalAccessException e) {
            throw new UnexpectedException("Unknown error calling action method");
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof Result) {
                Result r = (Result) t;
                if (r instanceof RenderTemplate) {
                    p(((RenderTemplate) r).getContent());
                } else {
                    Http.Response resp = new Http.Response();
                    resp.out = new ByteArrayOutputStream();
                    ((Result) r).apply(null, resp);
                    try {
                        p(resp.out.toString("utf-8"));
                    } catch (UnsupportedEncodingException e0) {
                        throw new UnexpectedException("utf-8 not supported?");
                    }
                }
                ControllersEnhancer.ControllerInstrumentation.initActionCall();
                RythmPlugin.resetActionCallFlag();
            } else {
                throw new RuntimeException(t);
            }
        } finally {
            request.action = oldAction;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    private static boolean isActionMethod(Method method) {
        if (!Void.TYPE.equals(method.getReturnType())) {
            return false;
        }
        if (method.isAnnotationPresent(Before.class)) {
            return false;
        }
        if (method.isAnnotationPresent(After.class)) {
            return false;
        }
        if (method.isAnnotationPresent(Finally.class)) {
            return false;
        }
        if (method.isAnnotationPresent(Catch.class)) {
            return false;
        }
        if (method.isAnnotationPresent(Util.class)) {
            return false;
        }
        if (method.isAnnotationPresent(ControllersEnhancer.ByPass.class)) {
            return false;
        }
        return true;
    }

    public static void registerActionTags(RythmEngine engine) {
        long l = System.currentTimeMillis();
        Play.classloader.getAllClasses(); // ensure controller class loaded
        List<ApplicationClasses.ApplicationClass> classes = Play.classes.getAssignableClasses(Controller.class);
        for (ApplicationClasses.ApplicationClass appClass : classes) {
            Class cls = appClass.javaClass;
            for (Method method : cls.getMethods()) {
                if (!isActionMethod(method)) continue;
                ActionTagBridge atb = new ActionTagBridge(cls, method);
                engine.registerTag(atb);
                atb = new ActionTagBridge(cls, method, true);
                engine.registerTag(atb);
            }
        }
        if (Logger.isDebugEnabled()) {
            RythmPlugin.debug("%sms to register action tags", System.currentTimeMillis() - l);
        }
    }
}
