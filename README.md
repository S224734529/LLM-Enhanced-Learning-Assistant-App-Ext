# LLM-Enhanced Learning Assistant App

## Overview

LLM-Enhanced Learning Assistant App is an Android Java mobile application designed to provide adaptive learning support for beginner students. The application combines traditional Android development with Large Language Model (LLM) features to generate hints, flashcards, learning feedback, and personalised study support.

The application uses a Node.js backend connected to MongoDB Atlas for data storage and Ollama for local AI inference using the Gemma 3 model.

---

# Features

## User Authentication

Users can:

- Create a new account
- Login using saved credentials
- Store profile information locally
- Access personalised learning features

---

# Interest Selection

Students can select learning interests such as:

- Algorithms
- Data Structures
- Web Development
- Cloud Computing
- Databases
- Android Development
- Artificial Intelligence
- Cyber Security

The selected interests are used to personalise generated learning tasks.

---

# Adaptive Learning Tasks

The application generates beginner-level learning activities based on the student’s selected interests.

Features include:

- Multiple-choice questions
- Written response questions
- AI-generated hints
- AI-generated flashcards
- AI feedback after submission

---

# AI Integration with Ollama

The application uses Ollama running locally through a Node.js backend.

The Android app communicates with the backend API using HTTP requests.

The backend then sends prompts to the Gemma 3 model using Ollama.

### AI Features

- Hint generation
- Flashcard generation
- Learning feedback
- Adaptive prompts

---

# MongoDB Atlas Integration

MongoDB Atlas is used to store:

- User accounts
- Interests
- Learning history
- AI responses
- Purchase records

The backend uses Mongoose models to manage database operations.

---

# Learning History

The History screen stores and displays previous generated tasks and AI responses.

Students can review:

- Completed tasks
- AI-generated feedback
- Previous learning activities

---

# Profile Dashboard

The Profile page displays:

- Username
- Learning interests
- Learning statistics
- Correct and incorrect answers

---

# Sharing Features

The application provides two sharing methods:

## Text Sharing

Users can share their profile using Android’s native sharing system.

## QR Code Sharing

Users can generate a QR code containing:

- Username
- Interests
- Learning statistics

The QR code can be scanned by another device to quickly transfer profile information.

---

# Upgrade and Purchasing Features

The application includes an Upgrade screen with multiple plans:

- Starter
- Intermediate
- Advanced

The purchase flow is simulated and purchase details are stored in MongoDB Atlas.

---

# Technologies Used

## Frontend

- Android Studio
- Java
- XML Layouts
- SharedPreferences
- ZXing QR Library

## Backend

- Node.js
- Express.js
- MongoDB Atlas
- Mongoose
- Ollama

## AI Model

- Gemma 3
- Ollama Local Inference

---

# Project Structure

```text
app/
├── java/com/example/llm_enhancedlearningassistantapp/
│   ├── MainActivity.java
│   ├── SignupActivity.java
│   ├── InterestsActivity.java
│   ├── HomeActivity.java
│   ├── TaskActivity.java
│   ├── ResultsActivity.java
│   ├── ProfileActivity.java
│   ├── HistoryActivity.java
│   ├── UpgradeActivity.java
│   ├── AppStore.java
│   └── LlmRepository.java
│
├── res/layout/
│   ├── activity_login.xml
│   ├── activity_signup.xml
│   ├── activity_home.xml
│   ├── activity_task.xml
│   ├── activity_profile.xml
│   ├── activity_history.xml
│   └── activity_upgrade.xml
│
backend/
├── server.js
├── package.json
└── .env
```

---

# Backend Setup

## Install Dependencies

```bash
npm install
```

## Run Ollama

```bash
ollama serve
```

## Download Gemma 3

```bash
ollama pull gemma3:1b
```

## Start Backend

```bash
npm start
```

---

# Android Emulator Backend URL

Use:

```java
http://10.0.2.2:5000/llm
```

This allows the Android emulator to communicate with the locally running backend server.

---

# Modern Android Development Practices

The application follows several modern Android development practices:

- Activity-based navigation
- Responsive XML layouts
- Reusable drawable resources
- Separation of frontend and backend logic
- API-based AI integration
- External cloud database usage
- Asynchronous API communication using OkHttp
- Local AI inference through Ollama
- Modular activity structure

---

# Future Improvements

Possible future improvements include:

- Real payment gateway integration
- User authentication using JWT
- Firebase push notifications
- Real-time analytics
- AI-generated quizzes
- Voice-based learning support
- Cloud deployment for backend APIs
- AI-powered study recommendations

---


