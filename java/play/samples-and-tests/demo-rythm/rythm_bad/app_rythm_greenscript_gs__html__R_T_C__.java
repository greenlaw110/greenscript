import play.modules.greenscript.GreenScriptPlugin;
import play.templates.JavaExtensions;
import controllers.*;
import models.*;
import java.util.*;
import java.io.*;

public class app_rythm_greenscript_gs__html__R_T_C__ extends com.greenlaw110.rythm.template.TagBase {

	@Override public java.lang.String getName() {
		return "greenscript.gs_";
	}

	@Override protected void setup() {
		if (nameList == null) {nameList=(String)_get("nameList");}
		if (output == null) {output=(Object)_get("output");}
		if (all == null) {all=(Boolean)_get("all");}
		if (deps == null) {deps=(Boolean)_get("deps");}
		if (media == null) {media=(String)_get("media");}
		if (browser == null) {browser=(String)_get("browser");}
		if (id == null) {id=(String)_get("id");}
		if (charset == null) {charset=(String)_get("charset");}
		if (priority == null) {priority=(Integer)_get("priority");}
		if (type == null) {type=(String)_get("type");}
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

	protected String nameList=null; //line: 2
	protected Object output=null; //line: 2
	protected Boolean all=false; //line: 2
	protected Boolean deps=false; //line: 2
	protected String media=null; //line: 2
	protected String browser=null; //line: 2
	protected String id=null; //line: 2
	protected String charset=null; //line: 2
	protected Integer priority=0; //line: 2
	protected String type=null; //line: 2
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
		if (null != args && args.containsKey("nameList")) this.nameList=(String)args.get("nameList");
		if (null != args && args.containsKey("output")) this.output=(Object)args.get("output");
		if (null != args && args.containsKey("all")) this.all=(Boolean)args.get("all");
		if (null != args && args.containsKey("deps")) this.deps=(Boolean)args.get("deps");
		if (null != args && args.containsKey("media")) this.media=(String)args.get("media");
		if (null != args && args.containsKey("browser")) this.browser=(String)args.get("browser");
		if (null != args && args.containsKey("id")) this.id=(String)args.get("id");
		if (null != args && args.containsKey("charset")) this.charset=(String)args.get("charset");
		if (null != args && args.containsKey("priority")) this.priority=(Integer)args.get("priority");
		if (null != args && args.containsKey("type")) this.type=(String)args.get("type");
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
		if (_p < l) { Object v = args[_p++]; boolean isString = ("java.lang.String".equals("String") || "String".equals("String")); nameList = (String)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p < l) { Object v = args[_p++]; boolean isString = ("java.lang.String".equals("Object") || "String".equals("Object")); output = (Object)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p < l) { Object v = args[_p++]; boolean isString = ("java.lang.String".equals("Boolean") || "String".equals("Boolean")); all = (Boolean)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p < l) { Object v = args[_p++]; boolean isString = ("java.lang.String".equals("Boolean") || "String".equals("Boolean")); deps = (Boolean)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p < l) { Object v = args[_p++]; boolean isString = ("java.lang.String".equals("String") || "String".equals("String")); media = (String)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p < l) { Object v = args[_p++]; boolean isString = ("java.lang.String".equals("String") || "String".equals("String")); browser = (String)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p < l) { Object v = args[_p++]; boolean isString = ("java.lang.String".equals("String") || "String".equals("String")); id = (String)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p < l) { Object v = args[_p++]; boolean isString = ("java.lang.String".equals("String") || "String".equals("String")); charset = (String)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p < l) { Object v = args[_p++]; boolean isString = ("java.lang.String".equals("Integer") || "String".equals("Integer")); priority = (Integer)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p < l) { Object v = args[_p++]; boolean isString = ("java.lang.String".equals("String") || "String".equals("String")); type = (String)(isString ? (null == v ? "" : v.toString()) : v); }
	}

	@SuppressWarnings("unchecked") @Override public void setRenderArg(String name, Object arg) {
		if ("nameList".equals(name)) this.nameList=(String)arg;
		if ("output".equals(name)) this.output=(Object)arg;
		if ("all".equals(name)) this.all=(Boolean)arg;
		if ("deps".equals(name)) this.deps=(Boolean)arg;
		if ("media".equals(name)) this.media=(String)arg;
		if ("browser".equals(name)) this.browser=(String)arg;
		if ("id".equals(name)) this.id=(String)arg;
		if ("charset".equals(name)) this.charset=(String)arg;
		if ("priority".equals(name)) this.priority=(Integer)arg;
		if ("type".equals(name)) this.type=(String)arg;
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
		if (_p++ == pos) { Object v = arg; boolean isString = ("java.lang.String".equals("String") || "String".equals("String")); nameList = (String)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p++ == pos) { Object v = arg; boolean isString = ("java.lang.String".equals("Object") || "String".equals("Object")); output = (Object)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p++ == pos) { Object v = arg; boolean isString = ("java.lang.String".equals("Boolean") || "String".equals("Boolean")); all = (Boolean)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p++ == pos) { Object v = arg; boolean isString = ("java.lang.String".equals("Boolean") || "String".equals("Boolean")); deps = (Boolean)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p++ == pos) { Object v = arg; boolean isString = ("java.lang.String".equals("String") || "String".equals("String")); media = (String)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p++ == pos) { Object v = arg; boolean isString = ("java.lang.String".equals("String") || "String".equals("String")); browser = (String)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p++ == pos) { Object v = arg; boolean isString = ("java.lang.String".equals("String") || "String".equals("String")); id = (String)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p++ == pos) { Object v = arg; boolean isString = ("java.lang.String".equals("String") || "String".equals("String")); charset = (String)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p++ == pos) { Object v = arg; boolean isString = ("java.lang.String".equals("Integer") || "String".equals("Integer")); priority = (Integer)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p++ == pos) { Object v = arg; boolean isString = ("java.lang.String".equals("String") || "String".equals("String")); type = (String)(isString ? (null == v ? "" : v.toString()) : v); }
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
		out().ensureCapacity(1801);
 //line: 3
    final com.greenscriptool.RenderSession sm = (com.greenscriptool.RenderSession)GreenScriptPlugin.session(type); //line: 4
    if (null != nameList) { //line: 5
        sm.declare(nameList, media, browser); //line: 6
    } //line: 7
    all = all || "all".equals(output); //line: 8
    deps = deps || "deps".equals(output); //line: 9
    boolean _output = all || deps || ((null != output) && ((output instanceof Boolean) ? (Boolean)output : !"false".equals(output.toString()))); //line: 10
 //line: 14
 //line: 14
p(""); //line: 15
if (_output) { //line: 15
p("\n "); //line: 16
if (all) { //line: 16
p("\n "); //line: 17
p("  "); //line: 18
{ //line: 18
	com.greenlaw110.rythm.runtime.ITag.ParameterList _pl = null;  //line: 18
	_pl = new com.greenlaw110.rythm.runtime.ITag.ParameterList(); //line: 18
	_pl.add("",nameList); //line: 18
	_pl.add("deps",deps); //line: 18
	_pl.add("all",all); //line: 18
	_pl.add("media",null); //line: 18
	_pl.add("browser",null); //line: 18
	_pl.add("id",id); //line: 18
	_pl.add("charset",charset); //line: 18
	_pl.add("type",type); //line: 18
	_pl.add("sm",sm); //line: 18
		_invokeTag("greenscript.output_", _pl, false); //line: 18
} //line: 18
p("\n "); //line: 19
p("  "); //line: 20
if (_sizeOf(sm.getMedias(null)) > 0) { //line: 20
com.greenlaw110.rythm.runtime.Each.INSTANCE.render(sm.getMedias(null), new com.greenlaw110.rythm.runtime.Each.Looper<String>(app_rythm_greenscript_gs__html__R_T_C__.this,144){ //line: 20
	public boolean render(final String  med, final int  med_size, final int  med_index, final boolean  med_isOdd, final String  med_parity, final boolean  med_isFirst, final boolean  med_isLast, final String  med_sep, final com.greenlaw110.rythm.runtime.Each.IBody.LoopUtils  med_utils) {  //line: 20
{ //line: 21
	com.greenlaw110.rythm.runtime.ITag.ParameterList _pl = null;  //line: 21
	_pl = new com.greenlaw110.rythm.runtime.ITag.ParameterList(); //line: 21
	_pl.add("",nameList); //line: 21
	_pl.add("deps",deps); //line: 21
	_pl.add("all",all); //line: 21
	_pl.add("media",med); //line: 21
	_pl.add("browser",null); //line: 21
	_pl.add("id",id); //line: 21
	_pl.add("charset",charset); //line: 21
	_pl.add("type",type); //line: 21
	_pl.add("sm",sm); //line: 21
		_invokeTag("greenscript.output_", _pl, false); //line: 21
} //line: 21
p("\n "); //line: 22

	 return true;
	}});
}
 //line: 22
p("\n "); //line: 24
p("  "); //line: 25
if (_sizeOf(sm.getBrowsers()) > 0) { //line: 24
com.greenlaw110.rythm.runtime.Each.INSTANCE.render(sm.getBrowsers(), new com.greenlaw110.rythm.runtime.Each.Looper<String>(app_rythm_greenscript_gs__html__R_T_C__.this,171){ //line: 24
	public boolean render(final String  bro, final int  bro_size, final int  bro_index, final boolean  bro_isOdd, final String  bro_parity, final boolean  bro_isFirst, final boolean  bro_isLast, final String  bro_sep, final com.greenlaw110.rythm.runtime.Each.IBody.LoopUtils  bro_utils) {  //line: 24
if (_sizeOf(sm.getMedias(bro)) > 0) { //line: 24
com.greenlaw110.rythm.runtime.Each.INSTANCE.render(sm.getMedias(bro), new com.greenlaw110.rythm.runtime.Each.Looper<String>(app_rythm_greenscript_gs__html__R_T_C__.this,133){ //line: 24
	public boolean render(final String  med, final int  med_size, final int  med_index, final boolean  med_isOdd, final String  med_parity, final boolean  med_isFirst, final boolean  med_isLast, final String  med_sep, final com.greenlaw110.rythm.runtime.Each.IBody.LoopUtils  med_utils) {  //line: 24
{ //line: 25
	com.greenlaw110.rythm.runtime.ITag.ParameterList _pl = null;  //line: 25
	_pl = new com.greenlaw110.rythm.runtime.ITag.ParameterList(); //line: 25
	_pl.add("",nameList); //line: 25
	_pl.add("deps",deps); //line: 25
	_pl.add("all",all); //line: 25
	_pl.add("media",med); //line: 25
	_pl.add("browser",bro); //line: 25
	_pl.add("id",id); //line: 25
	_pl.add("charset",charset); //line: 25
	_pl.add("type",type); //line: 25
	_pl.add("sm",sm); //line: 25
		_invokeTag("greenscript.output_", _pl, false); //line: 25
} //line: 25

	 return true;
	}});
}
 //line: 25

	 return true;
	}});
}
 //line: 25
p("\n "); //line: 26
}else { //line: 26
p("\n "); //line: 27
{ //line: 27
	com.greenlaw110.rythm.runtime.ITag.ParameterList _pl = null;  //line: 27
	_pl = new com.greenlaw110.rythm.runtime.ITag.ParameterList(); //line: 27
	_pl.add("",nameList); //line: 27
	_pl.add("deps",deps); //line: 27
	_pl.add("all",all); //line: 27
	_pl.add("media",media); //line: 27
	_pl.add("browser",browser); //line: 27
	_pl.add("id",id); //line: 27
	_pl.add("charset",charset); //line: 27
	_pl.add("type",type); //line: 27
	_pl.add("sm",sm); //line: 27
		_invokeTag("greenscript.output_", _pl, false); //line: 27
} //line: 27
p("\n "); //line: 28
} //line: 28
p(""); //line: 29
} //line: 29
p(""); //line: 31
if (null != _body) { //line: 32
p("\n "); //line: 33
if (_output) { //line: 33
p("\n "); //line: 34
{ //line: 34
	com.greenlaw110.rythm.runtime.ITag.ParameterList _pl = null;  //line: 34
	_pl = new com.greenlaw110.rythm.runtime.ITag.ParameterList(); //line: 34
	_pl.add("",type); //line: 34
		_invokeTag("greenscript.openTag_", _pl, false); //line: 34
} //line: 34
p("\n "); //line: 35
{ //line: 36
	com.greenlaw110.rythm.runtime.ITag.ParameterList _pl = null;  //line: 36
	_pTagBody(_pl, _out); //line: 36
} //line: 36
{ //line: 36
	com.greenlaw110.rythm.runtime.ITag.ParameterList _pl = null;  //line: 36
	_pl = new com.greenlaw110.rythm.runtime.ITag.ParameterList(); //line: 36
	_pl.add("",type); //line: 36
		_invokeTag("greenscript.closeTag_", _pl, false); //line: 36
} //line: 36
p("\n "); //line: 37
}else { //line: 37
 sm.declareInline(_body.render(), priority)  //line: 37
; //line: 37
} //line: 37
p(""); //line: 38
} //line: 38
p(""); //line: 39
 //line: 39

		return this;
	}

}
