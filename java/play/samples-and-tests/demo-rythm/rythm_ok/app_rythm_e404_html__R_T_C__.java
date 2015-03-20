import play.mvc.Router;
import play.templates.JavaExtensions;
import controllers.*;
import models.*;
import java.util.*;
import java.io.*;

public class app_rythm_e404_html__R_T_C__ extends com.greenlaw110.rythm.template.TagBase {

	@Override public java.lang.String getName() {
		return "e404";
	}

	@Override protected void setup() {
		if (result == null) {result=(play.mvc.results.Result)_get("result");}
		if (flash == null) {flash=(play.mvc.Scope.Flash)_get("flash");}
		if (error_index == null) {error_index=(Integer)_get("error_index");}
		if (error == null) {error=(play.data.validation.Error)_get("error");}
		if (params == null) {params=(play.mvc.Scope.Params)_get("params");}
		if (lang == null) {lang=(java.lang.String)_get("lang");}
		if (messages == null) {messages=(play.i18n.Messages)_get("messages");}
		if (error_isFirst == null) {error_isFirst=(Boolean)_get("error_isFirst");}
		if (errors == null) {errors=(java.util.List<play.data.validation.Error>)_get("errors");}
		if (error_isLast == null) {error_isLast=(Boolean)_get("error_isLast");}
		if (session == null) {session=(play.mvc.Scope.Session)_get("session");}
		if (request == null) {request=(play.mvc.Http.Request)_get("request");}
		if (_rythmPlugin == null) {_rythmPlugin=(com.greenlaw110.rythm.play.RythmPlugin)_get("_rythmPlugin");}
		if (_response_encoding == null) {_response_encoding=(java.lang.String)_get("_response_encoding");}
		if (_renderArgs == null) {_renderArgs=(play.mvc.Scope.RenderArgs)_get("_renderArgs");}
		if (_rythm == null) {_rythm=(com.greenlaw110.rythm.RythmEngine)_get("_rythm");}
		if (error_parity == null) {error_parity=(java.lang.String)_get("error_parity");}
		if (_play == null) {_play=(play.Play)_get("_play");}
	}

	protected play.mvc.results.Result result=null;
	protected play.mvc.Scope.Flash flash=null;
	protected Integer error_index=0;
	protected play.data.validation.Error error=null;
	protected play.mvc.Scope.Params params=null;
	protected java.lang.String lang=null;
	protected play.i18n.Messages messages=null;
	protected Boolean error_isFirst=false;
	protected java.util.List<play.data.validation.Error> errors=null;
	protected Boolean error_isLast=false;
	protected play.mvc.Scope.Session session=null;
	protected play.mvc.Http.Request request=null;
	protected com.greenlaw110.rythm.play.RythmPlugin _rythmPlugin=null;
	protected java.lang.String _response_encoding=null;
	protected play.mvc.Scope.RenderArgs _renderArgs=null;
	protected com.greenlaw110.rythm.RythmEngine _rythm=null;
	protected java.lang.String error_parity=null;
	protected play.Play _play=null;

	@SuppressWarnings("unchecked") public void setRenderArgs(java.util.Map<String, Object> args) {
		if (null != args && args.containsKey("result")) this.result=(play.mvc.results.Result)args.get("result");
		if (null != args && args.containsKey("flash")) this.flash=(play.mvc.Scope.Flash)args.get("flash");
		if (null != args && args.containsKey("error_index")) this.error_index=(Integer)args.get("error_index");
		if (null != args && args.containsKey("error")) this.error=(play.data.validation.Error)args.get("error");
		if (null != args && args.containsKey("params")) this.params=(play.mvc.Scope.Params)args.get("params");
		if (null != args && args.containsKey("lang")) this.lang=(java.lang.String)args.get("lang");
		if (null != args && args.containsKey("messages")) this.messages=(play.i18n.Messages)args.get("messages");
		if (null != args && args.containsKey("error_isFirst")) this.error_isFirst=(Boolean)args.get("error_isFirst");
		if (null != args && args.containsKey("errors")) this.errors=(java.util.List<play.data.validation.Error>)args.get("errors");
		if (null != args && args.containsKey("error_isLast")) this.error_isLast=(Boolean)args.get("error_isLast");
		if (null != args && args.containsKey("session")) this.session=(play.mvc.Scope.Session)args.get("session");
		if (null != args && args.containsKey("request")) this.request=(play.mvc.Http.Request)args.get("request");
		if (null != args && args.containsKey("_rythmPlugin")) this._rythmPlugin=(com.greenlaw110.rythm.play.RythmPlugin)args.get("_rythmPlugin");
		if (null != args && args.containsKey("_response_encoding")) this._response_encoding=(java.lang.String)args.get("_response_encoding");
		if (null != args && args.containsKey("_renderArgs")) this._renderArgs=(play.mvc.Scope.RenderArgs)args.get("_renderArgs");
		if (null != args && args.containsKey("_rythm")) this._rythm=(com.greenlaw110.rythm.RythmEngine)args.get("_rythm");
		if (null != args && args.containsKey("error_parity")) this.error_parity=(java.lang.String)args.get("error_parity");
		if (null != args && args.containsKey("_play")) this._play=(play.Play)args.get("_play");
		super.setRenderArgs(args);
	}

	@SuppressWarnings("unchecked") public void setRenderArgs(Object... args) {
		int _p = 0, l = args.length;
		if (_p < l) { Object v = args[_p++]; boolean isString = ("java.lang.String".equals("play.mvc.results.Result") || "String".equals("play.mvc.results.Result")); result = (play.mvc.results.Result)(isString ? (null == v ? "" : v.toString()) : v); }
	}

	@SuppressWarnings("unchecked") @Override public void setRenderArg(String name, Object arg) {
		if ("result".equals(name)) this.result=(play.mvc.results.Result)arg;
		if ("flash".equals(name)) this.flash=(play.mvc.Scope.Flash)arg;
		if ("error_index".equals(name)) this.error_index=(Integer)arg;
		if ("error".equals(name)) this.error=(play.data.validation.Error)arg;
		if ("params".equals(name)) this.params=(play.mvc.Scope.Params)arg;
		if ("lang".equals(name)) this.lang=(java.lang.String)arg;
		if ("messages".equals(name)) this.messages=(play.i18n.Messages)arg;
		if ("error_isFirst".equals(name)) this.error_isFirst=(Boolean)arg;
		if ("errors".equals(name)) this.errors=(java.util.List<play.data.validation.Error>)arg;
		if ("error_isLast".equals(name)) this.error_isLast=(Boolean)arg;
		if ("session".equals(name)) this.session=(play.mvc.Scope.Session)arg;
		if ("request".equals(name)) this.request=(play.mvc.Http.Request)arg;
		if ("_rythmPlugin".equals(name)) this._rythmPlugin=(com.greenlaw110.rythm.play.RythmPlugin)arg;
		if ("_response_encoding".equals(name)) this._response_encoding=(java.lang.String)arg;
		if ("_renderArgs".equals(name)) this._renderArgs=(play.mvc.Scope.RenderArgs)arg;
		if ("_rythm".equals(name)) this._rythm=(com.greenlaw110.rythm.RythmEngine)arg;
		if ("error_parity".equals(name)) this.error_parity=(java.lang.String)arg;
		if ("_play".equals(name)) this._play=(play.Play)arg;
		super.setRenderArg(name, arg);
	}

	@SuppressWarnings("unchecked") public void setRenderArg(int pos, Object arg) {
		int _p = 0;
		if (_p++ == pos) { Object v = arg; boolean isString = ("java.lang.String".equals("play.mvc.results.Result") || "String".equals("play.mvc.results.Result")); result = (play.mvc.results.Result)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p++ == pos) { Object v = arg; boolean isString = ("java.lang.String".equals("play.mvc.Scope.Flash") || "String".equals("play.mvc.Scope.Flash")); flash = (play.mvc.Scope.Flash)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p++ == pos) { Object v = arg; boolean isString = ("java.lang.String".equals("Integer") || "String".equals("Integer")); error_index = (Integer)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p++ == pos) { Object v = arg; boolean isString = ("java.lang.String".equals("play.data.validation.Error") || "String".equals("play.data.validation.Error")); error = (play.data.validation.Error)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p++ == pos) { Object v = arg; boolean isString = ("java.lang.String".equals("play.mvc.Scope.Params") || "String".equals("play.mvc.Scope.Params")); params = (play.mvc.Scope.Params)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p++ == pos) { Object v = arg; boolean isString = ("java.lang.String".equals("java.lang.String") || "String".equals("java.lang.String")); lang = (java.lang.String)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p++ == pos) { Object v = arg; boolean isString = ("java.lang.String".equals("play.i18n.Messages") || "String".equals("play.i18n.Messages")); messages = (play.i18n.Messages)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p++ == pos) { Object v = arg; boolean isString = ("java.lang.String".equals("Boolean") || "String".equals("Boolean")); error_isFirst = (Boolean)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p++ == pos) { Object v = arg; boolean isString = ("java.lang.String".equals("java.util.List<play.data.validation.Error>") || "String".equals("java.util.List<play.data.validation.Error>")); errors = (java.util.List<play.data.validation.Error>)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p++ == pos) { Object v = arg; boolean isString = ("java.lang.String".equals("Boolean") || "String".equals("Boolean")); error_isLast = (Boolean)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p++ == pos) { Object v = arg; boolean isString = ("java.lang.String".equals("play.mvc.Scope.Session") || "String".equals("play.mvc.Scope.Session")); session = (play.mvc.Scope.Session)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p++ == pos) { Object v = arg; boolean isString = ("java.lang.String".equals("play.mvc.Http.Request") || "String".equals("play.mvc.Http.Request")); request = (play.mvc.Http.Request)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p++ == pos) { Object v = arg; boolean isString = ("java.lang.String".equals("com.greenlaw110.rythm.play.RythmPlugin") || "String".equals("com.greenlaw110.rythm.play.RythmPlugin")); _rythmPlugin = (com.greenlaw110.rythm.play.RythmPlugin)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p++ == pos) { Object v = arg; boolean isString = ("java.lang.String".equals("java.lang.String") || "String".equals("java.lang.String")); _response_encoding = (java.lang.String)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p++ == pos) { Object v = arg; boolean isString = ("java.lang.String".equals("play.mvc.Scope.RenderArgs") || "String".equals("play.mvc.Scope.RenderArgs")); _renderArgs = (play.mvc.Scope.RenderArgs)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p++ == pos) { Object v = arg; boolean isString = ("java.lang.String".equals("com.greenlaw110.rythm.RythmEngine") || "String".equals("com.greenlaw110.rythm.RythmEngine")); _rythm = (com.greenlaw110.rythm.RythmEngine)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p++ == pos) { Object v = arg; boolean isString = ("java.lang.String".equals("java.lang.String") || "String".equals("java.lang.String")); error_parity = (java.lang.String)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p++ == pos) { Object v = arg; boolean isString = ("java.lang.String".equals("play.Play") || "String".equals("play.Play")); _play = (play.Play)(isString ? (null == v ? "" : v.toString()) : v); }
		if(0 == pos) setRenderArg("arg", arg);
	}



    protected String _msg(String key, Object ... params) {return play.i18n.Messages.get(key, params);}
    protected String _url(String action, Object... params) {return _url(false, action, params);}
   protected String _url(boolean isAbsolute, String action, Object... params) {
       com.greenlaw110.rythm.internal.compiler.TemplateClass tc = getTemplateClass(true);
       boolean escapeXML = (!tc.isStringTemplate() && tc.templateResource.getKey().toString().endsWith(".xml"));
       return new com.greenlaw110.rythm.play.utils.ActionBridge(isAbsolute, escapeXML).invokeMethod(action, params).toString();
   }


	@Override public com.greenlaw110.rythm.utils.TextBuilder build(){
		out().ensureCapacity(2746);
p("<style type=\"text/css\">\n    html, body, pre "); //line: 4
p("{"); //line: 4
p("\n        margin: 0;\n        padding: 0;\n        font-family: Monaco, 'Lucida Console';\n        background: "); //line: 8
p("#"); //line: 8
p("ECECEC;\n    "); //line: 9

p('}');
 //line: 9
p("\n    h1 "); //line: 10
p("{"); //line: 10
p("\n        margin: 0;\n        background: "); //line: 12
p("#"); //line: 12
p("AD632A;\n        padding: 20px 45px;\n        color: "); //line: 14
p("#"); //line: 14
p("fff;\n        text-shadow: 1px 1px 1px rgba(0,0,0,.3);\n        border-bottom: 1px solid "); //line: 16
p("#"); //line: 16
p("9F5805;\n        font-size: 28px;\n    "); //line: 18

p('}');
 //line: 18
p("\n    p"); //line: 19
p("#"); //line: 19
p("detail "); //line: 19
p("{"); //line: 19
p("\n        margin: 0;\n        padding: 15px 45px;\n        background: "); //line: 22
p("#"); //line: 22
p("F6A960;\n        border-top: 4px solid "); //line: 23
p("#"); //line: 23
p("D29052;\n        color: "); //line: 24
p("#"); //line: 24
p("733512;\n        text-shadow: 1px 1px 1px rgba(255,255,255,.3);\n        font-size: 14px;\n        border-bottom: 1px solid "); //line: 27
p("#"); //line: 27
p("BA7F5B;\n    "); //line: 28

p('}');
 //line: 28
p("\n    h2 "); //line: 29
p("{"); //line: 29
p("\n        margin: 0;\n        padding: 5px 45px;\n        font-size: 12px;\n        background: "); //line: 33
p("#"); //line: 33
p("333;\n        color: "); //line: 34
p("#"); //line: 34
p("fff;\n        text-shadow: 1px 1px 1px rgba(0,0,0,.3);\n        border-top: 4px solid "); //line: 36
p("#"); //line: 36
p("2a2a2a;\n    "); //line: 37

p('}');
 //line: 37
p("\n    pre "); //line: 38
p("{"); //line: 38
p("\n        margin: 0;\n        border-bottom: 1px solid "); //line: 40
p("#"); //line: 40
p("DDD;\n        text-shadow: 1px 1px 1px rgba(255,255,255,.5);\n        position: relative;\n        font-size: 12px;\n        overflow: hidden;\n    "); //line: 45

p('}');
 //line: 45
p("\n    pre span.line "); //line: 46
p("{"); //line: 46
p("\n        text-align: right;\n        display: inline-block;\n        padding: 5px 5px;\n        width: 30px;\n        background: "); //line: 51
p("#"); //line: 51
p("D6D6D6;\n        color: "); //line: 52
p("#"); //line: 52
p("8B8B8B;\n        text-shadow: 1px 1px 1px rgba(255,255,255,.5);\n        font-weight: bold;\n    "); //line: 55

p('}');
 //line: 55
p("\n    pre span.route "); //line: 56
p("{"); //line: 56
p("\n        padding: 5px 5px;\n        position: absolute;\n        right: 0;\n        left: 40px;\n    "); //line: 61

p('}');
 //line: 61
p("\n    pre span.route span.verb "); //line: 62
p("{"); //line: 62
p("\n        display: inline-block;\n        width: 5%;\n        min-width: 50px;\n        overflow: hidden;\n        margin-right: 10px;\n    "); //line: 68

p('}');
 //line: 68
p("\n    pre span.route span.path "); //line: 69
p("{"); //line: 69
p("\n        display: inline-block;\n        width: 30%;\n        min-width: 200px;\n        overflow: hidden;\n        margin-right: 10px;\n    "); //line: 75

p('}');
 //line: 75
p("\n    pre span.route span.call "); //line: 76
p("{"); //line: 76
p("\n        display: inline-block;\n        width: 50%;\n        overflow: hidden;\n        margin-right: 10px;\n    "); //line: 81

p('}');
 //line: 81
p("\n    pre:first-child span.route "); //line: 82
p("{"); //line: 82
p("\n        border-top: 4px solid "); //line: 83
p("#"); //line: 83
p("CDCDCD;\n    "); //line: 84

p('}');
 //line: 84
p("\n    pre:first-child span.line "); //line: 85
p("{"); //line: 85
p("\n        border-top: 4px solid "); //line: 86
p("#"); //line: 86
p("B6B6B6;\n    "); //line: 87

p('}');
 //line: 87
p("\n    pre.error span.line "); //line: 88
p("{"); //line: 88
p("\n        background: "); //line: 89
p("#"); //line: 89
p("A31012;\n        color: "); //line: 90
p("#"); //line: 90
p("fff;\n        text-shadow: 1px 1px 1px rgba(0,0,0,.3);\n    "); //line: 92

p('}');
 //line: 92
p("\n</style>\n\n<h1>Action not found</h1>\n\n<p id=\"detail\">\n    For request '"); //line: 98

try{pe(play.mvc.Http.Request.current());} catch (RuntimeException e) {handleTemplateExecutionException(e);}  //line: 98
p("'\n</p>\n\n"); //line: 101
if (Router.routes != null) { //line: 101
p("\n    "); //line: 102
com.greenlaw110.rythm.runtime.Each.INSTANCE.render(Router.routes, new com.greenlaw110.rythm.runtime.Each.Looper<Router.Route>(app_rythm_e404_html__R_T_C__.this,197){ //line: 102
	public boolean render(final Router.Route  route, final int  route_size, final int  route_index, final boolean  route_isOdd, final String  route_parity, final boolean  route_isFirst, final boolean  route_isLast, final String  route_sep, final com.greenlaw110.rythm.runtime.Each.IBody.LoopUtils  route_utils) {  //line: 102
p("<pre><span class=\"line\">"); //line: 103

try{pe(route_index);} catch (RuntimeException e) {handleTemplateExecutionException(e);}  //line: 103
p("</span><span class=\"route\"><span class=\"verb\">"); //line: 103

try{pe(route.method);} catch (RuntimeException e) {handleTemplateExecutionException(e);}  //line: 103
p("</span><span class=\"path\">"); //line: 103

try{pe(route.path);} catch (RuntimeException e) {handleTemplateExecutionException(e);}  //line: 103
p("</span><span class=\"call\">"); //line: 103

try{pe(route.action);} catch (RuntimeException e) {handleTemplateExecutionException(e);}  //line: 103
p("</span></span></pre>\n    "); //line: 104

	 return true;
	}}); //line: 104
p("\n"); //line: 105
}else { //line: 105
p("\n    <h2>No router defined.</h2>\n"); //line: 107
} //line: 107
p("\n"); //line: 108

		return this;
	}

}
