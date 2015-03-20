package com.greenlaw110.rythm.play.utils;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 29/01/12
 * Time: 11:49 AM
 * To change this template use File | Settings | File Templates.
 */

import play.Play;
import play.data.binding.Unbinder;
import play.exceptions.NoRouteFoundException;
import play.exceptions.PlayException;
import play.exceptions.UnexpectedException;
import play.libs.F;
import play.mvc.ActionInvoker;
import play.mvc.Http;
import play.mvc.Router;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * this file is copied from the the the following code is copied from the
 * play.templates
 * .GroovyTemplate.ExecutableTemplate.ActionBridge.invokeMethod(...); version 1.2 trunk.
 * <p/>
 * since the class is not public.
 * <p/>
 * Please check this with the original source code to find any updates to bring over.
 */
public class ActionBridge {

    boolean absolute = false;
    boolean escapeXML = false;

    public ActionBridge(boolean absolute) {
        this.absolute = absolute;
    }
    public ActionBridge(boolean absolute, boolean escapeXML) {
        this.absolute = absolute;
        this.escapeXML = escapeXML;
    }

    public Object _abs() {
        this.absolute = true;
        return this;
    }

    // cache the action method lookup result
    private static Map<String, F.Tuple<Method, String[]>> _m = new HashMap<String, F.Tuple<Method, String[]>>();
    /**
     * this is really to do the reverse url lookup
     *
     * @param actionString
     * @param param
     * @return
     */
    public Router.ActionDefinition invokeMethod(String actionString, Object param) {
        try {

            String action = actionString;
            if (actionString.indexOf(".") < 0) {
                // is it comes from a controller or mailer?
                Http.Request request = Http.Request.current();
                if (null != request) {
                    action = request.controller + "." + actionString;
                } else {
                    throw new IllegalArgumentException("Must attach mailer class name to action string in reverse url lookup");
                }
            }

            Map<String, Object> r = new HashMap<String, Object>();
            Method actionMethod = null;
            String[] names = null;
            F.Tuple<Method, String[]> v = null;
            if (Play.mode.isProd()) v = _m.get(action);
            if (null == v) {
                actionMethod = (Method) ActionInvoker.getActionMethod(action)[1];
                names = (String[]) actionMethod
                        .getDeclaringClass()
                        .getDeclaredField("$" + actionMethod.getName() + computeMethodHash(actionMethod.getParameterTypes())).get(null);
                if (Play.mode.isProd()) _m.put(action, new F.Tuple(actionMethod, names));
            } else {
                actionMethod = v._1;
                names = v._2;
            }
            while (param instanceof Object[]) {
                // too many parameters versus action, possibly a developer
                // error. we must warn him.
                Object[] oa = (Object[])param;
                if (names.length < oa.length) {
                    throw new NoRouteFoundException(action, null);
                }
                if (oa.length == 0) break;
                Annotation[] annos = actionMethod.getAnnotations();
                for (int i = 0; i < oa.length; i++) {
                    if (oa[i] instanceof Router.ActionDefinition && oa[i] != null) {
                        Unbinder.unBind(r, oa[i].toString(), i < names.length ? names[i] : "", annos);
                    } else if (isSimpleParam(actionMethod.getParameterTypes()[i])) {
                        if (oa[i] != null) {
                            Unbinder.unBind(r, oa[i].toString(), i < names.length ? names[i] : "", annos);
                        }
                    } else {
                        Unbinder.unBind(r, oa[i], i < names.length ? names[i] : "", annos);
                    }
                }
                break;
            }
            Router.ActionDefinition def = Router.reverse(action, r);
            if (absolute) {
                def.absolute();
            }

            if (escapeXML) {
                def.url = def.url.replace("&", "&amp;");
            }
            return def;
        } catch (Exception e) {
            if (e instanceof PlayException) {
                throw (PlayException) e;
            }

            throw new UnexpectedException(e);
        }
    }

    static boolean isSimpleParam(Class type) {
        return Number.class.isAssignableFrom(type) || type.equals(String.class) || type.isPrimitive();
    }

    /**
     * copied from LVEnhancer
     */
    public static Integer computeMethodHash(String[] parameters) {
        StringBuffer buffer = new StringBuffer();
        for (String param : parameters) {
            buffer.append(param);
        }
        Integer hash = buffer.toString().hashCode();
        if (hash < 0) {
            return -hash;
        }
        return hash;
    }

    public static Integer computeMethodHash(Class<?>[] parameters) {
        String[] names = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Class<?> param = parameters[i];
            names[i] = "";
            if (param.isArray()) {
                int level = 1;
                param = param.getComponentType();
                // Array of array
                while (param.isArray()) {
                    level++;
                    param = param.getComponentType();
                }
                names[i] = param.getName();
                for (int j = 0; j < level; j++) {
                    names[i] += "[]";
                }
            } else {
                names[i] = param.getName();
            }
        }
        return computeMethodHash(names);
    }

}
