@import play.test.Fixtures, play.Play, play.db.Model, play.exceptions.TagInternalException;
@args String arg, Object delete, String load;
@{
    if ("all".equals(delete)) {
        Fixtures.deleteAll();
    } else if (delete instanceof Model) {
        //Fixtures.delete((Model)delete);
        throw new RuntimeException("delete a specific model not supported yet");
    }

    if (null != load) {
        Fixtures.load(load);
    }

    if (null != arg) {
        try {
            Play.classloader.loadClass(arg).newInstance();
        } catch (Exception e) {
            throw new TagInternalException("Cannot apply " + arg + " fixture because of " + e.getClass().getName() + ", " + e.getMessage());
        }
    }
}@

@*
groovy implementation
%{
    if(_delete == 'all') {
        play.test.Fixtures.deleteAll()
    } else if(_delete) {
        play.test.Fixtures.delete(_delete)
    }
}%

%{
    if(_load) {
        play.test.Fixtures.load(_load)
    }
}%

%{
    if(_arg && _arg instanceof String) {
        try {
            play.Play.classloader.loadClass(_arg).newInstance()
        } catch(Exception e) {
            throw new play.exceptions.TagInternalException('Cannot apply ' + _arg + ' fixture because of ' + e.getClass().getName() + ', ' + e.getMessage())
        }
    }
%}
*@
