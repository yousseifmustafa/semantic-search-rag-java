package com.example;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class Main {
    public static void main(String[] args) {
        try {

            ChatLanguageModel llm = OllamaChatModel.builder()
                    .baseUrl("http://localhost:11434")
                    .modelName("llama3")
                    .timeout(java.time.Duration.ofMinutes(3))
                    .build();
            

            try {
                llm.generate("Hi");
                System.out.println("LLM Connected Successfully");
            } catch (Exception e) {
                System.err.println("LLM Connection Failed: " + e.getMessage());
                return;
            }


            EmbeddingModel embeddingModel = OllamaEmbeddingModel.builder()
                    .baseUrl("http://localhost:11434")
                    .modelName("nomic-embed-text")
                    .timeout(java.time.Duration.ofMinutes(3))
                    .build();
            

            
            try {
                embeddingModel.embed("test").content();
                System.out.println("Embedding Model Connected Successfully");
            } catch (Exception e) {
                System.err.println("Embedding Model Failed: " + e.getMessage());
                return;
            }


            MilvusEmbeddingStore vectorDB;
            try {
                vectorDB = MilvusEmbeddingStore.builder()
                        .uri("http://localhost:19530")
                        .collectionName("knowledge_base")
                        .dimension(768) // nomic-embed-text dimension
                        .build();
                System.out.println("Milvus Vector DB Connected Successfully");
            } catch (Exception e) {
                System.err.println("Milvus Connection Failed: " + e.getMessage());
                return;
            }


            String filePath = "knowledge.txt";
           
            if (!Files.exists(Path.of(filePath))) {
                System.err.println("File 'knowledge.txt' not found!");
                return;
            }
            
            String knowledgeText = Files.readString(Path.of(filePath));
            System.out.println("Knowledge loaded");


            TextSegment textSegment = TextSegment.from(knowledgeText);
            vectorDB.add(embeddingModel.embed(textSegment.text()).content(), textSegment);
            System.out.println("Knowledge stored in vector database");


            String userQuery = "ما الفرق بين الذكاء الاصطناعي والتعلم الآلي؟";

            
            var queryEmbedding = embeddingModel.embed(userQuery).content();
            System.out.println("Query embedded successfully");


            int topK = 3;
            List<EmbeddingMatch<TextSegment>> topResults = vectorDB.findRelevant(queryEmbedding, topK);
            
            System.out.println("Top size: " + topResults.size()); 
               
                StringBuilder context = new StringBuilder();
                for (int i = 0; i < topResults.size(); i++) {
                        String resultText = topResults.get(i).embedded().text();
                        double score = topResults.get(i).score();
                        System.out.println("  " + (i + 1) + ". [Score: " + String.format("%.4f", score) + "] " + 
                                        resultText.substring(0, Math.min(100, resultText.length())) + "...");
                        context.append(resultText).append("\n");
                }


            String prompt = String.format(
                "أنت مساعد ذكي متخصص في الإجابة على الأسئلة بناءً على المعلومات المعطاة.\n\n" +
                "المعلومات المتاحة:\n%s\n" +
                "السؤال: %s\n\n" +
                "تعليمات:\n" +
                "- أجب باللغة العربية فقط\n" +
                "- استخدم المعلومات المعطاة فقط\n" +
                "- كن دقيقاً وواضحاً\n" +
                "- إذا لم تجد المعلومة، قل 'لا توجد معلومات كافية'\n\n" +
                "الإجابة:",
                context.toString(), userQuery
            );


            String finalAnswer = llm.generate(prompt);
            System.out.println("Final Ans:  \n " + finalAnswer);
        
            try {
                 String fileName = "answer_output.txt";
                 BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
                 writer.write("سؤال المستخدم:\n" + userQuery + "\n\n");
                 writer.write("الإجابة:\n" + finalAnswer + "\n"); 
                 writer.close();}
           catch (IOException e) {
                 System.err.println("فشل حفظ الإجابة: " + e.getMessage());
                return;}
        
        
        } catch (Exception e) {
            System.err.println("System Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}