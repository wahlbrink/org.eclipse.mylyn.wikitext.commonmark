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

import static com.google.common.base.Predicates.not;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.mylyn.internal.wikitext.commonmark.CommonMark;
import org.eclipse.mylyn.internal.wikitext.commonmark.Line;
import org.eclipse.mylyn.internal.wikitext.commonmark.LinePredicates;
import org.eclipse.mylyn.internal.wikitext.commonmark.LineSequence;
import org.eclipse.mylyn.internal.wikitext.commonmark.ProcessingContext;
import org.eclipse.mylyn.internal.wikitext.commonmark.SourceBlock;
import org.eclipse.mylyn.internal.wikitext.commonmark.TextSegment;
import org.eclipse.mylyn.internal.wikitext.commonmark.inlines.Inline;
import org.eclipse.mylyn.internal.wikitext.commonmark.inlines.InlineParser;
import org.eclipse.mylyn.internal.wikitext.commonmark.inlines.ReferenceDefinition;
import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;

import com.google.common.collect.ImmutableSet;

public class ParagraphBlock extends SourceBlock {

	private final Set<Class<? extends SourceBlock>> INTERRUPTION_EXCLUSIONS = ImmutableSet.of(IndentedCodeBlock.class,
			SetextHeaderBlock.class);

	@Override
	public ProcessingContext createContext(LineSequence lineSequence) {
		TextSegment textSegment = extractTextSegment(lineSequence);
		return new InlineContent().createContext(textSegment);
	}

	@Override
	public void process(ProcessingContext context, DocumentBuilder builder, LineSequence lineSequence) {
		TextSegment textSegment = extractTextSegment(lineSequence);
		InlineContent inlineContent = new InlineContent();
		List<Inline> inlines = inlineContent.parse(context, textSegment);
		if (!emptyParagraph(inlines)) {
			builder.setLocator(textSegment.getLines().get(0).toLocator());
			builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
			InlineParser.emit(builder, inlines);
			builder.endBlock();
		}
	}

	private boolean emptyParagraph(List<Inline> inlines) {
		for (Inline inline : inlines) {
			if (!(inline instanceof ReferenceDefinition)) {
				return false;
			}
		}
		return true;
	}

	private TextSegment extractTextSegment(LineSequence lineSequence) {
		List<Line> lines = new ArrayList<Line>();
		while (lineSequence.getCurrentLine() != null && notEmptyLine(lineSequence) && !anotherBlockStart(lineSequence)) {
			lines.add(lineSequence.getCurrentLine());
			lineSequence.advance();
		}
		TextSegment textSegment = new TextSegment(lines);
		return textSegment;
	}

	private boolean notEmptyLine(LineSequence lineSequence) {
		return not(LinePredicates.empty()).apply(lineSequence.getCurrentLine());
	}

	private boolean anotherBlockStart(LineSequence lineSequence) {
		SourceBlock block = CommonMark.sourceBlocks().selectBlock(lineSequence);
		if (block != null && !ParagraphBlock.class.isAssignableFrom(block.getClass())
				&& !INTERRUPTION_EXCLUSIONS.contains(block.getClass())) {
			return true;
		}
		return false;
	}

	@Override
	public boolean canStart(LineSequence lineSequence) {
		Line line = lineSequence.getCurrentLine();
		return line != null && !line.isEmpty();
	}

}
