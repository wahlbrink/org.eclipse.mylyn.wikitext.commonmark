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

package org.eclipse.mylyn.internal.wikitext.commonmark;

import static com.google.common.base.Predicates.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.builder.EventDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.BeginBlockEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.CharactersEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.DocumentBuilderEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.EndBlockEvent;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

public class SourceBlocksTest {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	private final SourceBlock block1 = mockBlock(BlockType.QUOTE, "b1");

	private final SourceBlock block2 = mockBlock(BlockType.PARAGRAPH, "b2");

	private final SourceBlocks sourceBlocks = new SourceBlocks(block1, block2);

	@Test
	public void requiresBlocks() {
		thrown.expect(NullPointerException.class);
		new SourceBlocks((SourceBlock[]) null);
	}

	@Test
	public void requiresBlocksCollection() {
		thrown.expect(NullPointerException.class);
		new SourceBlocks((List<SourceBlock>) null);
	}

	@Test
	public void canStart() {
		assertTrue(sourceBlocks.canStart(new LineSequence("any")));
		assertTrue(sourceBlocks.canStart(new LineSequence("")));
	}

	@Test
	public void process() {
		EventDocumentBuilder builder = new EventDocumentBuilder();
		sourceBlocks.process(ProcessingContext.empty(), builder, new LineSequence("one\nb2\nmore\n\nb1 and\n\n\nb2"));
		ImmutableList<DocumentBuilderEvent> expectedEvents = ImmutableList.of(//
				new BeginBlockEvent(BlockType.PARAGRAPH, new Attributes()),//
				new CharactersEvent("b2"),//
				new CharactersEvent("more"),//
				new EndBlockEvent(),//
				new BeginBlockEvent(BlockType.QUOTE, new Attributes()), //
				new CharactersEvent("b1 and"),//
				new EndBlockEvent(),//
				new BeginBlockEvent(BlockType.PARAGRAPH, new Attributes()),//
				new CharactersEvent("b2"),//
				new EndBlockEvent());
		assertEquals(Joiner.on("\n").join(builder.getDocumentBuilderEvents().getEvents()), expectedEvents,
				builder.getDocumentBuilderEvents().getEvents());
	}

	private SourceBlock mockBlock(final BlockType blockType, final String startString) {
		return new SourceBlock() {

			@Override
			public void process(ProcessingContext context, DocumentBuilder builder, LineSequence lineSequence) {
				builder.beginBlock(blockType, new Attributes());
				for (Line line : lineSequence.lines(not(LinePredicates.empty()))) {
					builder.characters(line.getText());
				}
				builder.endBlock();
			}

			@Override
			public boolean canStart(LineSequence lineSequence) {
				return lineSequence.getCurrentLine() != null
						&& lineSequence.getCurrentLine().getText().startsWith(startString);
			}
		};
	}
}
