package com.example.llm_enhancedlearningassistantapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import okhttp3.*;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private EditText usernameInput;
    private EditText passwordInput;
    private static final String LOGIN_URL = "http://10.0.2.2:5006/login";
    private final OkHttpClient client = new OkHttpClient();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        Button loginButton = findViewById(R.id.loginButton);
        TextView signupLink = findViewById(R.id.signupLink);

        loginButton.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter username and password.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Attempt Backend Login
            loginOnBackend(username, password);
        });

        signupLink.setOnClickListener(v -> startActivity(new Intent(this, SignupActivity.class)));
    }

    private void loginOnBackend(String username, String password) {
        try {
            JSONObject json = new JSONObject();
            json.put("username", username);
            json.put("password", password);

            RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder().url(LOGIN_URL).post(body).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    mainHandler.post(() -> {
                        // Fallback to local login if backend is down
                        if (AppStore.login(MainActivity.this, username, password)) {
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        } else {
                            Toast.makeText(MainActivity.this, "Connection failed and no local user found.", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                    mainHandler.post(() -> {
                        if (response.isSuccessful()) {
                            try {
                                JSONObject userJson = new JSONObject(responseData).getJSONObject("user");
                                // Sync MongoDB data to local AppStore
                                AppStore.saveUser(MainActivity.this, 
                                    userJson.getString("username"), 
                                    userJson.getString("email"), 
                                    userJson.getString("password"));
                                
                                if (userJson.has("planName")) {
                                    AppStore.savePlan(MainActivity.this, userJson.getString("planName"));
                                }

                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                finish();
                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this, "Error parsing user data", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Invalid credentials in MongoDB.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Login request error", Toast.LENGTH_SHORT).show();
        }
    }
}