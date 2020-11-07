/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.excution;

import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.Reshuffle;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.joda.time.Duration;


/** This class performs message deduplication on keys. */
public class MessageDedup<K, V> extends PTransform<PCollection<KV<K, V>>, PCollection<KV<K, V>>> {
  // Window for deduping event. Events within a same window will be deduped.
  private final Duration dedupWindow;

  private MessageDedup(Duration dedupWindow) {
    this.dedupWindow = dedupWindow;
  }

  /**
   * Returns a transform that will dedup a stream by key in fixed windows. The transformation acts
   * as a filter to skip any messages with keys that have been seen in the same window and output
   * them otherwise. Messages with new key will be save into the internal state.
   *
   * <p>Usage:
   *
   * <pre>{@code
   * input.apply(MessageDedup.within(Duration.standardHours(2));
   * }</pre>
   *
   * This will dedup all messages with the same 2 hour window, i.e. from 8:00-10:00, 10:00-12:00.
   *
   * @param dedupWindow Duration for deduping window
   * @param <K> Key type
   * @param <V> Value type
   */
  public static <K, V> MessageDedup<K, V> within(Duration dedupWindow) {
    return new MessageDedup<>(dedupWindow);
  }

  @Override
  public PCollection<KV<K, V>> expand(PCollection<KV<K, V>> input) {
    return input.apply(Reshuffle.of()).apply(ParDo.of((new DedupFn<>(dedupWindow))));
  }
}
