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

import java.util.List;

import org.eclipse.mylyn.internal.wikitext.commonmark.Line;
import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;

public class Strong extends InlineWithNestedContents {

	public Strong(Line line, int offset, int length, List<Inline> contents) {
		super(line, offset, length, contents);
	}

	@Override
	public void emit(DocumentBuilder builder) {
		builder.beginSpan(SpanType.STRONG, new Attributes());
		InlineParser.emit(builder, getContents());
		builder.endSpan();
	}

}
