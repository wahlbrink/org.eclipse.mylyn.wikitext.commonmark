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

package org.eclipse.mylyn.internal.wikitext.commonmark.blocks;

import static org.eclipse.mylyn.internal.wikitext.commonmark.CommonMarkAsserts.assertContent;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.mylyn.internal.wikitext.commonmark.LineSequence;
import org.junit.Test;

public class ParagraphBlockTest {

	private final ParagraphBlock block = new ParagraphBlock();

	@Test
	public void canStart() {
		assertFalse(block.canStart(new LineSequence("")));
		assertFalse(block.canStart(new LineSequence("\none")));
		assertTrue(block.canStart(new LineSequence("one")));
	}

	@Test
	public void assertParagraph() {
		assertContent("<p>first para second line</p><p>second para fourth line here</p>",
				"first para\nsecond line\n\nsecond para\nfourth line here\n\n\n");
		assertContent("<p>first para second line</p>", "first para\n    second line");
	}
}
