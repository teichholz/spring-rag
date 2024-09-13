package org.springframework.ai.openai.samples.helloworld.etl;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MDReader implements DocumentReader {

    private final Resource resource;

    MDReader(@Value("classpath:documents/abwesenheit.md") Resource resource) {
        this.resource = resource;
    }

    public List<Document> get() {
        MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                .withHorizontalRuleCreateDocument(true)
                .withIncludeCodeBlock(true)
                .withIncludeBlockquote(true)
                .build();

        MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
        return reader.get();
    }
}
