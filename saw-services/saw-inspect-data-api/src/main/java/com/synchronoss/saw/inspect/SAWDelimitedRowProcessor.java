package com.synchronoss.saw.inspect;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.ObjectRowProcessor;

public class SAWDelimitedRowProcessor extends ObjectRowProcessor {

  private int SAMPLE_SIZE;

  private List<String> samples;
  private List<Object[]> parsedSamples;
  private int sampleCnt;

  public SAWDelimitedRowProcessor(long sampleSize) {
    // TODO: Need cleanup into to long
    this.SAMPLE_SIZE = (int) sampleSize;
    this.samples = new ArrayList<>(SAMPLE_SIZE);
    this.parsedSamples = new ArrayList<>(SAMPLE_SIZE);
    this.sampleCnt = 0;
  }

  @Override
  public void rowProcessed(Object[] row, ParsingContext context) {
    if (sampleCnt < SAMPLE_SIZE) {
      String currentLine = context.currentParsedContent();
      samples.add(currentLine.substring(0, currentLine.indexOf(new String(context.lineSeparator()))));
      parsedSamples.add(row);
    }
    sampleCnt++;
  }
  public JsonObject toJson() {
    JsonObject result = new JsonObject();
    JsonArray samplesRaw = new JsonArray();
    for (String s : samples)
      samplesRaw.add(new JsonPrimitive(s));
    result.add("samples", samplesRaw);
    return result;
  }
}

