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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;

import com.google.common.collect.ImmutableList;

public class SourceBlocks extends SourceBlock {

	private final List<SourceBlock> supportedBlocks;

	public SourceBlocks(SourceBlock... blocks) {
		this(Arrays.asList(checkNotNull(blocks)));
	}

	SourceBlocks(List<SourceBlock> supportedBlocks) {
		this.supportedBlocks = ImmutableList.copyOf(supportedBlocks);
	}

	private interface SourceBlockRunnable {

		void run(LineSequence lineSequence, SourceBlock sourceBlock);
	}

	@Override
	public ProcessingContext createContext(LineSequence lineSequence) {
		final AtomicReference<ProcessingContext> context = new AtomicReference<ProcessingContext>(
				ProcessingContext.empty());
		process(lineSequence, new SourceBlockRunnable() {

			@Override
			public void run(LineSequence lineSequence, SourceBlock sourceBlock) {
				context.set(checkNotNull(context.get()).merge(sourceBlock.createContext(lineSequence)));
			}
		});
		return checkNotNull(context.get());
	}

	@Override
	public void process(final ProcessingContext context, final DocumentBuilder builder, LineSequence lineSequence) {
		process(lineSequence, new SourceBlockRunnable() {

			@Override
			public void run(LineSequence lineSequence, SourceBlock sourceBlock) {
				sourceBlock.process(context, builder, lineSequence);
			}
		});
	}

	private void process(LineSequence lineSequence, SourceBlockRunnable runnable) {
		SourceBlock currentBlock = null;
		while (lineSequence.getCurrentLine() != null) {
			if (lineSequence.getCurrentLine().isEmpty()) {
				currentBlock = null;
			} else {
				currentBlock = selectBlock(lineSequence);
			}
			if (currentBlock != null) {
				runnable.run(lineSequence, currentBlock);
			} else {
				lineSequence.advance();
			}
		}
	}

	public SourceBlock selectBlock(LineSequence lineSequence) {
		for (SourceBlock candidate : supportedBlocks) {
			if (candidate.canStart(lineSequence)) {
				return candidate;
			}
		}
		return null;
	}

	@Override
	public boolean canStart(LineSequence lineSequence) {
		return true;
	}

}
