package com.zey.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    Button loginButton;
    Button signupButton;
    ArrayList<User> UserList;
    Integer deneme;

    Context context= this;
    String filename = "myfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);       //ilişiyi kurdu.  aktivite çalıştığı anda aktiviti maini kendine man arayüz yaptı
        setTitle("Giriş Ekranı");

        GetID();

    }


    public void signupClick(View v){
        Intent i = new Intent(this, signupActivity.class);
        startActivity(i);
    }

    public void loginClick(View v){
        if(checkPassword( username.getText().toString(),password.getText().toString())){
            Toast.makeText(MainActivity.this, "Başarılı Giriş", Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, listActivity.class);
            startActivity(i);

        }else{
            deneme+=1;
            if(deneme>=3){
                Toast.makeText(MainActivity.this, "3 defa hatalı giriş denemesi yaptınız", Toast.LENGTH_LONG).show();
                loginButton.setEnabled(false);
                ///VE KAYT EKRANINA DÖNSÜN
                Intent i = new Intent(this, signupActivity.class);
                startActivity(i);
            }else
                Toast.makeText(MainActivity.this, "Kullanıcı adı veya Parola hatalı", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkPassword(String user, String pass){

        String donen= readFromfile2( );
        Log.e("login activity", "okunan string: " + donen);

        ArrayList<User> userList = new ArrayList<User>();
        userList = get_users(donen);

        int i;
        for (i=0; i< userList.size() ;i++ ){
//            System.out.println("gezinti"+userList.get(i).name);
//            System.out.println("gezinti len"+userList.get(i).name.length());
//            System.out.println("gezinti2"+user);
//            System.out.println("gezinti2 len"+user.length());
//            System.out.println("gezinti"+userList.get(i).password);
//            System.out.println("gezinti len"+userList.get(i).password.length());
//            System.out.println("gezinti2"+pass);
//            System.out.println("gezinti2 len"+pass.length());
            if(user.equals(userList.get(i).name) && pass.equals(userList.get(i).password))
                return true;
        }
        return false;

    }
    public String readFromfile2(){
        File path= getApplicationContext().getFilesDir();
        File readfrom = new File(path,filename);
        byte[]  content = new byte[(int) readfrom.length()];
        try {
            FileInputStream stream  =new FileInputStream(readfrom);
            stream.read(content);
            Log.e("read", "okundu " );
        }
        catch (IOException e) {
            Log.e("Exception", "File read failed: " + e.toString());
        }

        return new String(content);
    }

    public  ArrayList<User> get_users(String gelen){
        System.out.println(gelen);
        ArrayList<User> userList = new ArrayList<User>();
        String[] satir = gelen.split("\n");
        int i;
//        System.out.println("geldi1");
//        System.out.println("satir0"+satir[0]);
//        System.out.println("satir0len"+satir[0].length());
//        System.out.println("satirlen"+satir.length);
        for (i=0; i< satir.length ;i++ ){
            User user = new User();
            String[] kisi = satir[i].split("\t" );
//            System.out.println("geldi2");
//            System.out.println(kisi.length);
//            System.out.println(kisi[0]);
            user.name= kisi[0];
            user.surname= kisi[1];
            user.password= kisi[2];
            user.dgunu= kisi[3];
            user.telno= kisi[4];
            user.email= kisi[5];
            userList.add(user);
        }
        return userList;
    }

    private void cleantexts(){
        username.setText("");       // have a problem//resolved
        password.setText("");
    }

    public void GetID(){
        deneme=0;
        signupButton = (Button) findViewById(R.id.signup_butonu);
        username= (EditText) findViewById(R.id.editTextPersonName);
        password= (EditText) findViewById(R.id.editTextPassword);
        loginButton= (Button) findViewById(R.id.login_butonu);
    }


}