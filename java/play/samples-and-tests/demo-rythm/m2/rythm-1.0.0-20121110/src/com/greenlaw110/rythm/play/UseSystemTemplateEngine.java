package com.greenlaw110.rythm.play;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a controller or a controller action method to use system (groovy) engine to render
 *
 * <p>This is deprecated since v0.9.5a. To use rythm engine, just create a rythm template file
 * to <code>app/rythm</code> directory. To use system (groovy) engine, remove the template file
 * from <code>app/rythm</code> directory</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Deprecated
public @interface UseSystemTemplateEngine {
}
