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
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

class PotentialEmphasisDelimiter extends InlineWithText {

	private final boolean canOpen;

	private final boolean canClose;

	public PotentialEmphasisDelimiter(Line line, int offset, int length, String text, boolean canOpen, boolean canClose) {
		super(line, offset, length, text);
		this.canOpen = canOpen;
		this.canClose = canClose;
	}

	@Override
	public void emit(DocumentBuilder builder) {
		builder.characters(text);
	}

	@Override
	Optional<InlinesSubstitution> secondPass(List<Inline> inlines) {
		if (!canClose) {
			return Optional.absent();
		}
		int indexOfThis = inlines.indexOf(this);
		Optional<PotentialEmphasisDelimiter> opener = previousOpener(inlines, indexOfThis);
		if (opener.isPresent()) {
			PotentialEmphasisDelimiter openingDelimiter = opener.get();
			int delimiterSize = delimiterSize(openingDelimiter);
			int indexOfOpeningDelimiter = inlines.indexOf(openingDelimiter);

			List<Inline> contents = InlineParser.secondPass(inlines.subList(indexOfOpeningDelimiter + 1, indexOfThis));

			int spanOffset = openingDelimiter.getOffset();
			int spanLength = getOffset() + getLength() - openingDelimiter.getOffset();
			Inline emphasis = createEmphasis(openingDelimiter.getLine(), spanOffset, spanLength, delimiterSize,
					contents);

			ImmutableList.Builder<Inline> substitutionInlines = ImmutableList.builder();
			if (delimiterSize < openingDelimiter.getLength()) {
				substitutionInlines.add(createPotentialOpeningDelimiter(openingDelimiter, delimiterSize));
			}
			substitutionInlines.add(emphasis);
			if (delimiterSize < getLength()) {
				substitutionInlines.add(createPotentialClosingDelimiter(delimiterSize));
			}

			return Optional.of(new InlinesSubstitution(openingDelimiter, this, substitutionInlines.build()));
		}
		return Optional.absent();
	}

	private Inline createPotentialClosingDelimiter(int delimiterSize) {
		return new PotentialEmphasisDelimiter(getLine(), getOffset() + delimiterSize, getLength() - delimiterSize,
				getText().substring(0 + delimiterSize, getText().length()), canOpen, canClose);
	}

	private Inline createEmphasis(Line line, int offset, int length, int delimiterSize, List<Inline> contents) {
		if (delimiterSize == 1) {
			return new Emphasis(line, offset, length, contents);
		}
		return new Strong(line, offset, length, contents);
	}

	private PotentialEmphasisDelimiter createPotentialOpeningDelimiter(PotentialEmphasisDelimiter openingDelimiter,
			int consumedSize) {
		return new PotentialEmphasisDelimiter(openingDelimiter.getLine(), openingDelimiter.getOffset(),
				openingDelimiter.getLength() - consumedSize, openingDelimiter.getText().substring(0,
						openingDelimiter.getText().length() - consumedSize), openingDelimiter.canOpen,
				openingDelimiter.canClose);
	}

	private int delimiterSize(PotentialEmphasisDelimiter openingDelimiter) {
		int openingLength = openingDelimiter.getLength();
		if (openingLength < 3 || getLength() < 3) {
			return openingLength > getLength() ? getLength() : openingLength;
		}
		return getLength() % 2 == 0 ? 2 : 1;
	}

	private Optional<PotentialEmphasisDelimiter> previousOpener(List<Inline> inlines, int indexOfThis) {
		char c = getText().charAt(0);
		for (int index = indexOfThis - 1; index >= 0; --index) {
			Inline inline = inlines.get(index);
			if (inline instanceof PotentialEmphasisDelimiter) {
				PotentialEmphasisDelimiter previousDelimiter = (PotentialEmphasisDelimiter) inline;
				if (previousDelimiter.canOpen && previousDelimiter.getText().charAt(0) == c) {
					return Optional.of(previousDelimiter);
				}
			}
		}
		return Optional.absent();
	}
}
