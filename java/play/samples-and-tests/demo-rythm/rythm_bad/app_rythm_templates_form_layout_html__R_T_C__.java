import play.templates.JavaExtensions;
import controllers.*;
import models.*;
import java.util.*;
import java.io.*;

public class app_rythm_templates_form_layout_html__R_T_C__ extends app_rythm_main_html__R_T_C__ {//<extended_resource_key>/app/rythm/main.html</extended_resource_key>

	@Override public java.lang.String getName() {
		return "templates.form_layout";
	}

	@Override protected void setup() {
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

	@SuppressWarnings("unchecked") @Override public void setRenderArg(String name, Object arg) {
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
		out().ensureCapacity(472);
{ //line: 2
	com.greenlaw110.rythm.runtime.ITag.ParameterList _pl = null;  //line: 2
	_pl = new com.greenlaw110.rythm.runtime.ITag.ParameterList(); //line: 2
	_pl.add("","form livevalidation"); //line: 2
		_invokeTag("greenscript.css", _pl, false); //line: 2
} //line: 2
p("\n\n<h2>"); //line: 4

p(_getRenderProperty("formTitle", null));p("</h2>\n\n"); //line: 6
{ //line: 6
	com.greenlaw110.rythm.runtime.ITag.ParameterList _pl = null;  //line: 6
	_pl = new com.greenlaw110.rythm.runtime.ITag.ParameterList(); //line: 6
	_pl.add("","livevalidation"); //line: 6
		_invokeTag("greenscript.js", _pl, false); //line: 6
} //line: 6
p("\n"); //line: 7
{ //line: 7
	com.greenlaw110.rythm.runtime.ITag.ParameterList _pl = null;  //line: 7
		_invokeTag("greenscript.js", _pl,  new com.greenlaw110.rythm.runtime.ITag.Body(app_rythm_templates_form_layout_html__R_T_C__.this) { //line: 7
			@Override public void setProperty(String name, Object val) { //line: 7
				setRenderArg(name, val); //line: 7
			} //line: 7
			@Override public Object getProperty(String name) { //line: 7
				return getRenderArg(name);  //line: 7
			} //line: 7
			@Override protected void setBodyArgByName(String name, Object val) { //line: 7
			} //line: 7
			@Override protected void setBodyArgByPos(int pos, Object val) { //line: 7
			} //line: 7
			@Override protected void _call() { //line: 7
p("\nvar rules = new Hash();\nvar ruleById = function (id) "); //line: 9
p("{"); //line: 9
p("\n    if (!"); //line: 10
p("$"); //line: 10
p("(id)) return;\n    var r = rules.get(id);\n    if (!r) "); //line: 12
p("{"); //line: 12
p("\n        r = new LiveValidation(id, "); //line: 13
p("{"); //line: 13
p("onlyOnBlur: true"); //line: 13

p('}');
 //line: 13
p(");\n        rules.set(id, r);\n    "); //line: 15

p('}');
 //line: 15
p("\n    return r;\n"); //line: 17

p('}');
 //line: 17
p("\n"); //line: 18

		}
	});
} //line: 18
p("\n\n"); //line: 20
_pLayoutContent(); //line: 20
p("\n\n"); //line: 22
{ //line: 22
	com.greenlaw110.rythm.runtime.ITag.ParameterList _pl = null;  //line: 22
	_pl = new com.greenlaw110.rythm.runtime.ITag.ParameterList(); //line: 22
	_pl.add("","/public/javascripts/pMask"); //line: 22
		_invokeTag("greenscript.js", _pl, false); //line: 22
} //line: 22
p("\n"); //line: 23
{ //line: 23
	com.greenlaw110.rythm.runtime.ITag.ParameterList _pl = null;  //line: 23
	_pl = new com.greenlaw110.rythm.runtime.ITag.ParameterList(); //line: 23
	_pl.add("output",true); //line: 23
		_invokeTag("greenscript.js", _pl,  new com.greenlaw110.rythm.runtime.ITag.Body(app_rythm_templates_form_layout_html__R_T_C__.this) { //line: 23
			@Override public void setProperty(String name, Object val) { //line: 23
				setRenderArg(name, val); //line: 23
			} //line: 23
			@Override public Object getProperty(String name) { //line: 23
				return getRenderArg(name);  //line: 23
			} //line: 23
			@Override protected void setBodyArgByName(String name, Object val) { //line: 23
			} //line: 23
			@Override protected void setBodyArgByPos(int pos, Object val) { //line: 23
			} //line: 23
			@Override protected void _call() { //line: 23
p("\n    new pMask();\n"); //line: 25

		}
	});
} //line: 25
p("\n"); //line: 26

		return this;
	}

}
