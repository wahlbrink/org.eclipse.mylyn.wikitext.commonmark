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

public class FencedCodeBlock extends SourceBlock {

	private final Pattern openingFencePattern = Pattern.compile("(\\s{0,4})(`{3,}|~{3,})\\s*(?:([^\\s~`]+)[^~`]*)?");

	@Override
	public void process(ProcessingContext context, DocumentBuilder builder, LineSequence lineSequence) {
		Matcher matcher = openingFencePattern.matcher(lineSequence.getCurrentLine().getText());
		checkState(matcher.matches());
		String indent = matcher.group(1);
		boolean indentedCodeBlock = indent != null && indent.length() == 4;
		Pattern closingFencePattern = closingFencePattern(matcher);

		Attributes codeAttributes = new Attributes();
		addInfoTextCssClass(codeAttributes, matcher);

		builder.setLocator(lineSequence.getCurrentLine().toLocator());
		builder.beginBlock(BlockType.CODE, codeAttributes);

		if (indentedCodeBlock) {
			outputLine(builder, indent, lineSequence.getCurrentLine());
		}
		lineSequence.advance();
		for (Line line : lineSequence.lines(Predicates.not(LinePredicates.matches(closingFencePattern)))) {
			outputLine(builder, indent, line);
		}
		if (indentedCodeBlock && lineSequence.getCurrentLine() != null) {
			outputLine(builder, indent, lineSequence.getCurrentLine());
		}
		lineSequence.advance();

		builder.endBlock();
	}

	private void outputLine(DocumentBuilder builder, String indent, Line line) {
		String text = line.getText();
		text = removeIndent(indent, text);
		builder.characters(text);
		builder.characters("\n");
	}

	private String removeIndent(String indent, String text) {
		if (indent != null && indent.length() > 0) {
			Pattern indentPattern = Pattern.compile("\\s{1," + indent.length() + "}(.*)");
			Matcher matcher = indentPattern.matcher(text);
			if (matcher.matches()) {
				return matcher.group(1);
			}
		}
		return text;
	}

	private Pattern closingFencePattern(Matcher matcher) {
		String fence = matcher.group(2);
		char fenceDelimiter = fence.charAt(0);
		return Pattern.compile("\\s{0,3}" + fenceDelimiter + "{" + fence.length() + ",}\\s*");
	}

	private void addInfoTextCssClass(Attributes codeAttributes, Matcher matcher) {
		String infoText = matcher.group(3);
		if (infoText != null) {
			codeAttributes.setCssClass("language-" + infoText);
		}
	}

	@Override
	public boolean canStart(LineSequence lineSequence) {
		Line line = lineSequence.getCurrentLine();
		return line != null && openingFencePattern.matcher(line.getText()).matches();
	}

}
