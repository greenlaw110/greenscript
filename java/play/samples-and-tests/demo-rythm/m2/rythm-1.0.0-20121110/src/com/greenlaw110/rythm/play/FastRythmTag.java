package com.greenlaw110.rythm.play;

import com.greenlaw110.rythm.template.JavaTagBase;
import play.templates.FastTags;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 29/01/12
 * Time: 7:27 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class FastRythmTag extends JavaTagBase {
    
    private String nameSpace;
    private String tagName;

    public FastRythmTag() {
        this(null, null);
    }
    
    private FastRythmTag(String nameSpace, String tagName) {
        if (null == nameSpace) {
            FastTags.Namespace ns = getClass().getAnnotation(FastTags.Namespace.class);
            if (null != ns) {
                nameSpace = ns.value();
            } else {
                Class<?> c = getClass();
                String fullName = c.getName();
                String simpleName = c.getSimpleName();
                // am i embedded class?
                Class<?> pc = c.getEnclosingClass();
                while (null != pc && null == ns) {
                    ns = pc.getAnnotation(FastTags.Namespace.class);
                }
                if (null != ns) {
                    nameSpace = ns.value();
                } else {
                    // use package and enclosure class name as name space
                    nameSpace = fullName.replaceAll(simpleName + "$", "");
                    nameSpace = nameSpace.replace('$', '.');
                }
            }
        }
        if (!"".equals(nameSpace) && !nameSpace.endsWith(".")) nameSpace = nameSpace + ".";

        if (null == tagName) {
            tagName = getClass().getSimpleName();
        }
        
        this.nameSpace = nameSpace;
        this.tagName = tagName;
    }

    @Override
    public String getName() {
        return nameSpace + tagName;
    }
}
