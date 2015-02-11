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

public class LineBreakSpanTest extends AbstractSourceSpanTest {

	public LineBreakSpanTest() {
		super(new LineBreakSpan());
	}

	@Test
	public void createInline() {
		assertInline(SoftLineBreak.class, 1, 1, createCursor("a\nb", 1));
		assertInline(SoftLineBreak.class, 1, 2, createCursor("a \nb", 1));
		assertInline(SoftLineBreak.class, 1, 2, createCursor("a \nb\nc", 1));
		assertInline(HardLineBreak.class, 1, 4, createCursor("a   \nb", 1));
		assertInline(HardLineBreak.class, 1, 3, createCursor("a \\\nb", 1));
		assertNoInline(createCursor("one"));
	}
}
