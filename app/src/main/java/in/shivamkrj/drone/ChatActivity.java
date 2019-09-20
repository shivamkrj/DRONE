package in.shivamkrj.drone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatActivity extends AppCompatActivity {

    String emailid,username;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userReference;
    DatabaseReference adminNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        emailid = getIntent().getStringExtra("EMAIL");
        username = emailid.replaceAll(".","k");
        firebaseDatabase = FirebaseReference.getDatabaseInstance();
        userReference = firebaseDatabase.getReference(emailid);
        adminNotify = firebaseDatabase.getReference("USERS");


    }

}
