@__exitIfNoClass__(controllers.Secure)
@import controllers.Secure;
@args Object arg;
@if (session.get("username") != null) {
    Object o = Secure.Security.invoke("check", arg);
    if (((Boolean)o).booleanValue()) {
        @_body
    }
}
