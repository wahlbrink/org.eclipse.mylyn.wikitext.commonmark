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

public class FencedCodeBlockTest {

	private final FencedCodeBlock block = new FencedCodeBlock();

	@Test
	public void canStart() {
		assertCanStart("```");
		assertCanStart(" ```");
		assertCanStart("  ```");
		assertCanStart("   ```");
		assertCanStart("````````````````");
		assertCanStart("    ```");
		assertCanStartFalse("     ```");
		assertCanStart("~~~");
		assertCanStart(" ~~~");
		assertCanStart("  ~~~");
		assertCanStart("   ~~~");
		assertCanStart("    ~~~");
		assertCanStartFalse("     ~~~");
		assertCanStart("~~~~~~~~~~~~~~~~~");
		assertCanStartFalse("``` one ``");
	}

	@Test
	public void canStartWithInfoText() {
		assertCanStart("```````````````` some info text");
		assertCanStart("~~~~~~~~~ some info text");
	}

	@Test
	public void basic() {
		assertContent(
				"<p>first para</p><pre><code class=\"language-java\">public void foo() {\n\n}\n</code></pre><p>text</p>",
				"first para\n\n```` java and stuff\npublic void foo() {\n\n}\n````\ntext");
	}

	@Test
	public void encodedCharacters() {
		assertContent("<pre><code>&lt;\n &gt;\n</code></pre>",
				"```\n<\n >\n```");
	}

	private void assertCanStartFalse(String content) {
		assertFalse(content, block.canStart(new LineSequence(content)));
	}

	private void assertCanStart(String content) {
		assertTrue(content, block.canStart(new LineSequence(content)));
	}
}
