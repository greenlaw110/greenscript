package com.greenscriptool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The implementation of {@link IRenderSession} interface
 * 
 * @author greenlaw110@gmail.com
 * @version 1.0.1, 2010-11-13 add compatibility to play-greenscript v1.1 or before
 * @version 1.0, 2010-10-15 original version
 * @since 1.0
 */
public class RenderSession implements IRenderSession {
	private static Log logger_ = LogFactory.getLog(IRenderSession.class);

	private IMinimizer m_ = null;

	private IDependenceManager d_ = null;

	private ResourceType type_ = null;

	/**
	 * Store resource declared using {@link #declare(String, String, String)}
	 */
	private List<Resource> declared_ = new ArrayList<RenderSession.Resource>();

	/**
	 * Store all resources that has been loaded (in this session) already
	 */
	private Set<String> loaded_ = new HashSet<String>();

	/**
	 * Store inline bodies declared
	 */
	private SortedMap<Integer, StringBuffer> inlines_ = new TreeMap<Integer, StringBuffer>();

	public ResourceType getResourceType() {
		return type_;
	}

	/**
	 * Construct an {@link IRenderSession} with an {@link IMinimizer} instance, an {@link IDependenceManager} instance
	 * and a {@link ResourceType}
	 * 
	 * @param minimizer
	 * @param depMgr
	 * @param type
	 */
	@Inject
	public RenderSession(IMinimizer minimizer, IDependenceManager depMgr, ResourceType type) {
		if (null == minimizer || null == depMgr)
			throw new NullPointerException();
		m_ = minimizer;
		d_ = depMgr;
		type_ = type;
	}

	@Override
	public void declareInline(String inline, int priority) {
		priority = -1 * priority;
		StringBuffer sb = inlines_.get(priority);
		if (null == sb) {
			sb = new StringBuffer();
			inlines_.put(priority, sb);
		}
		sb.append("\n").append(inline);
	}

	@Override
	public void declare(String nameList, String media, String browser) {
		String[] sa = nameList.split(SEPARATOR);
		media = canonical_(media);
		browser = canonical_(browser);
		for (String name : sa) {
			addDeclared(media, browser, name);
		}
	}

	@Override
	public void declare(List<String> nameList, String media, String browser) {
		media = canonical_(media);
		browser = canonical_(browser);
		for (String name : nameList) {
			addDeclared(media, browser, name);
		}
	}

	@Override
	public List<String> output(String nameList, boolean withDependencies, boolean all, String media, String browser) {
		if (null != nameList)
			declare(nameList, null, null);

		List<String> l = null;
		if (all) {
			l = d_.comprehend(getByMediaAndBrowser_(media, browser), true);
		} else if (withDependencies) {
			l = d_.comprehend(nameList);
		} else if (null != nameList) {
			l = new ArrayList<String>();
			String[] sa = nameList.split(SEPARATOR);
			for (String s : sa) {
				if (!l.contains(s))
					l.add(s);
			}
		} else {
			l = Collections.emptyList();
		}

		l = l.isEmpty() ? l : m_.process(l);
		l.removeAll(loaded_);
		loaded_.addAll(l);
		if (logger_.isTraceEnabled())
			logger_.trace("output items: " + l);
		return l;
	}

	@Override
	public String outputInline() {
		StringBuffer all = new StringBuffer();
		for (StringBuffer sb : inlines_.values()) {
			all.append(sb);
			sb.delete(0, sb.length());
		}
		return all.toString();
	}

	public boolean isDefault(String s) {
		s = canonical_(s);
		return s.equalsIgnoreCase(DEFAULT);
	}

	private String canonical_(String s) {
		if (null == s)
			return DEFAULT;
		return s.trim().replaceAll("\\s+", " ");
	}

	private List<String> getByMediaAndBrowser_(String media, String browser) {
		List<String> list = new ArrayList<String>();
		media = canonical_(media);
		browser = canonical_(browser);
		for (Resource r : declared_) {
			if (r.media.equalsIgnoreCase(media) && r.browser.equalsIgnoreCase(browser)) {
				if (!list.contains(r.name)) {
					list.add(r.name);
				}
			}
		}
		list.removeAll(loaded_);
		return list;
	}

	@Override
	public Set<String> getMedias(String browser) {
		Set<String> set = new HashSet<String>();
		browser = canonical_(browser);
		for (Resource r : declared_) {
			if (r.browser.equalsIgnoreCase(browser))
				set.add(r.media);
		}
		set.remove(DEFAULT);
		return set;
	}

	@Override
	public Set<String> getBrowsers() {
		Set<String> set = new HashSet<String>();
		for (Resource r : declared_) {
			set.add(r.browser);
		}
		set.remove(DEFAULT);
		return set;
	}

	@Override
	public boolean hasDeclared() {
		return declared_.size() > 0;
	}

	private void addDeclared(String media, String browser, String name) {
		Resource resource = new Resource(name, media, browser);
		addDeclared(resource);
	}

	private void addDeclared(Resource resource) {
		if (!declared_.contains(resource)) {
			declared_.add(resource);
		}
	}

	private class Resource {
		String name;
		String media;
		String browser;

		public Resource(String name, String media, String browser) {
			if (null == name)
				throw new NullPointerException();
			this.name = name;
			this.media = null == media ? DEFAULT : media;
			this.browser = null == browser ? DEFAULT : browser;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (!(obj instanceof Resource))
				return false;
			Resource that = (Resource) obj;
			return that.name.equals(name) && that.media.equals(media) && that.browser.equals(browser);
		}

		@Override
		public int hashCode() {
			int ret = 17;
			ret = ret * 31 + name.hashCode();
			ret = ret * 31 + media.hashCode();
			ret = ret * 31 + browser.hashCode();
			return ret;
		}

		@Override
		public String toString() {
			return name;
		}
	}
}
