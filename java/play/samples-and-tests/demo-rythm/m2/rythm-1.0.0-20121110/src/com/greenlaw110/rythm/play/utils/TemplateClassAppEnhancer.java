package com.greenlaw110.rythm.play.utils;

import com.greenlaw110.rythm.play.VirtualFileTemplateResourceLoader;
import com.greenlaw110.rythm.resource.ITemplateResource;
import play.Play;
import play.vfs.VirtualFile;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>TemplateClassAppEnhancer</code> scans a specific file named "app/rythm/_add_on.src" in which the
 * content will be added to template class source code
 */
public class TemplateClassAppEnhancer {
    public static final String ADD_ON_SRC_FN = "__add_on.src";
    public static final String ADD_ON_IMPORT_FN = "__add_on.import";

    private static List<VirtualFileTemplateResourceLoader.VirtualFileTemplateResource> srcResources = new ArrayList<VirtualFileTemplateResourceLoader.VirtualFileTemplateResource>();
    private static List<VirtualFileTemplateResourceLoader.VirtualFileTemplateResource> importResources = new ArrayList<VirtualFileTemplateResourceLoader.VirtualFileTemplateResource>();

    private static String srcCache = null;
    private static String importCache = null;

    public static void clearCache() {
        srcResources.clear();
        importResources.clear();
    }
    public static boolean changed() {
        boolean changed = false;
        StringBuilder sb = new StringBuilder();
        changed = loadResources(ADD_ON_SRC_FN, "rythm.addon.src", srcResources, sb);
        srcCache = sb.toString();
        sb = new StringBuilder();
        if (loadResources(ADD_ON_IMPORT_FN, "rythm.addon.import", importResources, sb)) changed = true;
        importCache = sb.toString();
        return changed;
    }
    // return true if new sources loaded
    private static boolean loadResources(String defFileName, String propertyKey, List<VirtualFileTemplateResourceLoader.VirtualFileTemplateResource> cache, StringBuilder stringCache) {
        boolean changed = false;

        String fnDef = defFileName;
        String fn = Play.configuration.getProperty(propertyKey, fnDef);
        StringBuilder sb = new StringBuilder();

        // remove resources that are deleted
        List<VirtualFileTemplateResourceLoader.VirtualFileTemplateResource> deleted = new ArrayList<VirtualFileTemplateResourceLoader.VirtualFileTemplateResource>();
        for (VirtualFileTemplateResourceLoader.VirtualFileTemplateResource tr: cache) {
            if (!tr.exists()) deleted.add(tr);
        }
        if (!deleted.isEmpty()) {
            changed = true;
            cache.removeAll(deleted);
        }

        // add project source
        VirtualFile appRoot = Play.roots.get(0);
        VirtualFile vf = appRoot.child("app/rythm/" + fn);
        VirtualFileTemplateResourceLoader loader = VirtualFileTemplateResourceLoader.instance;
        if (null != vf && vf.exists()) {
            VirtualFileTemplateResourceLoader.VirtualFileTemplateResource tr = loader.load(vf);
            boolean found = false;
            for (VirtualFileTemplateResourceLoader.VirtualFileTemplateResource tr0: cache) {
                if (tr0.equals(tr)) {
                    if (tr0.refresh()) changed = true;
                    stringCache.append(tr0.asTemplateContent()).append('\n');
                    found = true;
                }
            }
            if (!found) {
                changed = true;
                cache.add(tr);
                stringCache.append(tr.asTemplateContent()).append('\n');
            }
        }
        // add module source
        String fullPath = "app/rythm/" + defFileName;
        for (int i = 1; i < Play.roots.size(); ++i) {
            vf = Play.roots.get(i).child(fullPath);
            if (null != vf && vf.exists()) {
                VirtualFileTemplateResourceLoader.VirtualFileTemplateResource tr = loader.load(vf);
                boolean found = false;
                for (VirtualFileTemplateResourceLoader.VirtualFileTemplateResource tr0: cache) {
                    if (tr0.equals(tr)) {
                        if (tr0.refresh()) changed = true;
                        stringCache.append(tr0.asTemplateContent()).append('\n');
                        found = true;
                    }
                }
                if (!found) {
                    changed = true;
                    cache.add(tr);
                    stringCache.append(tr.asTemplateContent()).append('\n');
                }
            }
        }
        return changed;
    }

    public static String sourceCode() {
        StringBuilder sb = new StringBuilder();
        loadResources(ADD_ON_SRC_FN, "rythm.addon.src", srcResources, sb);
        return sb.toString();
    }

    public static String imports() {
        StringBuilder sb = new StringBuilder();
        loadResources(ADD_ON_IMPORT_FN, "rythm.addon.import", importResources, sb);
        return sb.toString().replaceAll("[\\n ]+", ",");
    }
}
