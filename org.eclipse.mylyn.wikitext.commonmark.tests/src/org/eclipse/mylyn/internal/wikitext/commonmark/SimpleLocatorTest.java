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

package org.eclipse.mylyn.internal.wikitext.commonmark;

import static org.junit.Assert.assertEquals;

import org.eclipse.mylyn.wikitext.core.parser.Locator;
import org.junit.Test;

public class SimpleLocatorTest {

	@Test
	public void createFromLine() {
		Line line = new Line(2, 104, "one two");
		Locator simpleLocator = new SimpleLocator(line);
		assertEquals(104, simpleLocator.getDocumentOffset());
		assertEquals(0, simpleLocator.getLineCharacterOffset());
		assertEquals(104, simpleLocator.getLineDocumentOffset());
		assertEquals(7, simpleLocator.getLineLength());
		assertEquals(3, simpleLocator.getLineNumber());
		assertEquals(7, simpleLocator.getLineSegmentEndOffset());
	}

	@Test
	public void createFromLineWithSegmentOffset() {
		Line line = new Line(2, 104, "one two");
		Locator simpleLocator = new SimpleLocator(line, 2, 6);
		assertEquals(106, simpleLocator.getDocumentOffset());
		assertEquals(2, simpleLocator.getLineCharacterOffset());
		assertEquals(104, simpleLocator.getLineDocumentOffset());
		assertEquals(7, simpleLocator.getLineLength());
		assertEquals(3, simpleLocator.getLineNumber());
		assertEquals(6, simpleLocator.getLineSegmentEndOffset());
	}
}
