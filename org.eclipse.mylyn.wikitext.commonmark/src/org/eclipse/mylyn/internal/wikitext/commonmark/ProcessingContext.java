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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

public class ProcessingContext {

	private static final ProcessingContext EMPTY_CONTEXT = new ProcessingContext();

	public static class LinkDefinition {

		private final String name;

		private final String href;

		private final String title;

		public LinkDefinition(String name, String href, String title) {
			this.name = name;
			this.href = href;
			this.title = title;
		}

		public String getName() {
			return name;
		}

		public String getHref() {
			return href;
		}

		public String getTitle() {
			return title;
		}
	}

	private final ImmutableMap<String, LinkDefinition> links;

	private ProcessingContext(ImmutableMap<String, LinkDefinition> links) {
		this.links = checkNotNull(links);
	}

	public ProcessingContext() {
		this(ImmutableMap.<String, LinkDefinition> of());
	}

	public static ProcessingContext empty() {
		return EMPTY_CONTEXT;
	}

	public static ProcessingContext withReferenceDefinition(String name, String href, String title) {
		if (Strings.isNullOrEmpty(name)) {
			return empty();
		}
		return new ProcessingContext(ImmutableMap.of(name.toLowerCase(Locale.ROOT), new LinkDefinition(name, href,
				title)));
	}

	public boolean isEmpty() {
		return links.isEmpty();
	}

	public ProcessingContext merge(ProcessingContext other) {
		checkNotNull(other);
		if (other.isEmpty()) {
			return this;
		} else if (isEmpty()) {
			return other;
		}
		Map<String, LinkDefinition> mergedLinks = new HashMap<>();
		mergedLinks.putAll(other.links);
		mergedLinks.putAll(links);
		return new ProcessingContext(ImmutableMap.copyOf(mergedLinks));
	}

	public LinkDefinition link(String name) {
		return links.get(name.toLowerCase());
	}

}
