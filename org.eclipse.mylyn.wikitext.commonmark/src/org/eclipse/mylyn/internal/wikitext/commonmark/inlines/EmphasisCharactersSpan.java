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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Optional;

public class EmphasisCharactersSpan extends SourceSpan {

	private final Pattern pattern = Pattern.compile("(_+|`+|\\*+).*", Pattern.DOTALL);

	@Override
	public Optional<? extends Inline> createInline(Cursor cursor) {
		Matcher matcher = cursor.matcher(pattern);
		if (matcher.matches()) {
			String group = matcher.group(1);
			return Optional.of(new Characters(cursor.getLineAtOffset(), cursor.getOffset(), group));
		}
		return Optional.absent();
	}

}
