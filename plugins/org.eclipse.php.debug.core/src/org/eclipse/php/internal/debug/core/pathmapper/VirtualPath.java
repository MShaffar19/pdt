/*******************************************************************************
 * Copyright (c) 2009, 2019 IBM Corporation and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies
 *******************************************************************************/
package org.eclipse.php.internal.debug.core.pathmapper;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents path-style entities (file-system paths, URLs). Paths
 * are case insensitive.
 * 
 * @author michael
 */
public class VirtualPath implements Cloneable {

	private static final Pattern VOLNAME = Pattern.compile("([A-Za-z]:)[/\\\\](.*)"); //$NON-NLS-1$
	private static final Pattern PROTOCOL = Pattern.compile("([A-Za-z]*://)(.*)"); //$NON-NLS-1$
	private LinkedList<String> segments;
	private String device;
	private char sepChar;

	/**
	 * Constructs new abstract path instance
	 * 
	 * @param path
	 *            Full path
	 */
	public VirtualPath(String path) {
		if (path == null) {
			throw new NullPointerException();
		}
		String originalPath = path;
		if (path.startsWith("\\\\")) { // Network path //$NON-NLS-1$
			sepChar = '\\';
			device = "\\\\"; //$NON-NLS-1$
			path = path.substring(2);
			// Basic UNC checks:
			if (path.length() == 0 || path.startsWith("\\") || path.startsWith("/")) {//$NON-NLS-1$ //$NON-NLS-2$
				throw new IllegalArgumentException("Illegal or not full path: " + originalPath);//$NON-NLS-1$
			}
		} else if (path.startsWith("\\")) { //$NON-NLS-1$
			sepChar = '\\';
			device = "\\"; //$NON-NLS-1$
		} else {
			Matcher m = VOLNAME.matcher(path);
			if (m.matches()) { // Windows path
				sepChar = '\\';
				device = m.group(1) + "\\"; // correct path from //$NON-NLS-1$
											// C:/ to C:\
				path = m.group(2);
			} else if (path.startsWith("/")) { // Unix path //$NON-NLS-1$
				sepChar = '/';
				device = "/"; //$NON-NLS-1$
			} else {
				m = PROTOCOL.matcher(path);
				if (m.matches()) { // URL
					sepChar = '/';
					device = m.group(1);
					path = m.group(2);
				} else {
					throw new IllegalArgumentException("Illegal or not full path: " + originalPath);//$NON-NLS-1$
				}
			}
		}

		StringTokenizer st = new StringTokenizer(path, "/\\"); //$NON-NLS-1$
		segments = new LinkedList<>();
		while (st.hasMoreTokens()) {
			String segment = st.nextToken();
			if (segment.length() > 0) {
				segments.add(segment);
			}
		}
	}

	/**
	 * Checks whether the given path is a local file-system path, totally
	 * excluding UNC paths and URLs. For example paths starting with
	 * \\localhost\ or file:/// will not be seen as local paths.
	 * 
	 * @param path
	 * @return <code>true</code> if given path is a local path, otherwise
	 *         <code>false</code>
	 */
	public static boolean isLocal(String path) {
		return (VOLNAME.matcher(path).matches() || path.startsWith("/") //$NON-NLS-1$
				|| (path.startsWith("\\") && !path.startsWith("\\\\"))); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Checks whether the given path is absolute
	 * 
	 * @param path
	 * @return <code>true</code> if given path is the absolute one, otherwise
	 *         <code>false</code>
	 */
	public static boolean isAbsolute(String path) {
		return (path.startsWith("\\\\") || VOLNAME.matcher(path).matches() //$NON-NLS-1$
				|| path.startsWith("/") || PROTOCOL.matcher(path).matches()); //$NON-NLS-1$
	}

	protected VirtualPath(String device, char sepChar, LinkedList<String> segments) {
		this.device = device;
		this.sepChar = sepChar;
		this.segments = segments;
	}

	public String getLastSegment() {
		return segments.getLast();
	}

	public int getSegmentsCount() {
		return segments.size();
	}

	public String removeFirstSegment() {
		return segments.removeFirst();
	}

	public String removeLastSegment() {
		return segments.removeLast();
	}

	public void addLastSegment(String segment) {
		segments.addLast(segment);
	}

	public String[] getSegments() {
		return segments.toArray(new String[segments.size()]);
	}

	public char getSeparatorChar() {
		return sepChar;
	}

	public boolean isPrefixOf(VirtualPath path) {
		Iterator<String> i1 = segments.iterator();
		Iterator<String> i2 = path.segments.iterator();
		while (i1.hasNext() && i2.hasNext()) {
			if (!i1.next().equals(i2.next())) {
				return false;
			}
		}
		return !i1.hasNext();
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder(device);
		Iterator<String> i = segments.iterator();
		while (i.hasNext()) {
			buf.append(i.next());
			if (i.hasNext()) {
				buf.append(sepChar);
			}
		}
		return buf.toString();
	}

	@Override
	public VirtualPath clone() {
		LinkedList<String> segments = new LinkedList<>();
		Iterator<String> i = this.segments.iterator();
		while (i.hasNext()) {
			segments.add(i.next());
		}
		VirtualPath path = new VirtualPath(device, sepChar, segments);
		return path;
	}

	@Override
	public int hashCode() {
		return device.hashCode() + 13 * segments.hashCode() + 31 * sepChar;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof VirtualPath)) {
			return false;
		}
		VirtualPath other = (VirtualPath) obj;
		boolean segmentsEqual = other.segments.size() == segments.size();
		if (segmentsEqual) {
			Iterator<String> i = segments.iterator();
			Iterator<String> j = other.segments.iterator();
			while (segmentsEqual && i.hasNext() && j.hasNext()) {
				segmentsEqual &= i.next().equalsIgnoreCase(j.next());
			}
		}
		return other.device.equalsIgnoreCase(device) && segmentsEqual && other.sepChar == sepChar;
	}
}