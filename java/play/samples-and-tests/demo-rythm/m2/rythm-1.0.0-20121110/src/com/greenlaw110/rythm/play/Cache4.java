package com.greenlaw110.rythm.play;

import play.mvc.Http;
import play.mvc.results.Result;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Cache an action's result.
 *
 * <p>If a time is not specified, the results will be cached for 1 hour by default.
 *
 * <p>Example: <code>@CacheFor("1h")</code>
 *
 * <ul>Differences with play.cache.CacheFor</p>
 * <li>You can specify time using configuration name like <code>cron.xx.cache4</code></li>
 * <li>It will check <code>rythm.cache.prodOnly</code> configuration. If it's true, then cache not effect on dev mode</li>
 * <li>Will NOT run @Before and @After filters if result get from cache, however @Finally filters will still be executed</li>
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cache4 {
    /**
     * Cache time, See play.libs.Time
     *
     * In addition, it also understand time configuration start with "cron."
     *
     * A special value "forever" also accept
     *
     * @return
     */
    String value() default "1h";

    /**
     * Define the cache key. Leave it empty if you want system to generate key from request automatically
     *
     * @return
     */
    String id() default "";

    /**
     * Whether use session data to generate key
     *
     * @return
     */
    boolean useSessionData() default false;

    /**
     * Wrap action result so that system know whether it comes out from cache or a real execution result
     */
    public static class CacheResult extends Result {
        private Result cached;

        public CacheResult(Result result) {
            if (null == result) throw new NullPointerException();
            cached = result;
        }

        public Result getCached() {
            return cached;
        }

        @Override
        public void apply(Http.Request request, Http.Response response) {
            cached.apply(request, response);
        }
    }
}
