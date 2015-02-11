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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Objects;

import org.eclipse.mylyn.internal.wikitext.commonmark.Line;
import org.eclipse.mylyn.internal.wikitext.commonmark.ToStringHelper;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.ImageAttributes;

public class Image extends InlineWithNestedContents {

	private final String src;

	private final String title;

	public Image(Line line, int offset, int length, String src, String title, List<Inline> contents) {
		super(line, offset, length, contents);
		this.src = checkNotNull(src);
		this.title = title;
	}

	public String getHref() {
		return src;
	}

	@Override
	public void emit(DocumentBuilder builder) {
		ImageAttributes attributes = new ImageAttributes();
		attributes.setTitle(title);

		List<Inline> contents = getContents();
		if (!contents.isEmpty()) {
			attributes.setAlt(InlineParser.toStringContent(contents));
		}

		builder.image(attributes, src);
	}

	@Override
	public int hashCode() {
		return Objects.hash(getOffset(), getLength(), getContents(), src, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		Image other = (Image) obj;
		return src.equals(other.src) && getContents().equals(other.getContents()) && Objects.equals(title, other.title);
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(Image.class)
				.add("offset", getOffset())
				.add("length", getLength())
				.add("src", ToStringHelper.toStringValue(src))
				.add("title", title)
				.add("contents", getContents())
				.toString();
	}
}
