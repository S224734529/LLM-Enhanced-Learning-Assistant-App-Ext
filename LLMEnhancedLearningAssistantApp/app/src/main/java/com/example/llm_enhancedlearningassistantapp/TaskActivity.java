package com.example.llm_enhancedlearningassistantapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Set;

public class TaskActivity extends AppCompatActivity {
    private TextView promptText;
    private TextView aiResponseText;
    private ProgressBar loadingBar;
    private EditText writtenAnswerInput;
    private RadioGroup questionOneGroup;
    private final LlmRepository llmRepository = new LlmRepository();
    private String username;
    private String mainTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        username = AppStore.getUsername(this);
        promptText = findViewById(R.id.promptText);
        aiResponseText = findViewById(R.id.aiResponseText);
        loadingBar = findViewById(R.id.loadingBar);
        writtenAnswerInput = findViewById(R.id.writtenAnswerInput);
        questionOneGroup = findViewById(R.id.questionOneGroup);

        Button hintButton = findViewById(R.id.hintButton);
        Button summaryButton = findViewById(R.id.summaryButton);
        Button submitButton = findViewById(R.id.submitButton);

        Set<String> interests = AppStore.getInterests(this);
        mainTopic = interests.isEmpty() ? "Algorithms" : interests.iterator().next();

        String taskPrompt = "Create an adaptive beginner learning task for a student interested in " + mainTopic + ".";
        promptText.setText("Prompt: " + taskPrompt);

        hintButton.setOnClickListener(v -> {
            String prompt = "Generate a helpful hint for this question about " + mainTopic + ": What is the purpose of testing in software development?";
            requestAi(prompt);
        });

        summaryButton.setOnClickListener(v -> {
            String prompt = "Create 3 flashcards from the topic " + mainTopic + " for a beginner student.";
            requestAi(prompt);
        });

        submitButton.setOnClickListener(v -> submitAnswers());
    }

    private void requestAi(String prompt) {
        loadingBar.setVisibility(View.VISIBLE);
        aiResponseText.setText("Prompt:\n" + prompt + "\n\nLoading response...");

        llmRepository.generate(prompt, username, mainTopic, new LlmRepository.LlmCallback() {
            @Override
            public void onSuccess(String text) {
                loadingBar.setVisibility(View.GONE);
                aiResponseText.setText("Prompt:\n" + prompt + "\n\nResponse:\n" + text);
            }

            @Override
            public void onFailure(String error) {
                loadingBar.setVisibility(View.GONE);
                aiResponseText.setText("Prompt:\n" + prompt + "\n\nFailure:\n" + error);
            }
        });
    }

    private void submitAnswers() {
        int selectedId = questionOneGroup.getCheckedRadioButtonId();
        String written = writtenAnswerInput.getText().toString().trim();

        if (selectedId == -1 || written.isEmpty()) {
            Toast.makeText(this, "Answer both questions before submitting.", Toast.LENGTH_SHORT).show();
            return;
        }

        String prompt = "Explain why this student's answer is correct or incorrect. Topic: " + mainTopic + ". Written answer: " + written;
        loadingBar.setVisibility(View.VISIBLE);

        llmRepository.generate(prompt, username, mainTopic, new LlmRepository.LlmCallback() {
            @Override
            public void onSuccess(String text) {
                loadingBar.setVisibility(View.GONE);
                String result = "1. Question 1\nResponse from the model: Good attempt. Testing helps confirm that software works as expected.\n\n"
                        + "2. Question 2\nResponse from the model: " + text + "\n\n"
                        + "3. Next Step\nStudy one short example and try another practice task tomorrow.";
                AppStore.saveLastResult(TaskActivity.this, result);
                startActivity(new Intent(TaskActivity.this, ResultsActivity.class));
            }

            @Override
            public void onFailure(String error) {
                loadingBar.setVisibility(View.GONE);
                AppStore.saveLastResult(TaskActivity.this, "The answer was submitted, but AI feedback failed: " + error);
                startActivity(new Intent(TaskActivity.this, ResultsActivity.class));
            }
        });
    }
}