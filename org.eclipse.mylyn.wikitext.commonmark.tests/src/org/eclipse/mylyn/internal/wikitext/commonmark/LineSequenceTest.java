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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;

public class LineSequenceTest {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Test
	public void requiresContent() {
		thrown.expect(NullPointerException.class);
		new LineSequence(null);
	}

	@Test
	public void empty() {
		LineSequence lineSequence = new LineSequence("");
		assertNoLinesRemain(lineSequence);
	}

	@Test
	public void oneLine() {
		LineSequence lineSequence = new LineSequence("a");
		Line currentLine = lineSequence.getCurrentLine();
		assertNotNull(currentLine);
		assertEquals("a", currentLine.getText());
		assertSame(currentLine, lineSequence.getCurrentLine());
		assertNull(lineSequence.getNextLine());
		lineSequence.advance();
		assertNoLinesRemain(lineSequence);
	}

	@Test
	public void twoLines() {
		LineSequence lineSequence = new LineSequence("abc\r\ndefg");

		Line currentLine = lineSequence.getCurrentLine();
		assertNotNull(currentLine);
		assertEquals("abc", currentLine.getText());
		assertEquals(0, currentLine.getLineOffset());
		assertEquals(0, currentLine.getLineNumber());
		assertSame(currentLine, lineSequence.getCurrentLine());
		Line nextLine = lineSequence.getNextLine();
		assertNotNull(nextLine);
		assertEquals("defg", nextLine.getText());
		assertEquals(5, nextLine.getLineOffset());
		assertEquals(1, nextLine.getLineNumber());
		assertSame(nextLine, lineSequence.getNextLine());

		lineSequence.advance();

		assertNotSame(currentLine, lineSequence.getCurrentLine());
		assertNotNull(lineSequence.getCurrentLine());
		assertEquals("defg", lineSequence.getCurrentLine().getText());
		assertNull(lineSequence.getNextLine());

		lineSequence.advance();

		assertNoLinesRemain(lineSequence);
	}

	@Test
	public void toStringTest() {
		assertEquals("LineSequence{currentLine=Line{lineNumber=0, lineOffset=0, text=a}, nextLine=null}",
				new LineSequence("a\n").toString());
		assertEquals(
				"LineSequence{currentLine=Line{lineNumber=0, lineOffset=0, text=a}, nextLine=Line{lineNumber=1, lineOffset=2, text=b}}",
				new LineSequence("a\nb").toString());
	}

	@Test
	public void lines() {
		assertFalse(new LineSequence("").lines(Predicates.<Line> alwaysTrue()).iterator().hasNext());
		assertFalse(new LineSequence("a").lines(Predicates.<Line> alwaysFalse()).iterator().hasNext());

		List<String> strings = new ArrayList<>();
		for (Line line : new LineSequence("a\nb\nc\na").lines(new Predicate<Line>() {

			@Override
			public boolean apply(Line input) {
				return !input.getText().equals("c");
			}
		})) {
			strings.add(line.getText());
		}
		assertEquals(ImmutableList.of("a", "b"), strings);
	}

	@Test
	public void advance() {
		LineSequence lineSequence = new LineSequence("one");
		lineSequence.advance();
		assertNoLinesRemain(lineSequence);
		lineSequence.advance();
		assertNoLinesRemain(lineSequence);
		lineSequence.advance();
		assertNoLinesRemain(lineSequence);
	}

	private void assertNoLinesRemain(LineSequence lineSequence) {
		assertNull(lineSequence.getCurrentLine());
		assertNull(lineSequence.getNextLine());
	}

}
