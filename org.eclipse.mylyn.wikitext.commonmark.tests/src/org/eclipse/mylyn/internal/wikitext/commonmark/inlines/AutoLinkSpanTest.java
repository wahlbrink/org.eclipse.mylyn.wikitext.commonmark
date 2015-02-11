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
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AutoLinkSpanTest extends AbstractSourceSpanTest {

	public AutoLinkSpanTest() {
		super(new AutoLinkSpan());
	}

	@Test
	public void createInline() {
		assertNoInline(createCursor("<http://example.com sdf"));
		assertNoInline(createCursor("<http:/ >"));
		assertNoInline(createCursor("<http://example.com sdf>"));
		assertNoInline(createCursor("http://example.com"));
		assertLink(30, "http://example.com:8080/#see", "http://example.com:8080/#see",
				createCursor("<http://example.com:8080/#see> one"));
		assertLink(37, "ftp:/example.com/a/b/c?one=&two=%20", "ftp:/example.com/a/b/c?one=&two=%20",
				createCursor("<ftp:/example.com/a/b/c?one=&two=%20> one"));
		assertLink(24, "ssh://user@example.com", "ssh://user@example.com", createCursor("<ssh://user@example.com>"));
		assertLink(23, "mailto:user@test.example.com", "user@test.example.com", createCursor("<user@test.example.com>"));
	}

	private void assertLink(int length, String linkHref, String text, Cursor cursor) {
		Link link = assertInline(Link.class, 0, length, cursor);
		assertEquals(linkHref, link.getHref());
		assertEquals(1, link.getContents().size());
		assertEquals(Characters.class, link.getContents().get(0).getClass());
		assertEquals(text, ((Characters) link.getContents().get(0)).getText());
	}
}
