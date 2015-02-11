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

package org.eclipse.mylyn.internal.wikitext.commonmark.inlines;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.internal.wikitext.commonmark.Line;
import org.eclipse.mylyn.internal.wikitext.commonmark.TextSegment;

public class Cursor {

	private final TextSegment segment;

	private final String text;

	private int textOffset;

	public Cursor(TextSegment segment) {
		this.segment = checkNotNull(segment);
		this.text = segment.getText();
		this.textOffset = 0;
	}

	/**
	 * Provides the offset of the current cursor position relative to the document.
	 * 
	 * @return the current cursor position offset
	 */
	public int getOffset() {
		return getOffset(textOffset);
	}

	/**
	 * Provides the offset of the given cursor position relative to the document.
	 * 
	 * @param cursorOffset
	 *            the position relative to the cursor
	 * @return the current cursor position offset
	 */
	public int getOffset(int cursorOffset) {
		return segment.offsetOf(cursorOffset);
	}

	public int toCursorOffset(int documentOffset) {
		return segment.toTextOffset(documentOffset);
	}

	public char getChar() {
		return text.charAt(textOffset);
	}

	/**
	 * Provides the character at the cursor's 0-based offset, where the given offset is not affected by the position of
	 * the cursor.
	 * 
	 * @param offset
	 *            the absolute offset of the character relative to this cursor
	 * @return the character
	 */
	public char getChar(int offset) {
		return text.charAt(offset);
	}

	public boolean hasChar() {
		return textOffset < text.length();
	}

	public char getPrevious() {
		return getPrevious(1);
	}

	public boolean hasPrevious() {
		return hasPrevious(1);
	}

	public boolean hasPrevious(int offset) {
		return textOffset - offset >= 0;
	}

	public char getPrevious(int offset) {
		int charOffset = textOffset - offset;
		checkArgument(charOffset >= 0);
		return text.charAt(charOffset);
	}

	public char getNext() {
		return getNext(1);
	}

	public char getNext(int offset) {
		checkArgument(offset >= 0);
		return text.charAt(textOffset + offset);
	}

	public String getTextAtOffset() {
		return text.substring(textOffset, text.length());
	}

	public String getTextAtOffset(int length) {
		checkArgument(length > 0);
		return text.substring(textOffset, textOffset + length);
	}

	public boolean hasNext() {
		return hasNext(1);
	}

	public boolean hasNext(int offset) {
		checkArgument(offset > 0);
		return textOffset + offset < text.length();
	}

	public Matcher matcher(Pattern pattern) {
		return matcher(0, pattern);
	}

	public Matcher matcher(int offset, Pattern pattern) {
		checkArgument(offset >= 0 && (offset + textOffset < text.length()));
		checkNotNull(pattern);
		Matcher matcher = pattern.matcher(text);
		matcher.region(textOffset + offset, text.length());
		return matcher;
	}

	public void advance() {
		if (textOffset < text.length()) {
			++textOffset;
		}
	}

	public void advance(int count) {
		for (int x = 0; x < count; ++x) {
			advance();
		}
	}

	public void rewind(int count) {
		for (int x = 0; x < count; ++x) {
			rewind();
		}
	}

	public void rewind() {
		if (textOffset > 0) {
			--textOffset;
		}
	}

	public Line getLineAtOffset() {
		return segment.getLineAtOffset(textOffset);
	}

}
