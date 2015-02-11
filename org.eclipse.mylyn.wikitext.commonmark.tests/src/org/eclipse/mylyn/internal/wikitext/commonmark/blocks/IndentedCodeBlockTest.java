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

public class IndentedCodeBlockTest {

	private final IndentedCodeBlock block = new IndentedCodeBlock();

	@Test
	public void canStart() {
		assertFalse(block.canStart(new LineSequence("   code")));
		assertTrue(block.canStart(new LineSequence("    code")));
		assertTrue(block.canStart(new LineSequence("     code")));
		assertFalse(block.canStart(new LineSequence(" code")));
		assertFalse(block.canStart(new LineSequence("  code")));
		assertFalse(block.canStart(new LineSequence("non-blank\n    code")));
		assertTrue(block.canStart(new LineSequence("\tcode")));
		assertTrue(block.canStart(new LineSequence("\t code")));
		assertTrue(block.canStart(new LineSequence(" \tcode")));
		assertTrue(block.canStart(new LineSequence("  \tcode")));
		assertTrue(block.canStart(new LineSequence("   \tcode")));
	}

	@Test
	public void process() {
		assertContent("<pre><code>code\n</code></pre>", "    code");
		assertContent("<pre><code>code\n</code></pre>", "\tcode");
		assertContent("<pre><code> code\n</code></pre>", "\t code");
		assertContent("<pre><code>\tcode\n</code></pre>", "    \tcode");
		assertContent("<pre><code>one\ntwo\n</code></pre><p>three</p>", "    one\n    two\n three");
		assertContent("<pre><code>one\n\nthree\n</code></pre>", "    one\n\n    three");
		assertContent("<pre><code>one\n  \nthree\n</code></pre>", "    one\n      \n    three");
	}
}
