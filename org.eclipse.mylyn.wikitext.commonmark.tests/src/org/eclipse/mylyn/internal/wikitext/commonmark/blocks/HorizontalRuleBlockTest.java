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

import com.google.common.base.Strings;

public class HorizontalRuleBlockTest {
	private final HorizontalRuleBlock block = new HorizontalRuleBlock();

	@Test
	public void canStart() {
		assertFalse(block.canStart(new LineSequence("")));
		assertFalse(block.canStart(new LineSequence("a")));
		assertFalse(block.canStart(new LineSequence("    ***")));
		for (char c : "*_-".toCharArray()) {
			String hrIndicator = Strings.repeat("" + c, 3);
			assertTrue(block.canStart(new LineSequence("   " + hrIndicator)));
			assertTrue(block.canStart(new LineSequence("  " + hrIndicator)));
			assertTrue(block.canStart(new LineSequence(" " + hrIndicator)));
			assertFalse(block.canStart(new LineSequence("    " + hrIndicator)));
			assertTrue(block.canStart(new LineSequence(hrIndicator)));
			assertTrue(block.canStart(new LineSequence(Strings
					.repeat("" + c, 4))));
			assertTrue(block.canStart(new LineSequence(Strings.repeat("" + c,
					14))));
		}
	}

	@Test
	public void process() {
		assertContent("<p>one</p><hr/>", "one\n\n------\n");
		assertContent("<p>one</p><hr/>", "one\n\n---\n");
		assertContent("<p>one</p><hr/>", "one\n\n-  - -\n");
		assertContent("<p>one</p><hr/>", "one\n\n   ** *****\n");
	}
}
