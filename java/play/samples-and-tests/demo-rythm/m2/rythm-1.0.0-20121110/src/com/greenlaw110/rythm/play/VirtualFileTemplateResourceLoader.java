package com.greenlaw110.rythm.play;

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.internal.compiler.TemplateClass;
import com.greenlaw110.rythm.resource.ITemplateResource;
import com.greenlaw110.rythm.resource.ITemplateResourceLoader;
import com.greenlaw110.rythm.resource.TemplateResourceBase;
import com.greenlaw110.rythm.runtime.ITag;
import com.greenlaw110.rythm.utils.S;
import play.Play;
import play.libs.IO;
import play.mvc.Http;
import play.vfs.VirtualFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 27/01/12
 * Time: 9:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class VirtualFileTemplateResourceLoader implements ITemplateResourceLoader {

    public static VirtualFileTemplateResourceLoader instance = new VirtualFileTemplateResourceLoader();

    public static class VirtualFileTemplateResource extends TemplateResourceBase {

        private static final long serialVersionUID = -4307922939957393745L;
        private String tagName;

        private VirtualFile file;

        VirtualFileTemplateResource(VirtualFile file) {
            if (null == file) throw new NullPointerException();
            this.file = file;
            this.tagName = getFullTagName(getKey());
//            String tagRoot = RythmPlugin.tagRoot;
//            if (!tagRoot.startsWith("/")) tagRoot = '/' + tagRoot;
//            String filePath = file.relativePath();
//            filePath = filePath.replaceFirst("\\{.*\\}", ""); // strip off module prefix
//            if (filePath.startsWith(tagRoot)) {
//                String tagName = filePath.substring(tagRoot.length() + 1);
//                while (tagName.startsWith("/") || tagName.startsWith("\\")) {
//                    tagName = tagName.substring(1);
//                }
//                tagName = tagName.replace('\\', '.');
//                tagName = tagName.replace('/', '.');
//                int dot = tagName.lastIndexOf(".");
//                this.tagName = tagName.substring(0, dot);
//            }
        }

        @Override
        protected long defCheckInterval() {
            return 100;
        }

        @Override
        protected long lastModified() {
            return file.lastModified();
        }

        @Override
        protected String reload() {
            return IO.readContentAsString(file.inputstream());
        }

        @Override
        public String getKey() {
            String path = file.relativePath();
            return path.replaceFirst("\\{.*?\\}", "");
        }

        @Override
        public boolean isValid() {
            return VirtualFileTemplateResourceLoader.isValid(file);
        }

        @Override
        public String getSuggestedClassName() {
            return path2CN(file.relativePath().replaceFirst("\\{.*\\}", ""));
        }

        @Override
        public String tagName() {
            return tagName;
        }

        public boolean exists() {
            return null != file && file.exists();
        }

        @Override
        public int hashCode() {
            return file.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj instanceof VirtualFileTemplateResource) {
                VirtualFileTemplateResource that = (VirtualFileTemplateResource)obj;
                return (that.file == this.file) || that.file.equals(this.file);
            }
            return false;
        }
    }

    public static boolean isValid(VirtualFile file) {
        return (null != file) && file.exists() && file.getRealFile().canRead();
    }

    private VirtualFile loadFromPath_(String path) {
        VirtualFile vf = null;
        if (path.indexOf("module:") != -1) vf = VirtualFile.fromRelativePath(path);
        else {
            vf = Play.getVirtualFile(path);
            if (!isValid(vf)) {
                if (!path.startsWith("/")) path = "/" + path;
                // try to attach template home and tag home
//                if (!path.startsWith(RythmPlugin.templateRoot2)) {
//                    String path0 = RythmPlugin.templateRoot2 + path;
//                    vf = Play.getVirtualFile(path0);
//                }
                if (!isValid(vf) && !path.startsWith(RythmPlugin.templateRoot)) {
                    String path0 = RythmPlugin.templateRoot + path;
                    vf = Play.getVirtualFile(path0);
                }
//                if (!isValid(vf) && !path.startsWith(RythmPlugin.tagRoot)) {
//                    String path0 = RythmPlugin.tagRoot + path;
//                    vf = Play.getVirtualFile(path0);
//                }
            }
        }
        return vf;
    }

    @Override
    public ITemplateResource load(String path) {
        VirtualFile vf = loadFromPath_(path);
        if (!isValid(vf) && path.indexOf("module:") == -1) {
            // try to see if it's package.class style
            // change a packaged name into a file path name
            path = path.replace(".", "/");
            // but not for suffix
            int pos = path.lastIndexOf("/");
            if (-1 != pos) {
                String path0 = path;
                path = path0.substring(0, pos) + "." + path0.substring(pos + 1);
            }
            vf = loadFromPath_(path);
        }
        if (!isValid(vf)) return null;
        // don't check black and white list as this is initialized from template engine side, which
        // might be very well loading a extended template which is not built into BW list
        return load(vf, false);
    }

    private VirtualFileTemplateResourceLoader.VirtualFileTemplateResource load(VirtualFile file, boolean checkBWList) {
        String path = file.relativePath();
        if (path.contains(".svn")) return null; // definitely we don't want to load anything inside there
        return new VirtualFileTemplateResource(file);
    }

    public VirtualFileTemplateResourceLoader.VirtualFileTemplateResource load(VirtualFile file) {
        return load(file, true);
    }

    @Override
    public String getFullTagName(TemplateClass tc) {
        String key = tc.getKey().toString();
        return getFullTagName(key);
    }

    private static String getFullTagName(String key) {
        if (key.startsWith("/")) key = key.substring(1);
        if (key.startsWith(RythmPlugin.templateRoot)) {
            key = key.replace(RythmPlugin.templateRoot, "");
        }
        if (key.startsWith("/")) key = key.substring(1);
        int pos = key.lastIndexOf(".");
        if (-1 != pos) key = key.substring(0, pos);
        return key.replace('/', '.');
    }

    @Override
    public TemplateClass tryLoadTag(String tagName, TemplateClass templateClass) {
//Logger.info(">>> try to load tag: %s", tagName);
        RythmEngine engine = RythmPlugin.engine;
        if (engine.tags.containsKey(tagName)) return null; //TODO: not consistent here
//Logger.info(">>> try to load tag: %s, tag not found in engine registry, continue loading", tagName);
        String origName = tagName;
        tagName = tagName.replace('.', '/');
        final String[] suffixes = {
                ".html",
                ".json",
                ".xml",
                ".csv",
                ".tag"
        };
        String defSuffix = null;
        Http.Request request = Http.Request.current();
        if (null != request) {
            defSuffix = "." + request.format;
        }
        List<String> sl = new ArrayList<String>();
        sl.add(defSuffix);
        for (String s : suffixes) {
            if (!S.isEqual(s, defSuffix)) sl.add(s);
        }

        List<String> roots = new ArrayList<String>();
        roots.add(RythmPlugin.templateRoot);

        // call tag with import path
        if (null != templateClass.importPaths) {
            for (String s: templateClass.importPaths) {
                roots.add(RythmPlugin.templateRoot + "/" + s.replace('.', '/'));
            }
        }

        String tagName0 = tagName;
        // call tag using relative path
        String currentPath = templateClass.getKey().toString();
        int pos = currentPath.lastIndexOf("/");
        if (-1 != pos) {
            currentPath = currentPath.substring(0, pos);
            if (currentPath.startsWith("/")) currentPath = currentPath.substring(1);
            if (!currentPath.startsWith(RythmPlugin.templateRoot)) currentPath = RythmPlugin.templateRoot + "/" + currentPath;
            roots.add(currentPath);
        }

        for (String root : roots) {
            tagName = root + "/" + tagName0;
            VirtualFile tagFile = null;
            for (String suffix : sl) {
                String name = tagName + suffix;
                tagFile = Play.getVirtualFile(name);
                if (null != tagFile && tagFile.getRealFile().canRead()) {
                    VirtualFileTemplateResource tr = new VirtualFileTemplateResource(tagFile);
                    TemplateClass tc = engine.classes.getByTemplate(tr.getKey());
                    if (null == tc) {
                        tc = new TemplateClass(tr, engine);
                    } else if (tc.equals(templateClass)) {
                        // call self
                        return templateClass;
                    }
                    ITag tag = (ITag) tc.asTemplate();
                    if (null != tag) {
                        String fullName = getFullTagName(tc);
                        tc.setFullName(fullName);
                        engine.registerTag(fullName, tag);
                        return tc;
                    }
                }
            }
        }
        return null;
    }

}
