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

import java.util.Objects;

import org.eclipse.mylyn.internal.wikitext.commonmark.Line;

abstract class InlineWithText extends Inline {

	protected final String text;

	public InlineWithText(Line line, int offset, int length, String text) {
		super(line, offset, length);
		this.text = checkNotNull(text);
	}

	public String getText() {
		return text;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getOffset(), getLength(), text);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		InlineWithText other = (InlineWithText) obj;
		return text.equals(other.text);
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(getClass())
				.add("offset", getOffset())
				.add("length", getLength())
				.add("text", getText())
				.toString();
	}
}
