package org.springframework.ai.openai.samples.helloworld.etl;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.TextReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TextDocumentReader implements DocumentReader {

    private final Resource resource;

    TextDocumentReader(@Value("classpath:documents/paragraphen.txt") Resource resource) {
        this.resource = resource;
    }

    public List<Document> get() {
        TextReader textReader = new TextReader(resource);

        return new NewlineSplitter().split(textReader.read());
    }
}
