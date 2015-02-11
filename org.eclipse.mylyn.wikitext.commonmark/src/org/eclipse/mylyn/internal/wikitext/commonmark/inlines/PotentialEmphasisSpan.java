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

import com.google.common.base.Optional;

public class PotentialEmphasisSpan extends SourceSpan {

	@Override
	public Optional<? extends Inline> createInline(Cursor cursor) {
		char c = cursor.getChar();
		if ((c == '_' || c == '*') && !currentPositionIsEscaped(cursor)) {
			int length = lengthMatching(cursor, c);
			boolean canClose = !cursor.hasPrevious() || !Character.isWhitespace(cursor.getPrevious());
			boolean canOpen = !cursor.hasNext(length) || !Character.isWhitespace(cursor.getNext(length));
			if (c == '_') {
				if (canOpen) {
					canOpen = !cursor.hasPrevious() || !isLetterOrDigit(cursor.getPrevious());
				}
				if (canClose) {
					canClose = !cursor.hasNext(length) || !isLetterOrDigit(cursor.getNext(length));
				}
			}
			return Optional.of(new PotentialEmphasisDelimiter(cursor.getLineAtOffset(), cursor.getOffset(), length,
					cursor.getTextAtOffset(length), canOpen, canClose));
		}
		return Optional.absent();
	}

	private boolean currentPositionIsEscaped(Cursor cursor) {
		int backslashCount = 0;
		for (int x = 1; cursor.hasPrevious(x) && cursor.getPrevious(x) == '\\'; ++x) {
			++backslashCount;
		}
		return backslashCount % 2 == 1;
	}

	private int lengthMatching(Cursor cursor, char c) {
		int x = 1;
		while (cursor.hasNext(x) && cursor.getNext(x) == c) {
			++x;
		}
		return x;
	}

	static boolean isLetterOrDigit(char previous) {
		return (previous >= '0' && previous <= '9') || (previous >= 'A' && previous <= 'Z')
				|| (previous >= 'a' && previous <= 'z');
	}
}
