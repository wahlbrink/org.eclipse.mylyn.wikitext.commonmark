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

public class AtxHeaderBlockTest {

	AtxHeaderBlock block = new AtxHeaderBlock();

	@Test
	public void canStart() {
		assertFalse(block.canStart(new LineSequence("")));
		assertTrue(block.canStart(new LineSequence("#")));
		assertTrue(block.canStart(new LineSequence("# ")));
		assertTrue(block.canStart(new LineSequence("# #")));
		assertTrue(block.canStart(new LineSequence("# Y")));
		assertTrue(block.canStart(new LineSequence("# Y #")));
		assertTrue(block.canStart(new LineSequence("## Y")));
		assertTrue(block.canStart(new LineSequence("### Y")));
		assertTrue(block.canStart(new LineSequence("#### Y")));
		assertTrue(block.canStart(new LineSequence("##### Y")));
		assertTrue(block.canStart(new LineSequence("###### Y")));
		assertFalse(block.canStart(new LineSequence("####### Y")));
		assertTrue(block.canStart(new LineSequence("# Y#")));
	}

	@Test
	public void basic() {
		assertContent("<h2>One Two</h2>", "## One Two");
		assertContent("<h2>One Two</h2>", "## One Two #####   ");
		assertContent("<h2>One Two#</h2>", "## One Two#");
		assertContent("<p>One</p><h1>two</h1><p>Three</p>", "One\n# two\nThree");
		assertContent("<h2></h2>", "##");
		assertContent("<h2></h2>", "## ##");
	}

	@Test
	public void withNestedInlines() {
		assertContent("<h2>One <em>Two</em> \\<strong>three</strong></h2>", "## One *Two* \\\\__three__ ##");
	}
}
