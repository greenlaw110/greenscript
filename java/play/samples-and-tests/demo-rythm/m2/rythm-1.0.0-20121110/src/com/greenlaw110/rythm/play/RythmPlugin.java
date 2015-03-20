package com.greenlaw110.rythm.play;

import com.greenlaw110.rythm.*;
import com.greenlaw110.rythm.cache.ICacheService;
import com.greenlaw110.rythm.internal.compiler.ClassReloadException;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.ILoggerFactory;
import com.greenlaw110.rythm.play.parsers.*;
import com.greenlaw110.rythm.play.utils.ActionInvokeProcessor;
import com.greenlaw110.rythm.play.utils.StaticRouteResolver;
import com.greenlaw110.rythm.play.utils.TemplateClassAppEnhancer;
import com.greenlaw110.rythm.runtime.ITag;
import com.greenlaw110.rythm.spi.*;
import com.greenlaw110.rythm.template.ITemplate;
import com.greenlaw110.rythm.template.TemplateBase;
import com.greenlaw110.rythm.utils.*;
import com.stevesoft.pat.Regex;
import org.apache.commons.io.FileUtils;
import play.Logger;
import play.Play;
import play.PlayPlugin;
import play.cache.Cache;
import play.classloading.ApplicationClasses;
import play.classloading.HotswapAgent;
import play.classloading.enhancers.ControllersEnhancer;
import play.exceptions.UnexpectedException;
import play.mvc.Http;
import play.mvc.Scope;
import play.mvc.results.NotFound;
import play.mvc.results.Redirect;
import play.mvc.results.RenderTemplate;
import play.mvc.results.Result;
import play.templates.RythmTagContext;
import play.templates.TagContext;
import play.templates.Template;
import play.vfs.VirtualFile;

import java.io.*;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class RythmPlugin extends PlayPlugin {
    public static final String VERSION = "1.0.0-20121110";
    public static final String R_VIEW_ROOT = "app/rythm";

    public static void info(String msg, Object... args) {
        Logger.info(msg_(msg, args));
    }

    public static void info(Throwable t, String msg, Object... args) {
        Logger.info(t, msg_(msg, args));
    }

    public static void debug(String msg, Object... args) {
        Logger.debug(msg_(msg, args));
    }

    public static void debug(Throwable t, String msg, Object... args) {
        Logger.debug(t, msg_(msg, args));
    }

    public static void trace(String msg, Object... args) {
        Logger.trace(msg_(msg, args));
    }

    public static void trace(Throwable t, String msg, Object... args) {
        Logger.warn(t, msg_(msg, args));
    }

    public static void warn(String msg, Object... args) {
        Logger.warn(msg_(msg, args));
    }

    public static void warn(Throwable t, String msg, Object... args) {
        Logger.warn(t, msg_(msg, args));
    }

    public static void error(String msg, Object... args) {
        Logger.error(msg_(msg, args));
    }

    public static void error(Throwable t, String msg, Object... args) {
        Logger.error(t, msg_(msg, args));
    }

    public static void fatal(String msg, Object... args) {
        Logger.fatal(msg_(msg, args));
    }

    public static void fatal(Throwable t, String msg, Object... args) {
        Logger.fatal(t, msg_(msg, args));
    }

    private static String msg_(String msg, Object... args) {
        return String.format("RythmPlugin-" + VERSION + "> %1$s",
                String.format(msg, args));
    }

    public static RythmEngine engine;

    public static boolean underscoreImplicitVariableName = false;
    public static boolean refreshOnRender = true;
    public static String templateRoot = R_VIEW_ROOT;
    //public static String templateRoot2 = R_VIEW_ROOT;
    //public static String tagRoot = "app/views/tags/rythm";

    public static List<ImplicitVariables.Var> implicitRenderArgs = new ArrayList<ImplicitVariables.Var>();

    public static void registerImplicitRenderArg(final String name, final String type) {
        implicitRenderArgs.add(new ImplicitVariables.Var(name, type) {
            @Override
            protected Object evaluate() {
                return Scope.RenderArgs.current().get(name());
            }
        });
    }

    public static void loadTemplatePaths() {
        for (VirtualFile mroot: Play.modules.values()) {
            VirtualFile mviews = mroot.child(R_VIEW_ROOT);
            if (mviews.exists()) {
                Play.templatesPath.add(0, mviews);
            }
        }
        VirtualFile rythm = VirtualFile.open(Play.applicationPath).child(R_VIEW_ROOT);
        if (rythm.exists()) {
            Play.templatesPath.add(0, rythm);
        }
    }

    private boolean loadingRoute = false;
    @Override
    public void onLoad() {
        loadTemplatePaths();
        StaticRouteResolver.loadStaticRoutes();
/* disable preload routes as it cause class load troubles */
//        // try to workaround play issue https://play.lighthouseapp.com/projects/57987-play-framework/tickets/1545-play-precompile-does-not-load-routes
//        if (Router.routes.isEmpty()) {
//            loadingRoute = true;
//            try {
//                Router.load(Play.ctxPath);
//            } catch (Exception e) {
//                warn("cannot load routes on rythm load: you have compilation error: %s", e.getMessage());
//            }
//            loadingRoute = false;
//        }
    }

    @Override
    public void onConfigurationRead() {
        if (null != engine && Play.mode.isProd()) return; // already configured

        Properties playConf = Play.configuration;

        // special configurations
        underscoreImplicitVariableName = Boolean.parseBoolean(playConf.getProperty("rythm.implicitVariable.underscore", "false"));
        refreshOnRender = Boolean.parseBoolean(playConf.getProperty("rythm.resource.refreshOnRender", "true"));

        Properties p = new Properties();

        // set default configurations
        // p.put("rythm.root", new File(Play.applicationPath, "app/views"));
        // p.put("rythm.tag.root", new File(Play.applicationPath, tagRoot));
        p.put("rythm.pluginVersion", VERSION);
        p.put("rythm.tag.autoscan", false); // we want to scan tag folder coz we have Virtual Filesystem
        p.put("rythm.classLoader.parent", Play.classloader);
        p.put("rythm.resource.refreshOnRender", "true");
        p.put("rythm.loadPreCompiled", Play.usePrecompiled);
        if (Play.usePrecompiled || Play.getFile("precompiled").exists()) {
            File preCompiledRoot = new File(Play.getFile("precompiled"), "rythm");
            if (!preCompiledRoot.exists()) preCompiledRoot.mkdirs();
            p.put("rythm.preCompiled.root", preCompiledRoot);
        }
        p.put("rythm.resource.loader", new VirtualFileTemplateResourceLoader());
        p.put("rythm.classLoader.byteCodeHelper", new IByteCodeHelper() {
            @Override
            public byte[] findByteCode(String typeName) {
                ApplicationClasses classBag = Play.classes;
                if (classBag.hasClass(typeName)) {
                    ApplicationClasses.ApplicationClass applicationClass = classBag.getApplicationClass(typeName);
                    return applicationClass.enhancedByteCode;
                } else {
                    return null;
                }
            }
        });
        p.put("rythm.logger.factory", new ILoggerFactory() {
            @Override
            public ILogger getLogger(Class<?> clazz) {
                return PlayRythmLogger.instance;
            }
        });
        p.put("rythm.enableJavaExtensions", true); // enable java extension by default

        // handle implicit render args
        p.put("rythm.implicitRenderArgProvider", new IImplicitRenderArgProvider() {
            @Override
            public Map<String, ?> getRenderArgDescriptions() {
                Map<String, Object> m = new HashMap<String, Object>();
                // App registered render args
                for (ImplicitVariables.Var var: implicitRenderArgs) {
                    m.put(var.name(), var.type);
                }
                // Play default render args
                for (ImplicitVariables.Var var: ImplicitVariables.vars) {
                    m.put(var.name(), var.type);
                }
                return m;
            }

            @Override
            public void setRenderArgs(ITemplate template) {
                Map<String, Object> m = new HashMap<String, Object>();
                // some system implicit render args are not set, so we need to set them here.
                for (ImplicitVariables.Var var: ImplicitVariables.vars) {
                    m.put(var.name(), var.evaluate());
                }
                // application render args should already be set in controller methods
                template.setRenderArgs(m);
            }

            @Override
            public List<String> getImplicitImportStatements() {
                return Arrays.asList(new String[]{"controllers.*", "models.*"});
            }
        });
        debug("Implicit render variables set up");

        p.put("rythm.cache.prodOnly", "true");
        p.put("rythm.cache.defaultTTL", 60 * 60);
        p.put("rythm.cache.service", new ICacheService() {
            private int defaultTTL = 60 * 60;
            @Override
            public void put(String key, Serializable value, int ttl) {
                Cache.cacheImpl.set(key, value, ttl);
            }

            @Override
            public void put(String key, Serializable value) {
                Cache.cacheImpl.set(key, value, defaultTTL);
            }

            @Override
            public Serializable remove(String key) {
                Object o = Cache.get(key);
                Cache.delete(key);
                return null == o ? null : (o instanceof Serializable ? (Serializable)o : o.toString());
            }

            @Override
            public Serializable get(String key) {
                Object o = Cache.get(key);
                return null == o ? null : (o instanceof Serializable ? (Serializable)o : o.toString());
            }

            @Override
            public boolean contains(String key) {
                Object o = Cache.get(key);
                return null != o;
            }

            @Override
            public void clean() {
                Cache.clear();
            }

            @Override
            public void setDefaultTTL(int ttl) {
                defaultTTL = ttl;
            }

            @Override
            public void shutdown() {
                // doing nothing as the resource is managed by Play cache service
            }
        });

        p.put("rythm.cache.durationParser", new IDurationParser() {
            @Override
            public int parseDuration(String s) {
                if (null == s) return RythmPlugin.engine.defaultTTL;
                String confDuration = play.Play.configuration.getProperty(s);
                if (null != confDuration) s = confDuration;
                if ("forever".equals(confDuration)) return -1;
                return IDurationParser.DEFAULT_PARSER.parseDuration(s);
            }
        });

        // set user configurations - coming from application.conf
        for (String key: playConf.stringPropertyNames()) {
            if (key.startsWith("rythm.")) {
                p.setProperty(key, playConf.getProperty(key));
            }
        }
        debug("User defined rythm properties configured");

        // set template root
        templateRoot = p.getProperty("rythm.root", templateRoot);
        p.put("rythm.root", new File(Play.applicationPath, templateRoot));
        if (Logger.isDebugEnabled()) debug("rythm template root set to: %s", p.get("rythm.root"));

//        // set tag root
//        tagRoot = p.getProperty("rythm.tag.root", tagRoot);
//        if (tagRoot.endsWith("/")) tagRoot = tagRoot.substring(0, tagRoot.length() - 1);
//        p.put("rythm.tag.root", new File(Play.applicationPath, tagRoot));
//        if (Logger.isDebugEnabled()) debug("rythm tag root set to %s", p.get("rythm.tag.root"));

        // set tmp dir
        debug("Play standalone play server? %s", Play.standalonePlayServer);
        boolean gae = !Play.standalonePlayServer
            || Boolean.valueOf(p.getProperty("rythm.gae", "false"))
            || Boolean.valueOf(p.getProperty("rythm.noFileWrite", "false"));
        if (!gae) {
            File tmpDir = new File(Play.tmpDir, "rythm");
            tmpDir.mkdirs();
            p.put("rythm.tmpDir", tmpDir);
            if (Logger.isDebugEnabled()) debug("rythm tmp dir set to %s", p.get("rythm.tmpDir"));
        } else {
            warn("GAE enabled");
            p.put("rythm.noFileWrite", true);
        }

        // always get "java.lang.UnsupportedOperationException: class redefinition failed: attempted to change the schema" exception
        // from the hotswapAgent
        boolean useHotswapAgent = Boolean.valueOf(playConf.getProperty("rythm.useHotswapAgent", "false"));
        if (useHotswapAgent) {
            p.put("rythm.classLoader.hotswapAgent", new IHotswapAgent() {
                @Override
                public void reload(ClassDefinition... definitions) throws UnmodifiableClassException, ClassNotFoundException {
                    HotswapAgent.reload(definitions);
                }
            });
        }

        p.put("rythm.mode", Play.mode.isDev() && Play.standalonePlayServer ? Rythm.Mode.dev : Rythm.Mode.prod);
        p.put("rythm.playHost", true);

        if (null == engine) {
            engine = new RythmEngine(p);
            engine.registerListener(new IRythmListener() {
                @Override
                public void onRender(ITemplate template) {
                    Map<String, Object> m = new HashMap<String, Object>();
                    for (ImplicitVariables.Var var : ImplicitVariables.vars) {
                        m.put(var.name(), var.evaluate());
                    }
                    template.setRenderArgs(m);
                }
            });
            engine.registerTemplateClassEnhancer(new ITemplateClassEnhancer() {
                @Override
                public byte[] enhance(String className, byte[] classBytes) throws  Exception {
                    if (engine.noFileWrite) return classBytes;
                    ApplicationClasses.ApplicationClass applicationClass = new ApplicationClasses.ApplicationClass();
                    applicationClass.javaByteCode = classBytes;
                    applicationClass.enhancedByteCode = classBytes;
                    File f = File.createTempFile("rythm_", className.contains("$") ? "$1" : "" + ".java", Play.tmpDir);
                    applicationClass.javaFile = VirtualFile.open(f);
                    try {
                        new TemplatePropertiesEnhancer().enhanceThisClass(applicationClass);
                    } catch (Exception e) {
                        error(e, "Error enhancing class: %s", className);
                    }
                    if (!f.delete()) f.deleteOnExit();
                    return applicationClass.enhancedByteCode;
                }

                @Override
                public String sourceCode() {
                    // add String _url(String) method to template class
                    TextBuilder b = new TextBuilder();
                    String url = "\n    protected String _url(String action, Object... params) {return _url(false, action, params);}" +
                        "\n   protected String _url(boolean isAbsolute, String action, Object... params) {" +
                        "\n       com.greenlaw110.rythm.internal.compiler.TemplateClass tc = getTemplateClass(true);" +
                        "\n       boolean escapeXML = (!tc.isStringTemplate() && tc.templateResource.getKey().toString().endsWith(\".xml\"));" +
                        "\n       return new com.greenlaw110.rythm.play.utils.ActionBridge(isAbsolute, escapeXML).invokeMethod(action, params).toString();" +
                        "\n   }\n";

                    String msg = "\n    protected String _msg(String key, Object ... params) {return play.i18n.Messages.get(key, params);}";
                    return msg + url + TemplateClassAppEnhancer.sourceCode();
                }

                @Override
                public boolean equals(Object obj) {
                    if (obj == this) return true;
                    if (obj instanceof ITemplateClassEnhancer) {
                        ITemplateClassEnhancer that = (ITemplateClassEnhancer)obj;

                    }
                    return false;
                }
            });
            engine.registerGlobalImportProvider(new IImportProvider() {
                @Override
                public List<String> imports() {
                    return Arrays.asList(TemplateClassAppEnhancer.imports().split("[,\n]+"));
                }
            });
            debug("Template class enhancer registered");
            //Rythm.engine.cacheService.shutdown();
            Rythm.engine = engine;
            engine.preCompiling = true;

            IParserFactory[] factories = {new AbsoluteUrlReverseLookupParser(), new UrlReverseLookupParser(),
                    new MessageLookupParser(), new GroovyVerbatimTagParser(), new ExitIfNoModuleParser()};
            engine.getExtensionManager().registerUserDefinedParsers(factories).registerUserDefinedParsers("simple_rythm", factories).registerTemplateExecutionExceptionHandler(new ITemplateExecutionExceptionHandler() {
                @Override
                public boolean handleTemplateExecutionException(Exception e, TemplateBase template) {
                    if (e instanceof Result) {
                        if (e instanceof RenderTemplate) {
                            template.p(((RenderTemplate) e).getContent());
                        } else {
                            Http.Response resp = new Http.Response();
                            resp.out = new ByteArrayOutputStream();
                            ((Result)e).apply(null, resp);
                            try {
                                template.p(resp.out.toString("utf-8"));
                            } catch (UnsupportedEncodingException e0) {
                                throw new UnexpectedException("utf-8 not supported?");
                            }
                        }
                        // allow next controller action call
                        ControllersEnhancer.ControllerInstrumentation.initActionCall();
                        resetActionCallFlag();
                        return true;
                    }
                    return false;
                }
            }).registerExpressionProcessor(new ActionInvokeProcessor()).registerTagInvoeListener(new ITagInvokeListener() {
                @Override
                public void onInvoke(ITag tag) {
                    RythmTagContext.enterTag(tag.getName());
                }
                @Override
                public void tagInvoked(ITag tag) {
                    RythmTagContext.exitTag();
                }
            });
            debug("Play specific parser registered");
        } else {
            engine.init(p);
        }

        FastTagBridge.registerFastTags(engine);
        registerJavaTags(engine);
        ActionTagBridge.registerActionTags(engine);
        if (engine.enableJavaExtensions()) {
            JavaExtensionBridge.registerPlayBuiltInJavaExtensions(engine);
            JavaExtensionBridge.registerAppJavaExtensions(engine);
        }

        RythmTemplateLoader.clear();
    }

    public static final String ACTION_CALL_FLAG_KEY = "__RYTHM_PLUGIN_ACTION_CALL_";

    public static void resetActionCallFlag() {
        Stack<Boolean> actionCalls = Scope.RenderArgs.current().get(ACTION_CALL_FLAG_KEY, Stack.class);
        if (null != actionCalls) {
            actionCalls.pop();
        }
    }

    public static void setActionCallFlag() {
        Scope.RenderArgs renderAargs = Scope.RenderArgs.current();
        Stack<Boolean> actionCalls = renderAargs.get(ACTION_CALL_FLAG_KEY, Stack.class);
        if (null == actionCalls) {
            actionCalls = new Stack<Boolean>();
            renderAargs.put(ACTION_CALL_FLAG_KEY, actionCalls);
        }
        actionCalls.push(true);
    }

    public static boolean isActionCall() {
        Scope.RenderArgs renderArgs = Scope.RenderArgs.current();
        if (null == renderArgs) {
            // calling from Mails?
            return false;
        }
        Stack<Boolean> actionCalls = Scope.RenderArgs.current().get(ACTION_CALL_FLAG_KEY, Stack.class);
        if (null == actionCalls || actionCalls.empty()) return false;
        return true;
    }

    @Override
    public void onApplicationStart() {
        if (engine.mode.isProd()) {
            // pre load template classes if they are not loaded yet
            VirtualFile vf = Play.getVirtualFile("app/rythm/welcome.html");
            String key = vf.relativePath().replaceFirst("\\{.*?\\}", "");
            if (!engine.classes.tmplIdx.containsKey(key)) RythmTemplateLoader.scanRythmFolder();
        }
        engine.preCompiling = false;
    }

    private void registerJavaTags(RythmEngine engine) {
        long l = System.currentTimeMillis();
        // -- register application java tags
        List<ApplicationClasses.ApplicationClass> classes = Play.classes.getAssignableClasses(FastRythmTag.class);
        for (ApplicationClasses.ApplicationClass ac: classes) {
            registerJavaTag(ac.javaClass, engine);
        }

        // -- register PlayRythm build-in tags
        Class<?>[] ca = FastRythmTags.class.getDeclaredClasses();
        for (Class<?> c: ca) {
            registerJavaTag(c, engine);
        }
        debug("%sms to register rythm java tags", System.currentTimeMillis() - l);
    }

    private void registerJavaTag(Class<?> jc, RythmEngine engine) {
        int flag = jc.getModifiers();
        if (Modifier.isAbstract(flag)) return;
        try {
            Constructor<?> c = jc.getConstructor(new Class[]{});
            c.setAccessible(true);
            FastRythmTag tag = (FastRythmTag)c.newInstance();
            engine.registerTag(tag);
        } catch (Exception e) {
            throw new UnexpectedException("Error initialize JavaTag: " + jc.getName(), e);
        }
    }

    public static final Template VOID_TEMPLATE = new Template() {
        @Override
        public void compile() {
            //
        }
        @Override
        protected String internalRender(Map<String, Object> args) {
            throw new UnexpectedException("It's not supposed to be called");
        }
    };

    @Override
    public Template loadTemplate(VirtualFile file) {
        if (loadingRoute) return null;
        if (null == engine) {
            // in prod mode this method is called in preCompile() when onConfigurationRead() has not been called yet
            onConfigurationRead();
        }
        //warn(">>>> %s", file.relativePath());
        return RythmTemplateLoader.loadTemplate(file);
    }

    @Override
    public void detectChange() {
        if (!refreshOnRender) engine.classLoader.detectChanges();
//        if (TemplateClassAppEnhancer.changed()) {
//            File f = new File(Play.tmpDir, "rythm");
//            if (f.exists() && f.isDirectory()) {
//                try {
//                    Iterator<File> itr = FileUtils.iterateFiles(f, new String[]{"rythm"}, true);
//                    while (itr.hasNext()) {
//                        File f0 = itr.next();
//                        f0.delete();
//                    }
//                } catch (Exception e) {
//                    error(e, "error clear rythm template class cache files");
//                    // just ignore
//                }
//            }
//            engine.restart(new ClassReloadException(""));
//            engine = null;
//            onConfigurationRead();
//            //TemplateClassAppEnhancer.sourceCode(); // reload the cache
//        }
    }

    @Override
    public void beforeActionInvocation(Method actionMethod) {
        TagContext.init();
        if (Play.mode.isDev() && Boolean.valueOf(Play.configuration.getProperty("rythm.cache.prodOnly", "true"))) {
            return;
        }
        Http.Request request = Http.Request.current();
        if ((request.method.equals("GET") || request.method.equals("HEAD")) && actionMethod.isAnnotationPresent(Cache4.class)) {
            Cache4 cache4 = actionMethod.getAnnotation(Cache4.class);
            String cacheKey = cache4.id();
            if (S.isEmpty(cacheKey)) {
                cacheKey = "rythm-urlcache:" + request.url + request.querystring;
                if (cache4.useSessionData()) {
                    cacheKey = cacheKey + Scope.Session.current().toString();
                }
            }
            request.args.put("rythm-urlcache-key", cacheKey);
            request.args.put("rythm-urlcache-actionMethod", actionMethod);
            Result result = (Result) play.cache.Cache.get(cacheKey);
            if (null == result) return;
            if (!(result instanceof Cache4.CacheResult)) {
                result = new Cache4.CacheResult(result);
            }
            throw result;
        }
    }

    @Override
    public void onActionInvocationResult(Result result) {
        if (result instanceof Cache4.CacheResult) {
            // it's already a cached result
            return;
        }
        if (result instanceof Redirect) {
            Redirect r = (Redirect)result;
            if (r.code != Http.StatusCode.MOVED) {
                // not permanent redirect, don't cache it
                return;
            }
        }
        if (result instanceof NotFound || result instanceof play.mvc.results.Error) {
            // might recover later, so don't cache it
            return;
        }
        Object o = Http.Request.current().args.get("rythm-urlcache-key");
        if (null == o) return;
        String cacheKey = o.toString();
        Method actionMethod = (Method)Http.Request.current().args.get("rythm-urlcache-actionMethod");
        String duration = actionMethod.getAnnotation(Cache4.class).value();
        if (S.isEmpty(duration)) duration = "1h";
        if (duration.startsWith("cron.")) {
            duration = Play.configuration.getProperty(duration, "1h");
        }
        if ("forever".equals(duration)) {
            duration = "99999d";
        }
        play.cache.Cache.set(cacheKey, new Cache4.CacheResult(result), duration);
    }

    public static void main(String[] args) {
        String s = "controllers.Tester.action1().cacheFor(\"1mn\").ad";
        Regex r = new Regex("cache(?@())$");
        if (r.search(s)) {
            System.out.println(r.stringMatched());
        }
    }

}
