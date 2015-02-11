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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.eclipse.mylyn.internal.wikitext.commonmark.Line;
import org.eclipse.mylyn.internal.wikitext.commonmark.ProcessingContext;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;

public class Characters extends InlineWithText {

	public Characters(Line line, int offset, String text) {
		super(line, offset, checkNotNull(text).length(), text);
	}

	@Override
	public void emit(DocumentBuilder builder) {
		builder.characters(text);
	}

	@Override
	public void apply(ProcessingContext context, List<Inline> inlines, Cursor cursor) {
		cursor.advance(getLength());
		if (!inlines.isEmpty()) {
			Inline last = inlines.get(inlines.size() - 1);
			if (last instanceof Characters) {
				Characters lastCharacters = (Characters) last;
				Characters substitution = new Characters(lastCharacters.getLine(), lastCharacters.getOffset(),
						lastCharacters.getText() + getText());
				inlines.set(inlines.size() - 1, substitution);
				return;
			}
		}
		inlines.add(this);
	}
}
