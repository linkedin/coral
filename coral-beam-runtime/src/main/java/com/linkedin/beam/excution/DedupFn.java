/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.excution;

import java.time.Instant;
import org.apache.beam.sdk.coders.VarLongCoder;
import org.apache.beam.sdk.state.StateSpec;
import org.apache.beam.sdk.state.StateSpecs;
import org.apache.beam.sdk.state.ValueState;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.KV;
import org.joda.time.Duration;


public class DedupFn<K, V> extends DoFn<KV<K, V>, KV<K, V>> {
  private static final String STATE_ID = "recordDedup";

  private final Duration dedupWindow;

  @StateId(STATE_ID)
  private final StateSpec<ValueState<Long>> lassModifiedTime = StateSpecs.value(VarLongCoder.of());

  public DedupFn(Duration dedupWindow) {
    this.dedupWindow = dedupWindow;
  }

  @ProcessElement
  public void processElement(
      ProcessContext context, @StateId(STATE_ID) ValueState<Long> lassModifiedTime) {
    final Long lastTouched = lassModifiedTime.read();
    final long eventTime = context.timestamp().getMillis();
    final long currentTime = eventTime > 0 ? eventTime : Instant.now().toEpochMilli();
    if (lastTouched == null) {
      // Output record if we have not seen it
      context.output(context.element());
      lassModifiedTime.write(currentTime);
    } else if (isExpired(lastTouched, currentTime, dedupWindow)) {
      // If out of date, emit and remove it
      context.output(context.element());
      lassModifiedTime.clear();
    } else {
      // Else (seen record and it is still valid) just update last accessed time
      lassModifiedTime.write(currentTime);
    }
  }

  /**
   * Checks if end time is expired from the begin time, given a window.
   *
   * <p>Example: - 2019/03/20 23:59, 2019/03/21 00:00, 1 DAY -> true - 2019/03/20 23:59, 2019/03/21
   * 00:00, 2 MINUTES -> false - 2019/03/21 00:00, 2019/03/21 23:59, 1 DAY -> false - 2019/03/21
   * 00:59, 2019/03/21 01:00, 2 HOURS -> false
   *
   * @param begin begin time
   * @param end end time
   * @param window Duration for the window to compare
   * @return true iff begin time is in the earlier window than end time
   */
  private static boolean isExpired(long begin, long end, Duration window) {
    long windowMillis = window.getMillis();
    return begin / windowMillis < end / windowMillis;
  }
}
