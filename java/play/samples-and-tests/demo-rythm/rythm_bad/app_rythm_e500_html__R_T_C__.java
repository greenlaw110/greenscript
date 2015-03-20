import play.utils.Utils;
import play.Play;
import play.exceptions.*;
import play.templates.JavaExtensions;
import controllers.*;
import models.*;
import java.util.*;
import java.io.*;

public class app_rythm_e500_html__R_T_C__ extends com.greenlaw110.rythm.template.TagBase {

	@Override public java.lang.String getName() {
		return "e500";
	}

	@Override protected void setup() {
		if (exception == null) {exception=(play.exceptions.PlayException)_get("exception");}
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

	protected play.exceptions.PlayException exception=null;
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
		if (null != args && args.containsKey("exception")) this.exception=(play.exceptions.PlayException)args.get("exception");
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
		if (_p < l) { Object v = args[_p++]; boolean isString = ("java.lang.String".equals("play.exceptions.PlayException") || "String".equals("play.exceptions.PlayException")); exception = (play.exceptions.PlayException)(isString ? (null == v ? "" : v.toString()) : v); }
	}

	@SuppressWarnings("unchecked") @Override public void setRenderArg(String name, Object arg) {
		if ("exception".equals(name)) this.exception=(play.exceptions.PlayException)arg;
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
		if (_p++ == pos) { Object v = arg; boolean isString = ("java.lang.String".equals("play.exceptions.PlayException") || "String".equals("play.exceptions.PlayException")); exception = (play.exceptions.PlayException)(isString ? (null == v ? "" : v.toString()) : v); }
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
		out().ensureCapacity(4865);
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
p("A31012;\n        padding: 20px 45px;\n        color: "); //line: 14
p("#"); //line: 14
p("fff;\n        text-shadow: 1px 1px 1px rgba(0,0,0,.3);\n        border-bottom: 1px solid "); //line: 16
p("#"); //line: 16
p("690000;\n        font-size: 28px;\n    "); //line: 18

p('}');
 //line: 18
p("\n    p"); //line: 19
p("#"); //line: 19
p("detail "); //line: 19
p("{"); //line: 19
p("\n        margin: 0;\n        padding: 15px 45px;\n        background: "); //line: 22
p("#"); //line: 22
p("F5A0A0;\n        border-top: 4px solid "); //line: 23
p("#"); //line: 23
p("D36D6D;\n        color: "); //line: 24
p("#"); //line: 24
p("730000;\n        text-shadow: 1px 1px 1px rgba(255,255,255,.3);\n        font-size: 14px;\n        border-bottom: 1px solid "); //line: 27
p("#"); //line: 27
p("BA7A7A;\n    "); //line: 28

p('}');
 //line: 28
p("\n    p"); //line: 29
p("#"); //line: 29
p("detail input "); //line: 29
p("{"); //line: 29
p("\n        background: -webkit-gradient(linear, 0% 0%, 0% 100%, from("); //line: 30
p("#"); //line: 30
p("AE1113), to("); //line: 30
p("#"); //line: 30
p("A31012));\n        border: 1px solid "); //line: 31
p("#"); //line: 31
p("790000;\n        padding: 3px 10px;\n        text-shadow: 1px 1px 0 rgba(0, 0, 0, .5);\n        color: white;\n        border-radius: 3px;\n        cursor: pointer;\n        font-family: Monaco, 'Lucida Console';\n        font-size: 12px;\n        margin: 0 10px;\n        display: inline-block;\n        position: relative;\n        top: -1px;\n    "); //line: 43

p('}');
 //line: 43
p("\n    h2 "); //line: 44
p("{"); //line: 44
p("\n        margin: 0;\n        padding: 5px 45px;\n        font-size: 12px;\n        background: "); //line: 48
p("#"); //line: 48
p("333;\n        color: "); //line: 49
p("#"); //line: 49
p("fff;\n        text-shadow: 1px 1px 1px rgba(0,0,0,.3);\n        border-top: 4px solid "); //line: 51
p("#"); //line: 51
p("2a2a2a;\n    "); //line: 52

p('}');
 //line: 52
p("\n    pre "); //line: 53
p("{"); //line: 53
p("\n        margin: 0;\n        border-bottom: 1px solid "); //line: 55
p("#"); //line: 55
p("DDD;\n        text-shadow: 1px 1px 1px rgba(255,255,255,.5);\n        position: relative;\n        font-size: 12px;\n        overflow: hidden;\n    "); //line: 60

p('}');
 //line: 60
p("\n    pre span.line "); //line: 61
p("{"); //line: 61
p("\n        text-align: right;\n        display: inline-block;\n        padding: 5px 5px;\n        width: 30px;\n        background: "); //line: 66
p("#"); //line: 66
p("D6D6D6;\n        color: "); //line: 67
p("#"); //line: 67
p("8B8B8B;\n        text-shadow: 1px 1px 1px rgba(255,255,255,.5);\n        font-weight: bold;\n    "); //line: 70

p('}');
 //line: 70
p("\n    pre span.code "); //line: 71
p("{"); //line: 71
p("\n        padding: 5px 5px;\n        position: absolute;\n        right: 0;\n        left: 40px;\n    "); //line: 76

p('}');
 //line: 76
p("\n    pre:first-child span.code "); //line: 77
p("{"); //line: 77
p("\n        border-top: 4px solid "); //line: 78
p("#"); //line: 78
p("CDCDCD;\n    "); //line: 79

p('}');
 //line: 79
p("\n    pre:first-child span.line "); //line: 80
p("{"); //line: 80
p("\n        border-top: 4px solid "); //line: 81
p("#"); //line: 81
p("B6B6B6;\n    "); //line: 82

p('}');
 //line: 82
p("\n    pre.error span.line "); //line: 83
p("{"); //line: 83
p("\n        background: "); //line: 84
p("#"); //line: 84
p("A31012;\n        color: "); //line: 85
p("#"); //line: 85
p("fff;\n        text-shadow: 1px 1px 1px rgba(0,0,0,.3);\n    "); //line: 87

p('}');
 //line: 87
p("\n    pre.error span.code "); //line: 88
p("{"); //line: 88
p("\n        font-weight: bold;\n    "); //line: 90

p('}');
 //line: 90
p("\n    pre.error "); //line: 91
p("{"); //line: 91
p("\n        color: "); //line: 92
p("#"); //line: 92
p("A31012;\n    "); //line: 93

p('}');
 //line: 93
p("\n    pre.error span.marker "); //line: 94
p("{"); //line: 94
p("\n        background: "); //line: 95
p("#"); //line: 95
p("A31012;\n        color: "); //line: 96
p("#"); //line: 96
p("fff;\n        text-shadow: 1px 1px 1px rgba(0,0,0,.3);\n    "); //line: 98

p('}');
 //line: 98
p("\n    "); //line: 99
p("#"); //line: 99
p("more "); //line: 99
p("{"); //line: 99
p("\n        padding: 8px;\n        font-size: 12px;\n    "); //line: 102

p('}');
 //line: 102
p("\n</style>\n\n"); //line: 105
if (exception instanceof play.exceptions.PlayException) { //line: 105
p("\n    <h1>"); //line: 106

try{pe(exception.getErrorTitle());} catch (RuntimeException e) {handleTemplateExecutionException(e);}  //line: 106
p("</h1>\n    "); //line: 107
if ("DEV".equals(Play.mode.name())) { //line: 107
p("\n        <p id=\"detail\">"); //line: 108

try{pe(com.greenlaw110.rythm.utils.S.raw(exception.getErrorDescription()));} catch (RuntimeException e) {handleTemplateExecutionException(e);}  //line: 108
p("</p>\n    "); //line: 109
}else if ("PROD".equals(Play.mode.name())) { //line: 109
p("\n        <p>Error details are not displayed when Play! is in PROD mode. Check server logs for detail.</p>\n    "); //line: 111
} //line: 111
p("\n\n"); //line: 113
if (exception.isSourceAvailable() && (null != exception.getLineNumber()) && "DEV".equals(Play.mode.name())) { //line: 113
p("\n    <h2>In "); //line: 114

try{pe(exception.getSourceFile());} catch (RuntimeException e) {handleTemplateExecutionException(e);}  //line: 114
p(" (around line "); //line: 114

try{pe(exception.getLineNumber());} catch (RuntimeException e) {handleTemplateExecutionException(e);}  //line: 114
p(")</h2>\n    <div>\n    "); //line: 116
p("    "); //line: 117
 //line: 117
        final List<String> source = (exception instanceof CacheException) ? ((CacheException)exception).getSource() : ((SourceAttachment)exception).getSource(); //line: 118
        int lineNumber = (exception instanceof CacheException) ? ((CacheException)exception).getLineNumber() : ((SourceAttachment)exception).getLineNumber(); //line: 119
        final int from = lineNumber - 5 >= 0 && lineNumber < source.size() ? lineNumber - 5 : 0; //line: 120
        final int to = lineNumber + 5  < source.size() ? lineNumber + 5 : source.size()-1; //line: 121
        final List<String> lines = new ArrayList(); //line: 122
        for (int i = from; i <= to; ++i) { //line: 123
            lines.add(source.get(i)); //line: 124
        } //line: 125
     //line: 126
; //line: 127
p("    "); //line: 127
com.greenlaw110.rythm.runtime.Each.INSTANCE.render(lines, new com.greenlaw110.rythm.runtime.Each.Looper<String>(app_rythm_e500_html__R_T_C__.this,313){ //line: 127
	public boolean render(final String  line, final int  line_size, final int  line_index, final boolean  line_isOdd, final String  line_parity, final boolean  line_isFirst, final boolean  line_isLast, final String  line_sep, final com.greenlaw110.rythm.runtime.Each.IBody.LoopUtils  line_utils) {  //line: 127
p("<pre "); //line: 128
if(exception.getLineNumber() == line_index+from) { //line: 128
p("class=\"error\""); //line: 128
} //line: 128
p("><span class=\"line\">"); //line: 128

try{pe((line_index+from));} catch (RuntimeException e) {handleTemplateExecutionException(e);}  //line: 128
p("</span><span class=\"code\">"); //line: 128
p("&"); //line: 128
p("nbsp;"); //line: 128

try{pe(com.greenlaw110.rythm.utils.S.raw(com.greenlaw110.rythm.utils.S.escape(line).toString().replace("&darr;", "<strong>&darr;</strong>").replace("\000", "<em>").replace("\001", "</em>")));} catch (RuntimeException e) {handleTemplateExecutionException(e);}  //line: 128
p("</span></pre>\n    "); //line: 129

	 return true;
	}}); //line: 129
p("\n    </div>\n"); //line: 131
} //line: 131
p("\n\n	"); //line: 133
 String moreHtml = exception.getMoreHTML()  //line: 133
; //line: 134
p("	"); //line: 134
if (null != moreHtml) { //line: 134
p("\n		<div id=\"specific\" class=\"block\">\n			"); //line: 136

try{pe(moreHtml);} catch (RuntimeException e) {handleTemplateExecutionException(e);}  //line: 136
p("\n		</div>\n	"); //line: 138
} //line: 138
p("\n    <div id=\"more\" class=\"block\">\n        This exception has been logged with id <strong>"); //line: 140

try{pe(exception.getId());} catch (RuntimeException e) {handleTemplateExecutionException(e);}  //line: 140
p("</strong>\n    </div>\n"); //line: 142
}else { //line: 142
p("\n    <div id=\"header\" class=\"block\">\n        <h1>"); //line: 144

try{pe((null == exception.getMessage() ? "" : exception.getMessage()));} catch (RuntimeException e) {handleTemplateExecutionException(e);}  //line: 144
p("</h1>\n    </div>\n"); //line: 146
} //line: 146
p("\n"); //line: 147

		return this;
	}

}
