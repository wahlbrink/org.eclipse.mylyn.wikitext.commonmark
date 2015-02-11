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

import org.junit.Test;

public class PotentialEmphasisSpanTest extends AbstractSourceSpanTest {

	public PotentialEmphasisSpanTest() {
		super(new PotentialEmphasisSpan());
	}

	@Test
	public void emphasis() {
		assertParseToHtml("* one*", "* one*");
		assertParseToHtml("*one *", "*one *");
		assertParseToHtml("*one", "*one");
		assertParseToHtml("<em>some text</em>", "*some text*");
		assertParseToHtml("<em>some text</em> and more", "*some text* and more");
		assertParseToHtml("<em>some\ntext</em>d", "*some\ntext*d");
		assertParseToHtml("*<em>one</em>", "**one*");
	}

	@Test
	public void strongEmphasis() {
		assertParseToHtml("** one**", "** one**");
		assertParseToHtml("**one **", "**one **");
		assertParseToHtml("**one", "**one");
		assertParseToHtml("<strong>some text</strong>", "**some text**");
		assertParseToHtml("<strong>some text</strong> and more", "**some text** and more");
		assertParseToHtml("<strong>some\ntext</strong>d", "**some\ntext**d");
	}

	@Test
	public void underscoreEmphasis() {
		assertParseToHtml("_ one_", "_ one_");
		assertParseToHtml("_one _", "_one _");
		assertParseToHtml("_one", "_one");
		assertParseToHtml("a_one_", "a_one_");
		assertParseToHtml("_one_a", "_one_a");
		assertParseToHtml("<em>s</em>", "_s_");
		assertParseToHtml("<em>some text</em>", "_some text_");
		assertParseToHtml("<em>some text</em> and more", "_some text_ and more");
		assertParseToHtml("<em>some\ntext</em>", "_some\ntext_");
		assertParseToHtml("<em>some text_a b</em> a", "_some text_a b_ a");
		assertParseToHtml("<em>some text\\_a b</em> a", "_some text\\_a b_ a");
		assertParseToHtml("_<em>one</em>", "__one_");
	}

	@Test
	public void underscoreStrongEmphasis() {
		assertParseToHtml("__ one__", "__ one__");
		assertParseToHtml("__one __", "__one __");
		assertParseToHtml("__one", "__one");
		assertParseToHtml("a__one__", "a__one__");
		assertParseToHtml("__one__a", "__one__a");
		assertParseToHtml("<strong>s</strong>", "__s__");
		assertParseToHtml("<strong>some text</strong>", "__some text__");
		assertParseToHtml("<strong>some text</strong> and more", "__some text__ and more");
		assertParseToHtml("<strong>some\ntext</strong>", "__some\ntext__");
		assertParseToHtml("<strong>some text__a b</strong> a", "__some text__a b__ a");
	}
}
