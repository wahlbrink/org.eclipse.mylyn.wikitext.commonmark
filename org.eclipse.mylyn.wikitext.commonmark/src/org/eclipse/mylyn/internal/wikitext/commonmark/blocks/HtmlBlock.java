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

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.internal.wikitext.commonmark.Line;
import org.eclipse.mylyn.internal.wikitext.commonmark.LineSequence;
import org.eclipse.mylyn.internal.wikitext.commonmark.ProcessingContext;
import org.eclipse.mylyn.internal.wikitext.commonmark.SourceBlock;
import org.eclipse.mylyn.internal.wikitext.commonmark.TextSegment;
import org.eclipse.mylyn.internal.wikitext.commonmark.inlines.Cursor;
import org.eclipse.mylyn.internal.wikitext.commonmark.inlines.HtmlTagSpan;
import org.eclipse.mylyn.internal.wikitext.commonmark.inlines.Inline;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;

import com.google.common.base.Optional;

public class HtmlBlock extends SourceBlock {

	private final Pattern startPattern = Pattern.compile("\\ {0,3}(<).*");

	@Override
	public void process(ProcessingContext context, DocumentBuilder builder, LineSequence lineSequence) {
		for (Line line = lineSequence.getCurrentLine(); line != null && !line.isEmpty(); lineSequence.advance(), line = lineSequence.getCurrentLine()) {
			builder.charactersUnescaped(line.getText());
			builder.charactersUnescaped("\n");
		}
	}

	@Override
	public boolean canStart(LineSequence lineSequence) {
		Line line = lineSequence.getCurrentLine();
		if (line != null) {
			Matcher matcher = startPattern.matcher(line.getText());
			if (matcher.matches()) {
				Cursor cursor = new Cursor(createCandidateTextSegment(line));
				cursor.advance(matcher.start(1));
				HtmlTagSpan htmlTagSpan = new HtmlTagSpan();
				Optional<? extends Inline> inline = htmlTagSpan.createInline(cursor);
				return inline.isPresent();
			}
		}
		return false;
	}

	public TextSegment createCandidateTextSegment(Line line) {
		return new TextSegment(Collections.singletonList(line));
	}

}
