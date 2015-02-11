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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.mylyn.wikitext.core.parser.Locator;

import com.google.common.base.CharMatcher;
import com.google.common.base.Objects;

public class Line {

	private final String text;

	private final int lineOffset;

	private final int lineNumber;

	public Line(int lineNumber, int lineOffset, String text) {
		checkArgument(lineOffset >= 0);
		checkArgument(lineNumber >= 0);
		this.lineNumber = lineNumber;
		this.lineOffset = lineOffset;
		this.text = checkNotNull(text);
	}

	public boolean isEmpty() {
		return !CharMatcher.WHITESPACE.negate().matchesAnyOf(text);
	}

	public String getText() {
		return text;
	}

	/**
	 * Provides the 0-based offset of the first character of the line.
	 * 
	 * @return the line offset
	 */
	public int getLineOffset() {
		return lineOffset;
	}

	/**
	 * Provides the 0-based line number.
	 * 
	 * @return the line number
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * Provides a segment of this line, with {@link #getText() text}.
	 * 
	 * @param offset
	 *            the 0-based offset of the {@link #getText() text}
	 * @param length
	 *            the length of the {@link #getText() text} from the given {@code offset}
	 * @return the segment
	 */
	public Line segment(int offset, int length) {
		return new Line(lineNumber, lineOffset + offset, text.substring(offset, offset + length));
	}

	public Locator toLocator() {
		return new SimpleLocator(this);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(Line.class)
				.add("lineNumber", lineNumber)
				.add("lineOffset", lineOffset)
				.add("text", ToStringHelper.toStringValue(text))
				.toString();
	}
}
