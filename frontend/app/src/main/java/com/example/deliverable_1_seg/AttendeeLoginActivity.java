package com.example.deliverable_1_seg;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AttendeeLoginActivity extends AppCompatActivity {
    private EditText loginUsername;
    private EditText loginPassword;

    private EditText signupFirstName;
    private EditText signupLastName;
    private EditText signupEmail;
    private EditText signupPassword;
    private EditText signupPhone;
    private EditText signupAddress;

    private FirebaseAuth userAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_login_screen); // Link to attendee_login_form.xml

        // Initialize views
        loginUsername = findViewById(R.id.loginUsername);
        loginPassword = findViewById(R.id.loginPassword);

        //Initialize firebase auth
        userAuth = FirebaseAuth.getInstance();


        // Find the Attendee button and set its click listener
        Button ForgotPassword_bttn = findViewById(R.id.ForgotPassword_bttn);

        ForgotPassword_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch AttendeeLoginActivity when button is clicked
                Intent intent = new Intent(AttendeeLoginActivity.this, AttendeePasswordChange.class);
                startActivity(intent);
            }

        });



    }


    // Method to switch to the signup form
    public void switchToSignupAttendee(View view) {
        setContentView(R.layout.attendee_signup_form); // Link to attendee_signup_form.xml

        signupFirstName = findViewById(R.id.signupFirstName);
        signupLastName = findViewById(R.id.signupLastName);
        signupEmail = findViewById(R.id.signupEmail);
        signupPassword = findViewById(R.id.signupPassword);
        signupPhone = findViewById(R.id.signupPhone);
        signupAddress = findViewById(R.id.signupAddress);
    }



    public void notAnAttendee(View view){
        finish();
    }




    public void loginAttendee(View view){
        DatabaseReference attendeeRef = FirebaseDatabase.getInstance().getReference("attendee");
        String userKey = loginUsername.getText().toString().replace(".", ",");

        String email = loginUsername.getText().toString();
        String password = loginPassword.getText().toString();

        //ensure all fields are filled
        if (loginUsername.getText().toString().isEmpty()
                || loginPassword.getText().toString().isEmpty()){
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
        return;
    }
        //firebase auth login
        userAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task ->{
            if (task.isSuccessful()) {
                // Sign-in success
                FirebaseUser user = userAuth.getCurrentUser();
                Intent intent = new Intent(AttendeeLoginActivity.this, AttendeeWelcomePage.class);
                startActivity(intent);
            } else {
                // Sign-in failed
                Toast.makeText(this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public Boolean emailChecker(String email) {
        // Check if the email contains an '@' symbol
        if (email.contains("@")) {
            // Find the index of '@' and check if there's a ',' after it
            String afterAt = email.substring(email.indexOf("@") + 1);
            // Ensure there is a ',' after the '@'
            if (afterAt.contains(",")) {
                return true; // Email is valid
            }
        }
        // If the conditions are not met, return false
        return false;
    }

    public boolean isValidAddress(String address) {
        // Define the regex pattern for "number Word Word (Street Address Pattern)"
        String regex = "^\\d+\\s+[a-zA-Z0-9]+\\s+[a-zA-Z0-9]+$";
        // Compile the pattern
        Pattern pattern = Pattern.compile(regex);
        // Match the address against the pattern
        Matcher matcher = pattern.matcher(address);
        // Return true if it matches, false otherwise
        return matcher.matches();
    }

    public void signupAttendee(View view){

        String email = signupEmail.getText().toString();
        String password = signupPassword.getText().toString();

        if (signupFirstName.getText().toString().isEmpty()
                || signupLastName.getText().toString().isEmpty()
                || signupEmail.getText().toString().isEmpty()
                || signupPassword.getText().toString().isEmpty()
                || signupPhone.getText().toString().isEmpty()
                || signupAddress.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        userAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    //create user
                    FirebaseUser user = userAuth.getCurrentUser();

                    //Save the additional attendee data
                    String userKey = user.getUid();
                    DatabaseReference attendeeRef = FirebaseDatabase.getInstance().getReference("attendee");

                    Map<String, Object> attendeeData = new HashMap<>();
                    attendeeData.put("firstName", signupFirstName.getText().toString());
                    attendeeData.put("lastName", signupLastName.getText().toString());
                    attendeeData.put("email", email);
                    attendeeData.put("phoneNumber", signupPhone.getText().toString());
                    attendeeData.put("address", signupAddress.getText().toString());

                    attendeeRef.child(userKey).setValue(attendeeData).addOnCompleteListener(databaseTask -> {
                        if (databaseTask.isSuccessful()) {
                            Toast.makeText(this, "Attendee signed up successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AttendeeLoginActivity.this, AttendeeWelcomePage.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "Failed to save user details", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    // Sign-up failed
                    Toast.makeText(this, "Failed to sign up attendee: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }






    //Back button to go back to login page
    public void backAttendeeForm (View view) {
        finish();
    }


}
