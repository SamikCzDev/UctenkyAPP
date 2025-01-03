package cz.cloudcrew.uctenky;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
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

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import cz.cloudcrew.uctenky.databinding.ActivityMainBinding;

import cz.cloudcrew.uctenky.req.GetInfoFromToken;
import cz.cloudcrew.uctenky.req.LoginRequestTask;
import cz.cloudcrew.uctenky.ui.home.HomeFragment;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    public static String authToken = null;
    String userName = null;
    String mail = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
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
                Intent switchActivityIntent = new Intent(getBaseContext(), DocumentCreateMainActivity.class);
                startActivity(switchActivityIntent);
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == 2131230788) {
            authToken = null;
            SharedPreferences prefs = getSharedPreferences("loginToken", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.remove("cookie");
            editor.apply();
            setContentView(R.layout.login_test);
            EditText usernameText = findViewById(R.id.username_text);
            EditText passwordText = findViewById(R.id.passweord_text);
            Button loginButton = findViewById(R.id.login_btn);



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


        return super.onOptionsItemSelected(item);

    }
    private static final int REQUEST_CAMERA_PERMISSION = 100;

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, REQUEST_CAMERA_PERMISSION);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Oprávnění udělena.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Oprávnění zamítnuta.", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}