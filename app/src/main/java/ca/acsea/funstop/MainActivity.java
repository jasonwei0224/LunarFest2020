package ca.acsea.funstop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;


import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import ca.acsea.funstop.event.Event;
import ca.acsea.funstop.sponsorquiz.QuizEnd;
import ca.acsea.funstop.sponsorquiz.QuizStart;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    private Event event;
    private Map map;
    private FunStop funStop;
    private QuizStart quizStart;
    private MyPoint myPoint;
    private About about;
    private QuizEnd quizEnd;
    private QrCodeScanner qrCodeScanner;
    private DatabaseReference ref;
    TextView userEmail;
    FirebaseUser currentUser;
    User mUser;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        Gson gson = new Gson();
        sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        String json = sharedPreferences.getString("userObject", "");
        mUser = gson.fromJson(json, User.class);

        //Initialize user
//        mUser = (User) getIntent().getSerializableExtra("user");

        //Actionbar hide
        //getSupportActionBar().hide();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference();

        //Return the FragmentManager for interacting with fragments associated with this activity.
        fragmentManager = getSupportFragmentManager();

        // Initialize page objects
        event = new Event(fragmentManager);
        map = new Map();
        quizStart = new QuizStart(fragmentManager, mUser, ref);
        //myPoint = new MyPoint(currentUser);
        //funStop = new FunStop(fragmentManager, currentUser, ref);
        about = new About();

        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, event).commitAllowingStateLoss();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference dbUsers = FirebaseDatabase.getInstance().getReference(Login.NODE_USERS);
        dbUsers.child(mAuth.getCurrentUser().getUid()).child("email").setValue(mAuth.getCurrentUser().getEmail());

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
//            super.onBackPressed();
        }
    }

//    public void onBackPressed(){
//
//    }

    public void onPause(){
        super.onPause();
//        SharedPreferences.Editor prefs = sharedPreferences.edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(mUser);
//        prefs.putString("userObject", json);

//        prefs.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav, menu);
        userSideBar();
        return true;
    }

    public void userSideBar(){

        if(currentUser != null){
            userEmail = findViewById(R.id.userEmail);
            userEmail.setText(mUser.getEmail());
        }
    }

    /**
     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
     // Handle action bar item clicks here. The action bar will
     // automatically handle clicks on the Home/Up button, so long
     // as you specify a parent activity in AndroidManifest.xml.
     int id = item.getItemId();
     //noinspection SimplifiableIfStatement
     if (id == R.id.action_settings) {
     return true;
     }
     return super.onOptionsItemSelected(item);
     }
     **/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        transaction = fragmentManager.beginTransaction();
        int id = item.getItemId();

        if (id == R.id.nav_event) {
            transaction.replace(R.id.frameLayout, event).commitAllowingStateLoss();
        } else if (id == R.id.nav_map) {
            //intent
            transaction.replace(R.id.frameLayout, map).commitAllowingStateLoss();
        } else if (id == R.id.nav_funstop) {
            Intent intent = new Intent(this, FunStop.class);
//            intent.putExtra("user", mUser);
            startActivity(intent);

            //transaction.replace(R.id.frameLayout, funStop).commitAllowingStateLoss();
        } else if (id == R.id.nav_quiz) {
            transaction.replace(R.id.frameLayout, quizStart).commitAllowingStateLoss();
        } else if (id == R.id.nav_point) {
            Intent intent = new Intent(this, MyPoint.class);
            intent.putExtra("source", "navbar");
            startActivity(intent);
            //  transaction.replace(R.id.frameLayout, myPoint).commitAllowingStateLoss();
        } else if (id == R.id.nav_about) {
            transaction.replace(R.id.frameLayout, about).commitAllowingStateLoss();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}