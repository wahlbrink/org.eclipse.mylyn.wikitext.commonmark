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

package org.eclipse.mylyn.internal.wikitext.commonmark.spec;

import static org.eclipse.mylyn.internal.wikitext.commonmark.CommonMarkAsserts.assertContent;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.util.LocationTrackingReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.CharStreams;
import com.google.common.io.Resources;

@RunWith(Parameterized.class)
public class CommonMarkSpecTests {

	private static final URI COMMONMARK_SPEC_URI = URI.create("https://raw.githubusercontent.com/jgm/CommonMark/b5e205fba226b3d8d94418e050022c48a37997f8/spec.txt");

	private static final Set<String> HEADING_EXCLUSIONS = ImmutableSet.of("Block quotes", "HTML blocks", "Lists",
			"List items");

	private static final Set<Integer> LINE_EXCLUSIONS = ImmutableSet.of(256, // Tab expansion
			263, // Tab expansion
			287, // Precedence
			451, // Horizontal rules
			495, // Horizontal rules
			511, // Horizontal rules
			886, // Setext headers
			896, // Setext headers
			962, // Setext headers
			981, // Setext headers
			1876, // Link reference definitions
			2009, // Link reference definitions
			2023, // Link reference definitions
			2057, // Link reference definitions
			4243, // Backslash escapes
			4334, // Entities
			4340, // Entities
			4348, // Entities
			4517, // Code spans
			4739, // Emphasis and strong emphasis
			4747, // Emphasis and strong emphasis
			4807, // Emphasis and strong emphasis
			4825, // Emphasis and strong emphasis
			4853, // Emphasis and strong emphasis
			4867, // Emphasis and strong emphasis
			4876, // Emphasis and strong emphasis
			4927, // Emphasis and strong emphasis
			4960, // Emphasis and strong emphasis
			5023, // Emphasis and strong emphasis
			5052, // Emphasis and strong emphasis
			5061, // Emphasis and strong emphasis
			5067, // Emphasis and strong emphasis
			5075, // Emphasis and strong emphasis
			5126, // Emphasis and strong emphasis
			5842, // Links
			5872, // Links
			5929, // Links
			5935, // Links
			5981, // Links
			6059, // Links
			6073, // Links
			6081, // Links
			6091, // Links
			6099, // Links
			6117, // Links
			6131, // Links
			6139, // Links
			6176, // Links
			6186, // Links
			6197, // Links
			6208, // Links
			6216, // Links
			6228, // Links
			6242, // Links
			6280, // Links
			6297, // Links
			6305, // Links
			6315, // Links
			6327, // Links
			6421, // Links
			6433, // Links
			6444, // Links
			6456, // Links
			6484, // Images
			6511, // Images
			6519, // Images
			6553, // Images
			6561, // Images
			6571, // Images
			6579, // Images
			6589, // Images
			6600, // Images
			6611, // Images
			6619, // Images
			6640, // Images
			6751, // Autolinks
			7022 // Raw HTML
	);

	public static class Expectation {

		final String input;

		final String expected;

		public Expectation(String input, String expected) {
			this.input = input;
			this.expected = expected;
		}

		@Override
		public String toString() {
			return Objects.toStringHelper(Expectation.class).add("input", input).add("expected", expected).toString();
		}
	}

	private final Expectation expectation;

	private final String heading;

	private final int lineNumber;

	@Before
	public void preconditions() {
		assumeFalse(HEADING_EXCLUSIONS.contains(heading));
		assumeFalse(LINE_EXCLUSIONS.contains(Integer.valueOf(lineNumber)));
	}

	@Test
	public void test() {
		try {
			assertContent(expectation.expected, expectation.input);
		} catch (AssertionError e) {
			System.out.println(lineNumber + ", // " + heading);
			System.out.flush();
			throw e;
		}
	}

	@Parameters(name = "{0} test {index}")
	public static List<Object[]> parameters() {
		ImmutableList.Builder<Object[]> parameters = ImmutableList.builder();

		loadSpec(parameters);

		return parameters.build();
	}

	public CommonMarkSpecTests(String title, String heading, int lineNumber, Expectation expectation) {
		this.heading = heading;
		this.lineNumber = lineNumber;
		this.expectation = expectation;
	}

	private static void loadSpec(ImmutableList.Builder<Object[]> parameters) {
		Pattern headingPattern = Pattern.compile("#+\\s*(.+)");
		try {
			String spec = loadCommonMarkSpec();
			LocationTrackingReader reader = new LocationTrackingReader(new StringReader(spec));
			String heading = "unspecified";
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.replace('→', '\t');
				if (line.trim().equals(".")) {
					int testLineNumber = reader.getLineNumber();
					Expectation expectation = readExpectation(reader);
					parameters.add(new Object[] { heading + ":line " + testLineNumber, heading, testLineNumber,
							expectation });
				}
				Matcher headingMatcher = headingPattern.matcher(line);
				if (headingMatcher.matches()) {
					heading = headingMatcher.group(1);
				}
			}
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	private static Expectation readExpectation(LocationTrackingReader reader) throws IOException {
		String input = readUntilDelimiter(reader);
		String expected = readUntilDelimiter(reader);
		return new Expectation(input, expected);
	}

	private static String readUntilDelimiter(LocationTrackingReader reader) throws IOException {
		List<String> lines = new ArrayList<>();
		String line;
		while ((line = reader.readLine()) != null) {
			line = line.replace('→', '\t');
			if (line.trim().equals(".")) {
				break;
			}
			lines.add(line);
		}
		return Joiner.on("\n").join(lines);
	}

	private static String loadCommonMarkSpec() throws IOException {
		File tmpFolder = new File("./tmp");
		if (!tmpFolder.exists()) {
			tmpFolder.mkdir();
		}
		assertTrue(tmpFolder.getAbsolutePath(), tmpFolder.exists());
		File spec = new File(tmpFolder, "spec.txt");
		if (!spec.exists()) {
			try (FileOutputStream out = new FileOutputStream(spec)) {
				Resources.copy(COMMONMARK_SPEC_URI.toURL(), out);
			}
		}
		try (InputStream in = new FileInputStream(spec)) {
			return CharStreams.toString(new InputStreamReader(in, StandardCharsets.UTF_8));
		}
	}

}
