/*******************************************************************************
 * Copyright (c) 2015 David Green.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.commonmark;

public class ToStringHelper {

	private static final String ELIPSES = "...";
	private static final int STRING_MAX_LENGTH = 20;

	public static String toStringValue(String text) {
		if (text == null) {
			return text;
		}
		String stringValue = text;
		if (stringValue.length() > 20) {
			stringValue = stringValue.substring(0, STRING_MAX_LENGTH) + ELIPSES;
		}
		return stringValue.replace("\t", "\\t").replace("\n", "\\n").replace("\r", "\\r");
	}

	private ToStringHelper() {
		// prevent instantiation
	}
}
