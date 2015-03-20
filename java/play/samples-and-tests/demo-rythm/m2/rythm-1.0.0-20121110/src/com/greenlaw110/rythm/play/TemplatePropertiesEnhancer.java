package com.greenlaw110.rythm.play;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.internal.compiler.TemplateClass;
import javassist.ClassPath;
import javassist.NotFoundException;
import play.classloading.enhancers.PropertiesEnhancer;
import play.utils.FastRuntimeException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 27/01/12
 * Time: 10:07 AM
 * To change this template use File | Settings | File Templates.
 */
class TemplatePropertiesEnhancer extends PropertiesEnhancer {
    TemplatePropertiesEnhancer() {
        super();
        //this.classPool.removeClassPath(new ApplicationClassesClasspath());
        this.classPool.insertClassPath(new ClassPath() {
            @Override
            public InputStream openClassfile(String className) throws NotFoundException {
                TemplateClass tc = RythmPlugin.engine.classes.getByClassName(className);
                if (null != tc) {
                    if (null != tc.enhancedByteCode) return new ByteArrayInputStream(tc.enhancedByteCode);
                    else throw new FastRuntimeException("Cannot find enhanced byte class for " + className);
                }
                else return null;
            }

            @Override
            public URL find(String className) {
                int pos = className.indexOf("$");
                if (pos > -1) {
                    className = className.substring(0, pos);
                }
                if (Rythm.engine.classes.getByClassName(className) != null) {
                    String cname = className.replace('.', '/') + ".class";
                    try {
                        // return new File(cname).toURL();
                        return new URL("file:/ApplicationClassesClasspath/" + cname);
                    } catch (MalformedURLException e) {
                    }
                }
                return null;
            }

            @Override
            public void close() {
            }
        });
    }
}
