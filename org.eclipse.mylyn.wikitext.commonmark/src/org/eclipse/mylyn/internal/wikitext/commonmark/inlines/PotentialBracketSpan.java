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

public class PotentialBracketSpan extends SourceSpan {

	@Override
	public Optional<? extends Inline> createInline(Cursor cursor) {
		char c = cursor.getChar();
		if (c == '!' && cursor.hasNext() && cursor.getNext() == '[') {
			return Optional.of(new PotentialBracketDelimiter(cursor.getLineAtOffset(), cursor.getOffset(), 2,
					cursor.getTextAtOffset(2)));
		}
		if (c == '[') {
			return Optional.of(new PotentialBracketDelimiter(cursor.getLineAtOffset(), cursor.getOffset(), 1,
					cursor.getTextAtOffset(1)));
		}
		if (c == ']') {
			return Optional.of(new PotentialBracketEndDelimiter(cursor.getLineAtOffset(), cursor.getOffset()));
		}
		return Optional.absent();
	}

}
