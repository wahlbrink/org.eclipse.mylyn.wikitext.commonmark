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

import static org.junit.Assert.assertEquals;

import org.eclipse.mylyn.internal.wikitext.commonmark.LineSequence;
import org.junit.Test;

public class ListBlockTest {

	@Test
	public void canStart() {
		assertCanStart(true, "-");
		assertCanStart(true, "- ");
		assertCanStart(true, "- test");
		assertCanStart(true, " - test");
		assertCanStart(true, "  - test");
		assertCanStart(true, "   - test");
		assertCanStart(false, "    - test");
		assertCanStart(true, "* test");
		assertCanStart(true, "+ test");
	}

	private void assertCanStart(boolean expected, String string) {
		assertEquals(expected, new ListBlock().canStart(new LineSequence(string)));
	}
}
