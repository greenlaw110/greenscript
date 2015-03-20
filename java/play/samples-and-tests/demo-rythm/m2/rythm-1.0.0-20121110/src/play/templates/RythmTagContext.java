package play.templates;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 20/07/12
 * Time: 2:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class RythmTagContext {
    public static boolean initialized() {
        return TagContext.currentStack.get() != null;
    }
    public static void enterTag(String name) {
        if (!initialized()) TagContext.init();
        TagContext.enterTag(name);
    }

    public static void exitTag() {
        if (!initialized()) return;
        TagContext.exitTag();
    }
}
