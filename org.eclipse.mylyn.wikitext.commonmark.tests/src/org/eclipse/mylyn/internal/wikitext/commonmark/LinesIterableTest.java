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
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class LinesIterableTest {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Test
	public void requiresLineSequence() {
		thrown.expect(NullPointerException.class);
		new LinesIterable(null, Predicates.<Line> alwaysTrue());
	}

	@Test
	public void requiresPredicate() {
		thrown.expect(NullPointerException.class);
		new LinesIterable(new LineSequence(""), null);
	}

	@Test
	public void iteratorWithNoLines() {
		LinesIterable iterable = new LinesIterable(new LineSequence(""),
				Predicates.<Line> alwaysTrue());
		Iterator<Line> iterator = iterable.iterator();
		assertNotNull(iterator);
		assertFalse(iterator.hasNext());
		thrown.expect(NoSuchElementException.class);
		iterator.next();
	}

	@Test
	public void iteratorAdvances() {
		LinesIterable iterable = new LinesIterable(
				new LineSequence("one\ntwo"), Predicates.<Line> alwaysTrue());
		Iterator<Line> iterator = iterable.iterator();
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		Line next = iterator.next();
		assertNotNull(next);
		assertEquals("one", next.getText());
		Line two = iterator.next();
		assertNotNull(two);
		assertEquals("two", two.getText());
	}

	@Test
	public void iteratorRemove() {
		LinesIterable iterable = new LinesIterable(new LineSequence("one"),
				Predicates.<Line> alwaysTrue());
		Iterator<Line> iterator = iterable.iterator();
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		Line next = iterator.next();
		assertNotNull(next);
		assertEquals("one", next.getText());
		thrown.expect(UnsupportedOperationException.class);
		iterator.remove();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void hasNext() {
		Predicate<Line> predicate = mock(Predicate.class);
		LineSequence lineSequence = new LineSequence("one");
		Iterator<Line> iterator = new LinesIterable(lineSequence, predicate)
				.iterator();
		assertFalse(iterator.hasNext());
		doReturn(true).when(predicate).apply(any(Line.class));
		assertTrue(iterator.hasNext());
		lineSequence.advance();
		assertFalse(iterator.hasNext());
	}
}
