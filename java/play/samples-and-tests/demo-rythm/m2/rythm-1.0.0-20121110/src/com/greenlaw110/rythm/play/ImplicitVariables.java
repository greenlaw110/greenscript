package com.greenlaw110.rythm.play;

import play.Play;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.Http;
import play.mvc.Scope;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 27/01/12
 * Time: 10:23 AM
 * To change this template use File | Settings | File Templates.
 */
class ImplicitVariables {
    static class Var {
        String name;
        String type;
        Var(String name, String type) {
            this.name = name;
            this.type = type;
        }
        String name() {
            return RythmPlugin.underscoreImplicitVariableName ? "_" + name : name;
        }
        protected Object evaluate() {
            Scope.RenderArgs args = Scope.RenderArgs.current();
            if (null == args) return null;
            return Scope.RenderArgs.current().get(name());
        }
    }

    static Var[] vars = {
            new Var("error", "play.data.validation.Error"),
            new Var("error_index", "int"),
            new Var("error_isLast", "boolean"),
            new Var("error_isFirst", "boolean"),
            new Var("error_parity", "java.lang.String"),
            new Var("flash", "play.mvc.Scope.Flash"),
            new Var("params", "play.mvc.Scope.Params"),
            new Var("request", "play.mvc.Http.Request"),
            new Var("session", "play.mvc.Scope.Session"),
            new Var("errors", "java.util.List<play.data.validation.Error>"),
            // -- the above render args set in controller method
            // -- the following render args set in groovy template, thus we need to provide evaluate method
            new Var("lang", "java.lang.String") {
                @Override
                protected Object evaluate() {
                    return Lang.get();
                }
            },
            new Var("messages", "play.i18n.Messages") {
                @Override
                protected Object evaluate() {
                    return new Messages();
                }
            },
            // use _play instead of play to avoid name conflicting why reference class in play.** package with full qualified name
            new Var("_play", "play.Play") {
                @Override
                protected Object evaluate() {
                    return new Play();
                }
            },
            new Var("_response_encoding", "java.lang.String") {
                @Override
                protected Object evaluate() {
                    Http.Response resp = Http.Response.current();
                    return null == resp ? "utf-8" : resp.encoding;
                }
            },
            new Var("_rythmPlugin", "com.greenlaw110.rythm.play.RythmPlugin") {
                @Override
                protected Object evaluate() {
                    return Play.plugin(RythmPlugin.class);
                }
            },
            new Var("_rythm", "com.greenlaw110.rythm.RythmEngine") {
                @Override
                protected Object evaluate() {
                    return RythmPlugin.engine;
                }
            },
            new Var("_renderArgs", "play.mvc.Scope.RenderArgs") {
                @Override
                protected Object evaluate() {
                    return Scope.RenderArgs.current();
                }
            }
    };

}
