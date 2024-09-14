package rag.etl.reader;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MDReader {

    @Value("classpath:documents/*.md")
    private List<Resource> resources;

    public List<Document> get() {
        MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                .withHorizontalRuleCreateDocument(true)
                .withIncludeCodeBlock(true)
                .withIncludeBlockquote(true)
                .build();

        return resources.stream()
                .map(resource -> new MarkdownDocumentReader(resource, config))
                .map(DocumentReader::read)
                .flatMap(List::stream)
                .toList();
    }
}
