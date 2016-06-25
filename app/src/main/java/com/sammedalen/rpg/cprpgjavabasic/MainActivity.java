package com.sammedalen.rpg.cprpgjavabasic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.sammedalen.rpg.cprpgjavabasic.classess.myApp;

public class MainActivity extends AppCompatActivity {
    private Button logInButton;
    private Button logOutButton;
    private Button resumeGameButton;
    private Button createCharacterButton;
    private Button selectCharacterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logInButton = (Button) findViewById(R.id.main_button_sign_log);
        logOutButton = (Button) findViewById(R.id.main_button_log_out);
        resumeGameButton = (Button) findViewById(R.id.main_button_resume_game);
        createCharacterButton = (Button) findViewById(R.id.main_button_create_character);
        selectCharacterButton = (Button) findViewById(R.id.main_button_select_character);

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 1);
            }
        });
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((myApp)getApplicationContext()).getAPIInstance().logOut(true);
                logInButton.setVisibility(Button.VISIBLE);
                logOutButton.setVisibility(Button.GONE);
                createCharacterButton.setVisibility(Button.GONE);
                selectCharacterButton.setVisibility(Button.GONE);
                resumeGameButton.setVisibility(Button.GONE);
                startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 1);
            }
        });
        startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 1);
        //startActivity(new Intent(MainActivity.this, GameActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("finished - request code",requestCode + "");
        Log.d("finished - result code", resultCode + "");
        if(requestCode == 1){//successful log in
            String loginType = data.getExtras().getString("loginType");
            Log.d("loginType", loginType);
            logOutButton.setVisibility(Button.VISIBLE);
            logInButton.setVisibility(Button.GONE);
            if(loginType.equals("guest") || loginType.equals("signUp")){
                createCharacterButton.setVisibility(Button.VISIBLE);
                Log.d("create", "goToCreate");
                startActivityForResult(new Intent(MainActivity.this, CharacterCreate.class), 2);
            } else {
                selectCharacterButton.setVisibility(Button.VISIBLE);
                startActivityForResult(new Intent(MainActivity.this, CharacterSelect.class), 3);
            }

        } else if (requestCode == 2 || requestCode == 3) {// successfull character creation/selection
            createCharacterButton.setVisibility(Button.GONE);
            selectCharacterButton.setVisibility(Button.GONE);
            resumeGameButton.setVisibility(Button.VISIBLE);
            Intent gameIntent = new Intent(MainActivity.this, GameActivity.class);
            gameIntent.putExtra("gameData", data.getExtras().getString("gameData"));
            startActivity(gameIntent);
        }
    }
}
