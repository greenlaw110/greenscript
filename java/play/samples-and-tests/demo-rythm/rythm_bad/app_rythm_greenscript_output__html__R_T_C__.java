import play.templates.JavaExtensions;
import controllers.*;
import models.*;
import java.util.*;
import java.io.*;

public class app_rythm_greenscript_output__html__R_T_C__ extends com.greenlaw110.rythm.template.TagBase {

	@Override public java.lang.String getName() {
		return "greenscript.output_";
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
		if (type == null) {type=(String)_get("type");}
		if (sm == null) {sm=(com.greenscriptool.RenderSession)_get("sm");}
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

	protected String nameList=null; //line: 1
	protected Object output=null; //line: 1
	protected Boolean all=false; //line: 1
	protected Boolean deps=false; //line: 1
	protected String media=null; //line: 1
	protected String browser=null; //line: 1
	protected String id=null; //line: 1
	protected String charset=null; //line: 1
	protected String type=null; //line: 1
	protected com.greenscriptool.RenderSession sm=null; //line: 1
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
		if (null != args && args.containsKey("type")) this.type=(String)args.get("type");
		if (null != args && args.containsKey("sm")) this.sm=(com.greenscriptool.RenderSession)args.get("sm");
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
		if (_p < l) { Object v = args[_p++]; boolean isString = ("java.lang.String".equals("String") || "String".equals("String")); type = (String)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p < l) { Object v = args[_p++]; boolean isString = ("java.lang.String".equals("com.greenscriptool.RenderSession") || "String".equals("com.greenscriptool.RenderSession")); sm = (com.greenscriptool.RenderSession)(isString ? (null == v ? "" : v.toString()) : v); }
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
		if ("type".equals(name)) this.type=(String)arg;
		if ("sm".equals(name)) this.sm=(com.greenscriptool.RenderSession)arg;
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
		if (_p++ == pos) { Object v = arg; boolean isString = ("java.lang.String".equals("String") || "String".equals("String")); type = (String)(isString ? (null == v ? "" : v.toString()) : v); }
		if (_p++ == pos) { Object v = arg; boolean isString = ("java.lang.String".equals("com.greenscriptool.RenderSession") || "String".equals("com.greenscriptool.RenderSession")); sm = (com.greenscriptool.RenderSession)(isString ? (null == v ? "" : v.toString()) : v); }
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
		out().ensureCapacity(850);
 //line: 2
p(""); //line: 3
if (!sm.isDefault(browser)) { //line: 3
p("\n<!--[if "); //line: 4

try{pe(browser);} catch (RuntimeException e) {handleTemplateExecutionException(e);}  //line: 4
p("]>"); //line: 5
} //line: 5
p(""); //line: 6
if (_sizeOf(sm.output(nameList, deps, all, media, browser)) > 0) { //line: 6
com.greenlaw110.rythm.runtime.Each.INSTANCE.render(sm.output(nameList, deps, all, media, browser), new com.greenlaw110.rythm.runtime.Each.Looper<String>(app_rythm_greenscript_output__html__R_T_C__.this,301){ //line: 6
	public boolean render(final String  sUrl, final int  sUrl_size, final int  sUrl_index, final boolean  sUrl_isOdd, final String  sUrl_parity, final boolean  sUrl_isFirst, final boolean  sUrl_isLast, final String  sUrl_sep, final com.greenlaw110.rythm.runtime.Each.IBody.LoopUtils  sUrl_utils) {  //line: 6
if("css".equals(type)) { //line: 7
p("\n<link rel=\"stylesheet\" type=\"text/css\" media=\""); //line: 8

try{pe(media);} catch (RuntimeException e) {handleTemplateExecutionException(e);}  //line: 8
p("\" href=\""); //line: 8

try{pe(sUrl);} catch (RuntimeException e) {handleTemplateExecutionException(e);}  //line: 8
p("\"/>\n "); //line: 9
}else { //line: 9
p("\n<script type=\"text/javascript\" src=\""); //line: 10

try{pe(sUrl);} catch (RuntimeException e) {handleTemplateExecutionException(e);}  //line: 10
p("\" "); //line: 10

if (null != id && !"".equals(id)) { //line: 10
p(" id=\""); //line: 10

try{pe(id);} catch (RuntimeException e) {handleTemplateExecutionException(e);}  //line: 10
p("\" "); //line: 10
} //line: 10

if (null != charset && !"".equals(charset)) { //line: 10
p(" charset=\""); //line: 10

try{pe(charset);} catch (RuntimeException e) {handleTemplateExecutionException(e);}  //line: 10
p("\" "); //line: 10
} //line: 10
p("></script>\n "); //line: 11
} //line: 11
p(""); //line: 12

	 return true;
	}});
}
 //line: 12
p(""); //line: 13
if (!sm.isDefault(browser)) { //line: 13
p("\n<![endif]-->"); //line: 15
} //line: 15
p(""); //line: 16
 String inline = all ? sm.outputInline().trim() : null;  //line: 16
 //line: 17
if (null != inline && !"".equals(inline)) { //line: 17
p("  "); //line: 17
{ //line: 17
	com.greenlaw110.rythm.runtime.ITag.ParameterList _pl = null;  //line: 17
	_pl = new com.greenlaw110.rythm.runtime.ITag.ParameterList(); //line: 17
	_pl.add("",type); //line: 17
		_invokeTag("greenscript.openTag_", _pl, false); //line: 17
} //line: 17
p("  "); //line: 17
__ctx.pushEscape(com.greenlaw110.rythm.template.ITemplate.Escape.RAW); //line: 17

try{pe(inline);} catch (RuntimeException e) {handleTemplateExecutionException(e);}  //line: 17
__ctx.popEscape(); //line: 17
p("  "); //line: 17
{ //line: 17
	com.greenlaw110.rythm.runtime.ITag.ParameterList _pl = null;  //line: 17
	_pl = new com.greenlaw110.rythm.runtime.ITag.ParameterList(); //line: 17
	_pl.add("",type); //line: 17
		_invokeTag("greenscript.closeTag_", _pl, false); //line: 17
} //line: 17
p("  "); //line: 17
} //line: 17
p(""); //line: 18
 //line: 18

		return this;
	}

}
