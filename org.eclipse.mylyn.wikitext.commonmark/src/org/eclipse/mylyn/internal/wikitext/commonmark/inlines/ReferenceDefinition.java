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
import org.eclipse.mylyn.internal.wikitext.commonmark.ProcessingContext;
import org.eclipse.mylyn.internal.wikitext.commonmark.ToStringHelper;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;

public class ReferenceDefinition extends InlineWithNestedContents {

	private final String href;

	private final String title;

	public ReferenceDefinition(Line line, int offset, int length, String href, String title, List<Inline> contents) {
		super(line, offset, length, contents);
		this.href = checkNotNull(href);
		this.title = title;
	}

	public String getHref() {
		return href;
	}

	@Override
	public void emit(DocumentBuilder builder) {
		// nothing to do
	}

	@Override
	public ProcessingContext createContext() {
		String name = InlineParser.toStringContent(getContents());
		return ProcessingContext.withReferenceDefinition(name, href, title);
	}

	@Override
	public int hashCode() {
		return Objects.hash(getOffset(), getLength(), getContents(), href, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		ReferenceDefinition other = (ReferenceDefinition) obj;
		return href.equals(other.href) && getContents().equals(other.getContents())
				&& Objects.equals(title, other.title);
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(ReferenceDefinition.class)
				.add("offset", getOffset())
				.add("length", getLength())
				.add("href", ToStringHelper.toStringValue(href))
				.add("title", title)
				.add("contents", getContents())
				.toString();
	}
}
