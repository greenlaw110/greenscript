@*
*{
 *  create a link element for a CSS file under /public/stylesheets
 *  src (required) filename without the leading path "/public/stylesheets"
 *  id (optional) an id attribute for the generated link tag
 *  media (optional) media : screen, print, aural, projection ...
 *  title (optional) title atttribute (or description)
 *    ${stylesheet src:'default.css' media:'screen,print' /}
}*
*@
@* groovy implementation
%{
    (_arg ) && (_src = _arg);

    if (!_src) {
        throw new play.exceptions.TagInternalException("src attribute cannot be empty for stylesheet tag");
    }
    _src = "/public/stylesheets/" + _src;
    try {
        _abs = play.mvc.Router.reverseWithCheck(_src, play.Play.getVirtualFile(_src), false);
    } catch (Exception ex) {
        throw new play.exceptions.TagInternalException("File not found: " + _src);
    }
}%
<link rel="stylesheet" type="text/css"#{if _id} id="${_id}"#{/if}#{if _title} title="${_title}"#{/if} href="${_abs}"#{if _media} media="${_media}"#{/if} charset="${_response_encoding}" ></link>
*@

@import play.mvc.Router, play.Play;
@args String src, String id, String title, String media, String charset
@{
    if (null == src) throw new play.exceptions.TagInternalException("src attribute cannot be empty for stylesheet tag");
    src = "/public/stylesheets/" + src;
    String _abs = null;
    try {
        _abs = Router.reverseWithCheck(src, Play.getVirtualFile(src), false);
    } catch (Exception e) {
        throw new play.exceptions.TagInternalException("File not found: " + src);
    }
}@
<script type="text/javascript" language="javascript"@if (null != id) {id="@id"} @if (null != charset) {charset="@charset"} src="@_abs"></script>
<link rel="stylesheet" type="text/css"@if (null != id) {id="@id"} @if (null != title) title="@title"} href="@_abs" @if (null != media) {media="@media"} charset="@_response_encoding" ></link>
