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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.eclipse.mylyn.internal.wikitext.commonmark.Line;
import org.eclipse.mylyn.internal.wikitext.commonmark.ProcessingContext;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.Locator;
import org.junit.Test;

public class InlineTest {

	@Test
	public void create() {
		Line line = new Line(3, 5, "text");
		Inline inline = new Inline(line, 8, 3) {

			@Override
			public void emit(DocumentBuilder builder) {
			}
		};
		assertSame(line, inline.getLine());
		assertEquals(8, inline.getOffset());
		assertEquals(3, inline.getLength());
		Locator locator = inline.getLocator();
		assertEquals(3, locator.getLineCharacterOffset());
		assertEquals(6, locator.getLineSegmentEndOffset());
		assertEquals(8, locator.getDocumentOffset());
		assertEquals(line.getLineOffset(), locator.getLineDocumentOffset());
		assertEquals(line.getText().length(), locator.getLineLength());
		assertEquals(line.getLineNumber() + 1, locator.getLineNumber());
	}

	@Test
	public void createContext() {
		ProcessingContext context = new Inline(new Line(1, 2, "text"), 0, 1) {

			@Override
			public void emit(DocumentBuilder builder) {
			}
		}.createContext();
		assertSame(ProcessingContext.empty(), context);
	}
}
