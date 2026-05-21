package com.example.llm_enhancedlearningassistantapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import okhttp3.*;
import java.io.IOException;

public class SignupActivity extends AppCompatActivity {
    private static final String SIGNUP_URL = "http://10.0.2.2:5006/signup";
    private final OkHttpClient client = new OkHttpClient();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        EditText username = findViewById(R.id.newUsernameInput);
        EditText email = findViewById(R.id.emailInput);
        EditText confirmEmail = findViewById(R.id.confirmEmailInput);
        EditText password = findViewById(R.id.newPasswordInput);
        EditText confirmPassword = findViewById(R.id.confirmPasswordInput);
        Button createButton = findViewById(R.id.createAccountButton);

        createButton.setOnClickListener(v -> {
            String name = username.getText().toString().trim();
            String mail = email.getText().toString().trim();
            String mail2 = confirmEmail.getText().toString().trim();
            String pass = password.getText().toString().trim();
            String pass2 = confirmPassword.getText().toString().trim();

            if (name.isEmpty() || mail.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Complete all required fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!mail.equals(mail2)) {
                Toast.makeText(this, "Email addresses do not match.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass.equals(pass2)) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save locally first
            AppStore.saveUser(this, name, mail, pass);
            
            // Send to MongoDB Backend
            registerUserOnBackend(name, mail, pass);
        });
    }

    private void registerUserOnBackend(String username, String email, String password) {
        try {
            JSONObject json = new JSONObject();
            json.put("username", username);
            json.put("email", email);
            json.put("password", password);

            RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder().url(SIGNUP_URL).post(body).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    mainHandler.post(() -> {
                        Toast.makeText(SignupActivity.this, "Backend Sync Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        proceed(); // Still proceed so user can use the app offline
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    mainHandler.post(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "Account synced with MongoDB!", Toast.LENGTH_SHORT).show();
                        }
                        proceed();
                    });
                }
            });
        } catch (Exception e) {
            proceed();
        }
    }

    private void proceed() {
        startActivity(new Intent(this, InterestsActivity.class));
        finish();
    }
}