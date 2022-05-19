package com.zey.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.telephony.PhoneNumberUtils;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import  javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
//import javax.mail.internet.AddressException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;

public class signupActivity extends AppCompatActivity {

    EditText ad;
    EditText soyad;
    EditText mail;
    EditText tel;
    EditText dg;
    EditText parola;
    EditText parola2;
    Button kayit_Button;
    ImageView fotoEkle;

    String TO;
    String icerik;

    String name;
    String surname;
    String mailadres ;
    String dgun ;
    String telno ;
    String par ;

    Bitmap bitmap;
    Uri fotoUri;

    Context context= this;
    String filename = "myfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        GetID();
        ///foto al
        fotoEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent anIntent = new Intent();
                anIntent.setType("image/*");
                anIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(anIntent,"Resminizi ekleyin"),1);
            }
        });

        //datepicker için
        Calendar calendar = Calendar.getInstance();
        final int yil = calendar.get(Calendar.YEAR);
        final int ay = calendar.get(Calendar.MONTH);
        final int gun = calendar.get(Calendar.DAY_OF_MONTH);

        dg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(signupActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int a, int g) {
                        a += 1;
                        String date = g + "/" + a + "/" + y;
                        dg.setText(date);
                    }
                }, yil, ay, gun);
                datePickerDialog.show();
            }
        });


        kayit_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checktexts() )      //yazı alanları check!
                    return;
                Toast.makeText(signupActivity.this, "Girilen bilgiler doğru formatta", Toast.LENGTH_SHORT).show();
                name = ad.getText().toString();

                if(!checknames() )  {    //aynı isimli kullanıcı check!
                    Toast.makeText(signupActivity.this, "Aynı isimli kullanıcı mevcut", Toast.LENGTH_LONG).show();
                    return;
                }
                surname = soyad.getText().toString();
                mailadres = mail.getText().toString();
                dgun = dg.getText().toString();
                telno = tel.getText().toString();
                par = parola.getText().toString();

                String text = "Kayıt olduğunuz için teşekkür ederiz\n Kayıt Bilgileriniz:\n"+"İsminiz: "+name+"\nSoysminiz: "+surname+
                        "\nTelefon numaranız: "+telno+"\nDoğum gününüz: "+dgun+"\nParolanız: "+par;
                System.out.println(text);
                TO=mailadres;
                icerik=text;

                new Connection().execute();     //mail atılsın

                kayit_ekle();       //dosyaya kayıt eklensin
                signupClick();
            }
        });
    }
    public void signupClick( ){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode == RESULT_OK){
            fotoUri=data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),fotoUri);
                fotoEkle.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public boolean checknames(){
        String donen= readFromfile2( );
        Log.e("login activity", "okunan string: " + donen);

        ArrayList<User> userList = new ArrayList<User>();
        userList = get_users(donen);

        int i;
        for (i=0; i< userList.size() ;i++ ){
            if(name.equals(userList.get(i).name))
                return false;
        }
        return true;
    }

    public void kayit_ekle(){
        User insan = new User();

        insan.name = name;
        insan.surname = surname;
        insan.dgunu =dgun;
        insan.telno = telno;
        insan.password = par;
        insan.email = mailadres;

        String inputString = insan.name +"\t"+insan.surname +"\t"+insan.password +"\t"+insan.dgunu  +"\t"+ insan.telno  +"\t"+ insan.email+"\n";

        writeToFile(inputString);
    }

    public  ArrayList<User> get_users(String gelen){
        System.out.println(gelen);
        ArrayList<User> userList = new ArrayList<User>();
        String[] satir = gelen.split("\n");
        int i;
        for (i=0; i< satir.length ;i++ ){
            User user = new User();
            String[] kisi = satir[i].split("\t" );
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

    private void writeToFile(String data) {
        File path= getApplicationContext().getFilesDir();
        try {
            FileOutputStream writer  =new FileOutputStream(new File(path,filename),true);
            writer.write(data.getBytes());
            writer.close();
            Log.e("write", "yazıldı: " );
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
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


    public class Connection extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... arg0){
            sendEmail();
            return null;
        }
    }

    public void sendEmail(){
        final String mailadd ="ytuce.guz2020@gmail.com";
        final String pass = "ytu.2020" ;
        Properties prop = new Properties();
        prop.put("mail.smtp.auth","true");
        prop.put("mail.smtp.port","587");
        prop.put("mail.smtp.host","smtp.gmail.com");
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        prop.put("mail.smtp.starttls.enable","true");
        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator(){
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication(){
                        return new PasswordAuthentication(mailadd,pass);
                    }
                });
        /////////////
        try{
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailadd));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(TO));
            message.setSubject("Kaydınız Gerçekleşti!   ");
            message.setText(icerik);
            Transport.send(message);
        }catch (MessagingException e){
            Log.e("HATAAA", "Error while connecting", e);
            throw new RuntimeException(e);
        }
        ///////////
        StrictMode.ThreadPolicy policy= new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

    public boolean checktexts(){
        String email = mail.getText().toString().trim();

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(ad.getText().toString().length() <= 3){
            Toast.makeText(signupActivity.this, "Ad uzunluğu 3'den büyük olmalı", Toast.LENGTH_LONG).show();
            return false;
        }
        if(soyad.getText().toString().length() < 2){
            Toast.makeText(signupActivity.this, "Soyad uzunluğu 1'den büyük olmalı", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!email.matches(emailPattern))
        {
            Toast.makeText(getApplicationContext(),"Geçersiz mail adresi",Toast.LENGTH_SHORT).show();
            mail.setText("");
            return false;
        }
        if (!PhoneNumberUtils.isGlobalPhoneNumber(tel.getText().toString())){
            Toast.makeText(getApplicationContext(),"Geçersiz telefon numarası",Toast.LENGTH_SHORT).show();
            tel.setText("");
            return false;
        }
        if(parola.getText().toString().length() <= 8){
            Toast.makeText(signupActivity.this, "Parola uzunluğu 8'den büyük olmalı", Toast.LENGTH_LONG).show();
            parola.setText("");
            parola2.setText("");
            return false;
        }
        if(!(parola2.getText().toString().equals(parola.getText().toString() ))){
            Toast.makeText(signupActivity.this, "Parolalar aynı değil", Toast.LENGTH_LONG).show();
            parola.setText("");
            parola2.setText("");
            return false;
        }if (bitmap == null) {
            Toast.makeText(context, "Resim Yüklenmedi", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void GetID(){

        kayit_Button = (Button) findViewById(R.id.kayit_butonu);
        ad= (EditText) findViewById(R.id.editTextName);
        soyad= (EditText) findViewById(R.id.editTextName2);
        mail= (EditText) findViewById(R.id.editTextMail);
        dg= (EditText) findViewById(R.id.editTextDg);
        tel= (EditText) findViewById(R.id.editTextTel);
        parola = (EditText) findViewById(R.id.editTextParola);
        parola2 = (EditText) findViewById(R.id.editTextParola2);
        fotoEkle = (ImageView) findViewById(R.id.fotoekle);

    }
}