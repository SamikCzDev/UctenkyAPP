package cz.cloudcrew.uctenky;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cz.cloudcrew.uctenky.databinding.ActivityMainBinding;

import cz.cloudcrew.uctenky.req.GetInfoFromToken;
import cz.cloudcrew.uctenky.req.LoginRequestTask;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    public static String authToken = null;
    String userName = null;
    String mail = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       setContentView(R.layout.login_test);

       EditText usernameText = findViewById(R.id.username_text);
       EditText passwordText = findViewById(R.id.passweord_text);
       Button loginButton = findViewById(R.id.login_btn);

       SharedPreferences prefs = getSharedPreferences("loginToken", Context.MODE_PRIVATE);
       SharedPreferences.Editor editor = prefs.edit();

       authToken = prefs.getString("cookie","null");
       Log.e("TOKEN",authToken);

       if(!(authToken.equals("null"))) {
           getInfoFromToken();
       }


       LoginRequestTask loginManager = new LoginRequestTask(this);

       loginButton.setOnClickListener(v -> {
           String username = usernameText.getText().toString().trim();
           String password = passwordText.getText().toString().trim();

           if (username.isEmpty() || password.isEmpty()) {
               Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
               return;
           }

           // Použití LoginManager pro přihlášení
           loginManager.loginUser(username, password, new LoginRequestTask.LoginCallback() {
               @Override
               public void onSuccess(String token) {
                   Toast.makeText(MainActivity.this, "Token: " + token, Toast.LENGTH_LONG).show();
                   editor.putString("cookie",token);
                   editor.apply();
                   authToken = token;
                   getInfoFromToken();
               }

               @Override
               public void onError(String error) {
                   Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
               }
           });
       });

    }
    public void getInfoFromToken() {
        GetInfoFromToken getInfoFromToken = new GetInfoFromToken(this);

        getInfoFromToken.getInfo(authToken, new GetInfoFromToken.TokenCallback() {

            @Override
            public void onSuccess(String userName2, String mail2) {
                userName = userName2;
                mail = mail2;
                Toast.makeText(MainActivity.this, "OK: " + mail, Toast.LENGTH_LONG).show();
                setMainView();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        TextView usernameView = findViewById(R.id.username_text);
        TextView mailView = findViewById(R.id.mail_text);

        usernameView.setText(userName);
        mailView.setText(mail);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void setMainView() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }
}