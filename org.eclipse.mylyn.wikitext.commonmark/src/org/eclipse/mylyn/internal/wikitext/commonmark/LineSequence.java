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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.StringReader;

import org.eclipse.mylyn.wikitext.core.util.LocationTrackingReader;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;

public class LineSequence {

	private final LocationTrackingReader reader;

	private Line currentLine;

	private Line nextLine;

	public LineSequence(String content) {
		this.reader = new LocationTrackingReader(new StringReader(checkNotNull(content)));
		currentLine = readLine();
	}

	private Line readLine() {
		String text;
		try {
			text = reader.readLine();
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
		if (text == null) {
			return null;
		}
		return new Line(reader.getLineNumber(), reader.getLineOffset(), text);
	}

	public Line getCurrentLine() {
		return currentLine;
	}

	public Line getNextLine() {
		if (nextLine == null) {
			nextLine = readLine();
		}
		return nextLine;
	}

	public void advance() {
		currentLine = getNextLine();
		nextLine = null;
	}

	public Iterable<Line> lines(Predicate<Line> predicate) {
		return new LinesIterable(this, predicate);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(LineSequence.class)
				.add("currentLine", getCurrentLine())
				.add("nextLine", getNextLine())
				.toString();
	}

}
