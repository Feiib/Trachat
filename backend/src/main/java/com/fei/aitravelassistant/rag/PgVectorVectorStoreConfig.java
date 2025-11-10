package com.fei.aitravelassistant.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType.COSINE_DISTANCE;

@Configuration
public class PgVectorVectorStoreConfig {
    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    @Bean
    public VectorStore pgVectorVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel dashscopeEmbeddingModel, LoveAppDocumentLoader loveAppDocumentLoader) {
        PgVectorStore vectorStore = PgVectorStore.builder(jdbcTemplate, dashscopeEmbeddingModel)
                .dimensions(1536)                    // Optional: defaults to model dimensions or 1536
                .distanceType(COSINE_DISTANCE)       // Optional: defaults to COSINE_DISTANCE
                //            .indexType(HNSW)                     // Optional: defaults to HNSW
                .initializeSchema(false)              // Optional: defaults to false
                .schemaName("public")                // Optional: defaults to "public"
                .vectorTableName("vector_store")     // Optional: defaults to "vector_store"
                .maxDocumentBatchSize(10000)         // Optional: defaults to 10000
                .build();
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM public.vector_store", Integer.class);
        if (count != null && count > 0) {
            System.out.println(" 向量数据库已存在数据，跳过重新导入。");
        } else {
            System.out.println(" 向量数据库为空，开始重新导入数据。");
            List<Document> documents = loveAppDocumentLoader.MarkdownLoad();
            int batchSize = 25;
            for (int i = 0; i < documents.size(); i += batchSize) {
                int end = Math.min(i + batchSize, documents.size());
                List<Document> batch = documents.subList(i, end);
                vectorStore.add(batch);
            }
            System.out.println("数据已写入到数据库");
        }
        return vectorStore;
    }
}

