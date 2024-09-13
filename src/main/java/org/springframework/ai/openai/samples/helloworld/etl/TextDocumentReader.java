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

    @Value("classpath:documents/*.txt")
    private List<Resource> resources;

    public List<Document> get() {
        List<Document> docs = resources.stream()
                .map(TextReader::new)
                .map(DocumentReader::read)
                .flatMap(List::stream)
                .toList();

        return new ParagraphSplitter().split(docs);
    }
}
