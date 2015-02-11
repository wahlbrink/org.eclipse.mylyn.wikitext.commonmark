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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.internal.wikitext.commonmark.Line;
import org.eclipse.mylyn.internal.wikitext.commonmark.ProcessingContext;
import org.eclipse.mylyn.internal.wikitext.commonmark.ProcessingContext.LinkDefinition;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;

import com.google.common.base.CharMatcher;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.net.UrlEscapers;

public class PotentialBracketEndDelimiter extends InlineWithText {

	static final String ESCAPABLE_CHARACTER_GROUP = "[!\"\\\\#$%&'()*+,./:;<=>?@\\[\\]^_`{|}~-]";

	static final String ESCAPED_CHARS = "(?:\\\\" + ESCAPABLE_CHARACTER_GROUP + ")";

	static final String CAPTURING_ESCAPED_CHARS = "\\\\(" + ESCAPABLE_CHARACTER_GROUP + ")";

	static final String PARENS_TITLE_PART = "(?:\\(((?:" + ESCAPED_CHARS + "|[^\\)])*)\\))";

	static final String SINGLE_QUOTED_TITLE_PART = "(?:'((?:" + ESCAPED_CHARS + "|[^'])*)')";

	static final String QUOTED_TITLE_PART = "(?:\"((?:" + ESCAPED_CHARS + "|[^\"])*)\")";

	static final String BRACKET_URI_PART = "<((?:[^<>\\\\\r\n]|" + ESCAPED_CHARS + ")*?)>";

	private static final String IN_PARENS = "\\((?:[^\\\\()]|" + ESCAPED_CHARS + ")*\\)";

	static final String NOBRACKET_URI_PART = "((?:[^\\\\\\s()]|" + ESCAPED_CHARS + "|" + IN_PARENS + ")+)";

	static final String URI_PART = "(?:" + BRACKET_URI_PART + "|" + NOBRACKET_URI_PART + ")";

	static final String TITLE_PART = "(?:" + QUOTED_TITLE_PART + "|" + SINGLE_QUOTED_TITLE_PART + "|"
			+ PARENS_TITLE_PART + ")";

	final Pattern endPattern = Pattern.compile("\\(\\s*" + URI_PART + "?(?:\\s+" + TITLE_PART + ")?\\s*\\)(.*)",
			Pattern.DOTALL);

	final Pattern referenceDefinitionEndPattern = Pattern.compile(":\\s*" + URI_PART + "?(?:\\s+" + TITLE_PART
			+ ")?\\s*(.*)", Pattern.DOTALL);

	public PotentialBracketEndDelimiter(Line line, int offset) {
		super(line, offset, 1, "]");
	}

	@Override
	public void emit(DocumentBuilder builder) {
		builder.characters(text);
	}

	@Override
	public void apply(ProcessingContext context, List<Inline> inlines, Cursor cursor) {
		Optional<PotentialBracketDelimiter> previousDelimiter = findLastPotentialBracketDelimiter(inlines);
		if (previousDelimiter.isPresent()) {
			PotentialBracketDelimiter openingDelimiter = previousDelimiter.get();
			int indexOfOpeningDelimiter = inlines.indexOf(openingDelimiter);

			boolean referenceDefinition = cursor.hasNext() && cursor.getNext() == ':'
					&& eligibleForReferenceDefinition(openingDelimiter, cursor);
			Matcher matcher = cursor.hasNext() ? cursor.matcher(1, referenceDefinition
					? referenceDefinitionEndPattern
					: endPattern) : null;

			List<Inline> contents = InlineParser.secondPass(inlines.subList(indexOfOpeningDelimiter + 1, inlines.size()));
			if (!cursor.hasNext() || !checkNotNull(matcher).matches()) {
				String name = InlineParser.toStringContent(contents);
				LinkDefinition linkDefinition = context.link(name);
				if (linkDefinition != null) {
					cursor.advance();
					truncate(inlines, indexOfOpeningDelimiter);
					int length = getOffset() - openingDelimiter.getOffset();
					inlines.add(new Link(openingDelimiter.getLine(), openingDelimiter.getOffset(), length,
							linkDefinition.getHref(), linkDefinition.getTitle(), contents));
					return;
				}
			} else {
				String uri = linkUri(matcher);
				String title = linkTitle(matcher);

				if (!(referenceDefinition && (Strings.isNullOrEmpty(uri) || hasContentAfterTitleOnSameLine(matcher,
						cursor)))) {
					int closingLength = matcher.start(6) - matcher.start() + 1;
					cursor.advance(closingLength);
					int length = getOffset() - openingDelimiter.getOffset() + closingLength;
					truncate(inlines, indexOfOpeningDelimiter);
					if (referenceDefinition) {
						truncatePrecedingWhitespace(inlines, 3);
						inlines.add(new ReferenceDefinition(openingDelimiter.getLine(), openingDelimiter.getOffset(),
								length, uri, title, contents));
					} else if (openingDelimiter.isImageDelimiter()) {
						inlines.add(new Image(openingDelimiter.getLine(), openingDelimiter.getOffset(), length, uri,
								title, contents));
					} else {
						inlines.add(new Link(openingDelimiter.getLine(), openingDelimiter.getOffset(), length, uri,
								title, contents));
					}
					return;
				}
			}
		}
		applyCharacters(context, inlines, cursor);
	}

	private boolean hasContentAfterTitleOnSameLine(Matcher matcher, Cursor cursor) {
		int indexOfContent = matcher.start(6);
		if (indexOfContent == -1 || matcher.end(6) == indexOfContent) {
			return false;
		}
		int endOfTitle = titleEndIndex(matcher);
		if (endOfTitle > 0) {
			for (int x = endOfTitle; x < indexOfContent; ++x) {
				char c = cursor.getChar(x);
				if (c == '\n') {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private void truncatePrecedingWhitespace(List<Inline> inlines, int length) {
		if (!inlines.isEmpty()) {
			Inline last = inlines.get(inlines.size() - 1);
			if (last instanceof Characters) {
				Characters characters = (Characters) last;
				if (characters.getText().length() <= length
						&& CharMatcher.WHITESPACE.matchesAllOf(characters.getText())) {
					inlines.remove(inlines.size() - 1);
				}
			}
		}
	}

	public void truncate(List<Inline> inlines, int indexOfOpeningDelimiter) {
		while (inlines.size() > indexOfOpeningDelimiter) {
			inlines.remove(indexOfOpeningDelimiter);
		}
	}

	boolean eligibleForReferenceDefinition(PotentialBracketDelimiter openingDelimiter, Cursor cursor) {
		boolean linkDelimiter = openingDelimiter.isLinkDelimiter();
		if (!linkDelimiter) {
			return false;
		}
		int cursorRelativeOffset = cursor.toCursorOffset(openingDelimiter.getOffset());
		for (int x = cursorRelativeOffset - 1; x >= 0; --x) {
			char c = cursor.getChar(x);
			if (c == '\n') {
				return true;
			} else if (c != ' ') {
				return false;
			}
			if (cursorRelativeOffset - x == 4) {
				return false;
			}
		}
		return true;
	}

	private void applyCharacters(ProcessingContext context, List<Inline> inlines, Cursor cursor) {
		new Characters(getLine(), getOffset(), getText()).apply(context, inlines, cursor);
	}

	private String linkTitle(Matcher matcher) {
		String title = matcher.group(3);
		if (title == null) {
			title = matcher.group(4);
			if (title == null) {
				title = matcher.group(5);
				if (title == null) {
					title = "";
				}
			}
		}
		return unescapeBackslashEscapes(title);
	}

	private int titleEndIndex(Matcher matcher) {
		int index = matcher.end(3);
		if (index == -1) {
			index = matcher.end(4);
			if (index == -1) {
				index = matcher.end(5);
			}
		}
		return index + 1;
	}

	private String linkUri(Matcher matcher) {
		String uriWithEscapes = matcher.group(1);
		if (uriWithEscapes == null) {
			uriWithEscapes = matcher.group(2);
		}
		uriWithEscapes = Objects.firstNonNull(uriWithEscapes, "");
		String uriWithoutBackslashEscapes = unescapeBackslashEscapes(uriWithEscapes);

		return UrlEscapers.urlFragmentEscaper().escape(uriWithoutBackslashEscapes);
	}

	String unescapeBackslashEscapes(String stringWithBackslashEscapes) {
		return stringWithBackslashEscapes.replaceAll(CAPTURING_ESCAPED_CHARS, "$1");
	}

	private Optional<PotentialBracketDelimiter> findLastPotentialBracketDelimiter(List<Inline> inlines) {
		boolean seenLink = false;
		for (int x = inlines.size() - 1; x >= 0; --x) {
			Inline inline = inlines.get(x);
			if (inline instanceof PotentialBracketDelimiter) {
				PotentialBracketDelimiter delimiter = (PotentialBracketDelimiter) inline;
				if ((delimiter.isLinkDelimiter() && !seenLink) || delimiter.isImageDelimiter()) {
					return Optional.of(delimiter);
				}
			}
			if (inline instanceof Link) {
				seenLink = true;
			}
		}
		return Optional.absent();
	}
}
