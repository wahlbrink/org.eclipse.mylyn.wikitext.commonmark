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

import static com.google.common.base.Preconditions.checkState;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.internal.wikitext.commonmark.Line;
import org.eclipse.mylyn.internal.wikitext.commonmark.LinePredicates;
import org.eclipse.mylyn.internal.wikitext.commonmark.LineSequence;
import org.eclipse.mylyn.internal.wikitext.commonmark.ProcessingContext;
import org.eclipse.mylyn.internal.wikitext.commonmark.SourceBlock;
import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;

import com.google.common.base.Predicates;

public class IndentedCodeBlock extends SourceBlock {

	private final Pattern linePattern = Pattern.compile("(?:\\s{0,2}\t|(\\s{4}))(.*)");

	@Override
	public void process(ProcessingContext context, DocumentBuilder builder, LineSequence lineSequence) {
		builder.setLocator(lineSequence.getCurrentLine().toLocator());
		builder.beginBlock(BlockType.CODE, new Attributes());
		Iterator<Line> iterator = lineSequence.lines(
				Predicates.or(LinePredicates.matches(linePattern), LinePredicates.empty())).iterator();
		while (iterator.hasNext()) {
			Line line = iterator.next();
			Matcher matcher = linePattern.matcher(line.getText());
			if (!matcher.matches()) {
				checkState(line.isEmpty());
				if (iterator.hasNext()) {
					builder.characters("\n");
				}
			} else {
				builder.characters(matcher.group(2));
				builder.characters("\n");
			}
		}
		builder.endBlock();
	}

	@Override
	public boolean canStart(LineSequence lineSequence) {
		Line line = lineSequence.getCurrentLine();
		return line != null && linePattern.matcher(line.getText()).matches();
	}
}
