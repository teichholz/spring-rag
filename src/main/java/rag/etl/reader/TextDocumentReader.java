package rag.etl.reader;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.TextReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import rag.etl.splitter.ParagraphSplitter;

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
                .map(this::addMetadata)
                .toList();

        return new ParagraphSplitter().split(docs);
    }

    /**
     * Note that {@link Document#getMetadata()} is a shared object which is instantiated once in the {@link TextReader} and then shared.
     * Noop for now.
     */
    public Document addMetadata(Document document) {
        return document;
    }
}
