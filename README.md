# 🤖 LangChain4j + Ollama + Milvus RAG Project

A lightweight Java-based Retrieval-Augmented Generation (RAG) pipeline using **LangChain4j**, **Ollama**, and **Milvus** to answer user questions based on custom knowledge.

---

## 🚀 How It Works

1. Loads knowledge from a local file (`knowledge.txt`)
2. Converts text chunks to embeddings using `nomic-embed-text`
3. Stores embeddings in a Milvus vector database
4. Accepts a user query
5. Searches Milvus for the top-k relevant chunks
6. Constructs a prompt with the retrieved context
7. Sends the prompt to the `llama3` model via Ollama
8. Saves the final answer to `answer_output.txt`

---

## 📁 Project Structure

```
RAG-TASK/
├── src/
│   └── main/java/com/example/Main.java
├── pom.xml
├── knowledge.txt
├── answer_output.txt
└── README.md
```

---

## 🛠️ Requirements

- Java 17+
- Maven
- Ollama running on `localhost:11434`
- Milvus running on `localhost:19530`
- LangChain4j
- Open-source embedding model: `nomic-embed-text`

---

## ✅ Example Output

```
User Question:
What is the difference between Artificial Intelligence and Machine Learning?

Answer:
The main difference is that Artificial Intelligence covers a broad range of capabilities, while Machine Learning focuses on improving performance through data.
```

---

## 📌 Notes

- LLM used: `llama3` (via Ollama)
- Embedding model: `nomic-embed-text`
- Context-aware answering using top-k vector search
- Answers are generated in **Arabic** based on instructions

---

## 📄 Features

- Clean, minimal Java implementation using LangChain4j
- Real-time answer generation via local models
- Output logging to a text file (`answer_output.txt`)
- Modular and easy to extend

---

## 👨‍💻 Developer

Developed by **Youssef** — an educational demo of modern AI integration using Java.

---

## 🧠 Keywords

`LangChain4j` · `RAG` · `Java` · `Ollama` · `Milvus` · `Local LLMs` · `Embeddings` · `Arabic NLP`