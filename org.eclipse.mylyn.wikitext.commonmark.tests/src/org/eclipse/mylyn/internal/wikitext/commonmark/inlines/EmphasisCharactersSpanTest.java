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

import static org.eclipse.mylyn.internal.wikitext.commonmark.inlines.Cursors.createCursor;

import org.junit.Test;

public class EmphasisCharactersSpanTest extends AbstractSourceSpanTest {

	public EmphasisCharactersSpanTest() {
		super(new EmphasisCharactersSpan());
	}

	@Test
	public void createInline() {
		assertNoInline(createCursor("one"));
		assertNoInline(createCursor("o`e"));
		assertInline(Characters.class, 0, 1, createCursor("`_"));
		assertInline(Characters.class, 1, 1, createCursor("`_", 1));
		assertInline(Characters.class, 0, 3, createCursor("___"));
		assertInline(Characters.class, 0, 3, createCursor("___**"));
		assertInline(Characters.class, 0, 2, createCursor("**"));
		assertInline(Characters.class, 0, 2, createCursor("**\nabc"));
	}
}
