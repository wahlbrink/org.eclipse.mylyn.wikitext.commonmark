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

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

public class TextSegment {

	private final List<Line> lines;

	private final String text;

	public TextSegment(Iterable<Line> lines) {
		this.lines = ImmutableList.copyOf(lines);
		text = computeText(this.lines);
	}

	private static String computeText(List<Line> lines) {
		String text = "";
		for (Line line : lines) {
			if (text.length() > 0) {
				text += "\n";
			}
			text += line.getText();
		}
		return text;
	}

	public String getText() {
		return text;
	}

	public List<Line> getLines() {
		return lines;
	}

	public int offsetOf(int textOffset) {
		checkArgument(textOffset >= 0);
		int textOffsetOfLine = 0;
		int remainder = textOffset;
		for (Line line : lines) {
			textOffsetOfLine = line.getLineOffset();
			int linePlusSeparatorLength = line.getText().length() + 1;
			if (linePlusSeparatorLength > remainder) {
				break;
			}
			remainder -= linePlusSeparatorLength;
		}
		return textOffsetOfLine + remainder;
	}

	public int toTextOffset(int documentOffset) {
		int textOffset = 0;
		for (Line line : lines) {
			int lineRelativeOffset = documentOffset - line.getLineOffset();
			int linePlusSeparatorLength = line.getText().length() + 1;
			if (lineRelativeOffset >= 0 && lineRelativeOffset < linePlusSeparatorLength) {
				return textOffset + lineRelativeOffset;
			}
			textOffset += linePlusSeparatorLength;
		}
		throw new IllegalArgumentException();
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(TextSegment.class).add("text", ToStringHelper.toStringValue(text)).toString();
	}

	public Line getLineAtOffset(int textOffset) {
		int documentOffset = offsetOf(textOffset);
		Line previous = null;
		for (Line line : lines) {
			if (line.getLineOffset() > documentOffset) {
				break;
			}
			previous = line;
		}
		return checkNotNull(previous);
	}

}
