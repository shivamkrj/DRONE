package in.shivamkrj.drone;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import com.google.android.gms.ads.MobileAds;

//import com.google.android.gms.ads.
//import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class Launcher extends AppCompatActivity {

    TextView sewaTv,sewaTV1,needTv,donateTv,beneficaryTv,ngoTv,itemTv, about;
    String token;

    AlertDialog dialog;
    String FCM_API_KEY;

    private InterstitialAd interstitialAd;
    AdRequest request;
    boolean first;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        first=true;
        googleAd();




        findViews();
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

        FirebaseMessaging.getInstance().subscribeToTopic("admin")
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
    }

    private void googleAd() {
        MobileAds.initialize(this);

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-5012406189825580/9223314671");
        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                interstitialAd.loadAd(request);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);

//                Toast.makeText(Launcher.this, "fail onAdLoaded() "+i, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if(first){
                    interstitialAd.show();
                    first  = false;
                }



            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }
        });

        request  =  new AdRequest.Builder()
                .build();
        interstitialAd.loadAd(request);

    }

    private void findViews() {
        about = findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }
                startActivity(new Intent(Launcher.this,About.class));
            }
        });
        sewaTv = findViewById(R.id.tv_sewa);
        sewaTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //donate/need
                selectActionForDonate();
            }
        });
        sewaTV1 = findViewById(R.id.tv_sewa1);
        sewaTV1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        needTv = findViewById(R.id.needy_tv);
        needTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Launcher.this,UsersActivity.class);
                intent.putExtra("isNeed",true);
                startActivity(intent);
            }
        });
        donateTv = findViewById(R.id.tv_doner);
        donateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Launcher.this,UsersActivity.class);
                intent.putExtra("isNeed",false);
                startActivity(intent);
            }
        });
        beneficaryTv = findViewById(R.id.beneficiary);
        beneficaryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }
                Intent i =new Intent(Launcher.this,Beneficiaries.class);
                i.putExtra("title","Beneficiaries");
                i.putExtra("node","Beneficiaries");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    ActivityOptions o = ActivityOptions.makeScaleUpAnimation(v,0,0, 200,2000);
                    startActivity(i,o.toBundle());
                }
                else
                startActivity(i);
//                finish();
            }
        });
        ngoTv = findViewById(R.id.tv_ngo);
        ngoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Launcher.this,ListNgo.class));

            }
        });
        itemTv=findViewById(R.id.tv_item_donate);
        itemTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(Launcher.this,ItemActivity.class);
                i.putExtra("title","List of Item Donate");
                i.putExtra("node","DonateItem");
                startActivity(i);
            }
        });
    }

    private void selectActionForDonate() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_three_options,null);
        builder.setView(dialogView);
        builder.setCancelable(true);
        CardView need = dialogView.findViewById(R.id.mapCardView);
        CardView donateR = dialogView.findViewById(R.id.chatCardView);
        CardView donateO = dialogView.findViewById(R.id.cashCardView);
        need.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                dialog.dismiss();
                Intent intent = new Intent(Launcher.this,LocationActivity.class);
                intent.putExtra("isNeed",true);
                startActivity(intent);
            }
        });
        donateR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                dialog.dismiss();
                Intent intent = new Intent(Launcher.this,LocationActivity.class);
                intent.putExtra("isNeed",false);
                startActivity(intent);
            }
        });
        donateO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                dialog.dismiss();
                //webview link for donation using uri
                Intent intent = new Intent(Launcher.this,About.class);
                intent.putExtra("url","donate");
                startActivity(intent);
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.launcer_menu, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.notification:
                notification();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void notification(){
//        Intent i =new Intent(Launcher.this,ItemActivity.class);
//        i.putExtra("title","Notifications");
//        i.putExtra("node","ADMIN-NOTIFICATION");
//        startActivity(i);
        Toast.makeText(this,"notice",Toast.LENGTH_LONG).show();


    }
}
