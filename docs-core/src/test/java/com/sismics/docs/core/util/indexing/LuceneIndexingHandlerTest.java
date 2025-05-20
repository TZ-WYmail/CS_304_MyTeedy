package com.sismics.docs.core.util.indexing;

import com.sismics.docs.core.model.jpa.Document;
import com.sismics.docs.core.model.jpa.File;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class LuceneIndexingHandlerTest {

    @Test
    public void testAcceptAlwaysTrue() {
        LuceneIndexingHandler handler = new LuceneIndexingHandler();
        assertTrue(handler.accept(), "accept() 方法应始终返回 true");
    }

    @Test
    public void testStartUpAndShutdown() {
        LuceneIndexingHandler handler = new LuceneIndexingHandler();
        assertDoesNotThrow(() -> {
            handler.startUp();
            handler.shutDown();
        }, "startUp/shutDown 应该能够正常执行");
    }

    @Test
    public void testClearIndex() {
        LuceneIndexingHandler handler = new LuceneIndexingHandler();
        assertDoesNotThrow(() -> {
            handler.startUp();
            handler.clearIndex(); // 会触发 IndexWriter::deleteAll
            handler.shutDown();
        });
    }

    @Test
    public void testCreateDocumentsWithEmptyList() {
        LuceneIndexingHandler handler = new LuceneIndexingHandler();
        assertDoesNotThrow(() -> {
            handler.startUp();
            handler.createDocuments(Collections.emptyList());
            handler.shutDown();
        });
    }

    @Test
    public void testCreateFilesWithEmptyList() {
        LuceneIndexingHandler handler = new LuceneIndexingHandler();
        assertDoesNotThrow(() -> {
            handler.startUp();
            handler.createFiles(Collections.emptyList());
            handler.shutDown();
        });
    }
}
