package in.shivamkrj.drone;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private String PASSWORD = "abc";
    private String USERNAME = "abc@";
    private FirebaseAuth firebaseAuth;
    String token;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(getApplicationContext());

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("fcms", "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        token = task.getResult().getToken();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("zzzfcms", msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        FirebaseMessaging.getInstance().subscribeToTopic("all-users")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d("zzzubscribeToTopic", msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        mEmailView = findViewById(R.id.email);

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setVisibility(View.GONE);

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String username = sharedPref.getString("SHIVAMKUMAR","skr");
        Toast.makeText(this,username,Toast.LENGTH_SHORT).show();
        if(!username.equals("skr")){
            login(username);
        }



        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                loginToTaskActivity();
                String s = mEmailView.getText().toString();

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("SHIVAMKUMAR",s);
                editor.apply();
                login(s);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        firebaseAuth = FirebaseAuth.getInstance();

//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            Intent intent = new Intent(this, LocationActivity.class);
//            String email = user.getEmail();
//            if(email==null||email.length()<2){
//
//            }else {
//                intent.putExtra("EMAIL", email);
//                startActivity(intent);
//                finish();
//            }
//        } else {
//            // No user is signed in
//        }

    }

    private void login(String username) {
        Intent intent = new Intent(this, LocationActivity.class);
        intent.putExtra("EMAIL", username);
        intent.putExtra("TOKEN",token);
        startActivity(intent);
        finish();
    }

    private void loginUsingSharedPreference() {

    }

//    private void loginToTaskActivity() {
//        final ProgressDialog pd = new ProgressDialog(this);
//        pd.setMessage("loading");
//        pd.setCanceledOnTouchOutside(false);
//        pd.show();
//        String password = mPasswordView.getText().toString();
////        mEmailView.setText("");
//        mPasswordView.setText("");
////        FirebaseAuth.getInstance().signOut();
//        firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (task.isSuccessful()) {
//                    pd.dismiss();
//                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(MainActivity.this,LocationActivity.class);
//                    intent.putExtra("EMAIL",mEmailView.getText().toString());
//                    startActivity(intent);
//                    finish();
//                }else{
//                    Toast.makeText(MainActivity.this,"Invalid Credentials",Toast.LENGTH_SHORT).show();
//                    pd.dismiss();
//                }
//            }
//        });
//    }
}
