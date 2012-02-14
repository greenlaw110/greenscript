/**
 * 
 */
package com.greenscriptool;

/**
 * @author chriswebb
 *
 */
public interface IRouteMapper {
	/**
	 * @param url
	 * @return
	 */
	String route(String url);
	
	/**
	 * @param fileName
	 * @return
	 */
	String reverse(String fileName);
}
