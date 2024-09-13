import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.samples.helloworld.Application;
import org.springframework.ai.openai.samples.helloworld.etl.TextDocumentReader;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest(classes = Application.class)
public class VectorStoreTest {
    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private TextDocumentReader textDocumentReader;

    @Test
    public void insert() {
        List <Document> documents = List.of(
                new Document("Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!", Map.of("meta1", "meta1")),
                new Document("The World is Big and Salvation Lurks Around the Corner"),
                new Document("You walk forward facing the past and you turn back toward the future.", Map.of("meta2", "meta2")));

        vectorStore.add(documents);
    }

    @Test
    public void similar() {
        List<Document> results = vectorStore.similaritySearch(SearchRequest
                .query("Was ist Spring?"));
        System.out.println(results);
    }

    @Test
    public void etl() {
        List<Document> splitDocuments = textDocumentReader.get();
        for (Document splitDocument : splitDocuments) {
            System.out.println(splitDocument);
        }
    }
}
