package rag.etl.reader;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.document.id.IdGenerator;
import org.springframework.ai.document.id.JdkSha256HexIdGenerator;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class SimpleReader implements DocumentReader {

    public static final String CHARSET_METADATA = "charset";

    public static final String SOURCE_METADATA = "source";

    /**
     * Input resource to load the text from.
     */
    private final Resource resource;

    @Setter
    private IdGenerator idGenerator = new JdkSha256HexIdGenerator();

    /**
     * Character set to be used when loading data from the
     */
    private final Charset charset = StandardCharsets.UTF_8;

    @Getter
    private final Map<String, Object> customMetadata = new HashMap<>();


    @SneakyThrows
    @Override
    public List<Document> get() {
        String document = StreamUtils.copyToString(this.resource.getInputStream(), this.charset);

        // Inject source information as a metadata.
        this.customMetadata.put(CHARSET_METADATA, this.charset.name());
        this.customMetadata.put(SOURCE_METADATA, this.resource.getFilename());

        return List.of(new Document(document, new HashMap<>(this.customMetadata), idGenerator));
    }
}
