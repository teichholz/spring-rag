package org.springframework.ai.openai.samples.helloworld.etl;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HTMLDocumentReader implements DocumentReader {

    private final Resource resource;

    HTMLDocumentReader(@Value("classpath:documents/abwesenheit.html") Resource resource) {
        this.resource = resource;
    }

    public List<Document> get() {
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
        return tikaDocumentReader.read();
    }
}
