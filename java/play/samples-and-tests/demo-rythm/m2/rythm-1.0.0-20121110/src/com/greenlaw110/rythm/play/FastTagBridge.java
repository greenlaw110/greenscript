package com.greenlaw110.rythm.play;

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.runtime.ITag;
import com.greenlaw110.rythm.template.JavaTagBase;
import com.greenlaw110.rythm.template.TemplateBase;
import groovy.lang.Closure;
import play.Play;
import play.classloading.ApplicationClasses;
import play.exceptions.UnexpectedException;
import play.templates.FastTags;
import play.templates.GroovyTemplate;
import play.templates.TagContext;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 28/01/12
 * Time: 6:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class FastTagBridge extends JavaTagBase {

    public static class RythmExecutableTemplate extends GroovyTemplate.ExecutableTemplate {

        public TemplateBase caller;
        RythmExecutableTemplate(TemplateBase caller) {
            this.caller = caller;
        }

        @Override
        public Object run() {
            return null;
        }
    }

    public static class TagBodyClosure extends Closure {
        private Body _body;
        private PrintWriter pw;
        public TagBodyClosure(Body body, PrintWriter pw) {
            super(null);
            _body = body;
            this.pw = pw;
        }

        @Override
        public Object call() {
            if (null == _body) return "";
            StringBuilder sb = new StringBuilder();
            _body.render(sb);
            String s = sb.toString();
            if (null != pw) pw.print(s);
            return s;
        }

        @Override
        public Object call(Object[] args) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object call(Object arguments) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setProperty(String property, Object newValue) {
            if ("out".equals(property)) {
                pw = (PrintWriter)newValue;
                return;
            }
            _body.setProperty(property, newValue);
        }

        @Override
        public Object getProperty(String property) {
            if ("out".equals(property)) {
                return pw;
            }
            return _body.getProperty(property);
        }
    }

    private String nameSpace;
    private String tagName;
    private Class<?> targetClass;
    private Method method;

    public FastTagBridge(String namespace, String tagName, Class<?> targetClass) {
        this.nameSpace = namespace;
        this.tagName = tagName;
        this.targetClass = targetClass;
        try {
            method = targetClass.getDeclaredMethod("_" + tagName, Map.class, Closure.class, PrintWriter.class, GroovyTemplate.ExecutableTemplate.class, int.class);
        } catch (NoSuchMethodException e) {
            throw new UnexpectedException("cannot find fast tag method to invoke:" + tagName, e);
        }
    }

    @Override
    public String getName() {
        return (null == nameSpace || "".equals(nameSpace)) ? tagName : nameSpace + "." + tagName;
    }

    @Override
    public void call(ParameterList params, Body body) {
        PrintWriter w = new PrintWriter(new Writer() {
            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
                out().append(cbuf, off, len);
            }

            @Override
            public void flush() throws IOException {
            }

            @Override
            public void close() throws IOException {
            }
        });
        Map<String, Object> paramMap = null == params ? new HashMap() : params.asMap();
        if (null != params) {
            Object defVal = params.getDefault();
            if (null != defVal && !paramMap.containsKey("arg")) {
                paramMap.put("arg", defVal);
            }
        }
        try {
            method.invoke(null, paramMap, null == body ? null : new TagBodyClosure(body, w), w, new RythmExecutableTemplate(caller()), 0);
        } catch (InvocationTargetException e) {
            throw new UnexpectedException("cannot invoke fast tag method: " + tagName, e);
        } catch (IllegalAccessException e) {
            throw new UnexpectedException("no access to fast tag method: " + tagName , e);
        }
    }

    @Override
    protected TemplateBase internalClone() {
        FastTagBridge bridge = new FastTagBridge(nameSpace, tagName, targetClass);
        return bridge;
    }

    public static void registerFastTags(RythmEngine engine) {
        long l = System.currentTimeMillis();
        Play.classloader.getAllClasses(); // ensure FastTags implementation loaded
        RythmPlugin.debug("%sms to load all application classes (in order to register FastTags)", System.currentTimeMillis() - l);
        l = System.currentTimeMillis();
        List<ApplicationClasses.ApplicationClass> classes = Play.classes.getAssignableClasses(FastTags.class);
        for (ApplicationClasses.ApplicationClass c: classes) {
            Class<?> jc = c.javaClass;
            String ns = null;
            play.templates.FastTags.Namespace a = jc.getAnnotation(play.templates.FastTags.Namespace.class);
            if (null != a) {
                ns = a.value().trim();
            }
            for (Method m: jc.getDeclaredMethods()) {
                int flag = m.getModifiers();
                if (!Modifier.isPublic(flag) || !Void.TYPE.equals(m.getReturnType())) continue;
                if (m.getParameterTypes().length != 5) continue;
                //note, need to strip off leading '_' from method name
                FastTagBridge tag = new FastTagBridge(ns, m.getName().substring(1), jc);
                ITag tag0 = engine.tags.get(tag.getName());
                // FastTagBridge has lowest priority, thus if there are other tags already registered
                // with the same name, FastTag bridge will not be registered again
                if (null == tag0 || (tag0 instanceof FastTagBridge)) engine.registerTag(tag);
            }
        }
        RythmPlugin.debug("%sms to register play fast tags", System.currentTimeMillis() - l);
    }
}
