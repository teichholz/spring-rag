package org.springframework.ai.openai.samples.helloworld.etl;

import org.springframework.ai.transformer.splitter.TextSplitter;

import java.util.Arrays;
import java.util.List;

public class ParagraphSplitter extends TextSplitter {

    @Override
    protected List<String> splitText(String text) {
        return Arrays.asList(text.split("\n\n+"));
    }
}
