package com.tugas.ecbn.ekacahya_125410150;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements LocationListener {
    //inisialisasi 2 buah tombol yang ada di halaman utama untuk melihat dan menyimpan
    Button save, lihat;

    //inisialisasi Edittext yang ada pada halaman utama, nama = nama sekolah, l_long = longitude, l_lat = latitude
    EditText nama, l_long, l_lat;

    // Progress Dialog
    private ProgressDialog pDialog;

    //ambil kelas JSONParser yang telah di buat untuk parsing data JSON
    JSONParser jsonParser = new JSONParser();

    // inisialisasi url tambahanggota.php
    private static String url_addSekolah = "http://uas-eka.zz.mu/tambahsekolah.php";

    // inisialisasi nama node dari json yang dihasilkan oleh php (utk class ini hanya node "sukses")
    private static final String TAG_SUKSES = "sukses";

    //inisialisasi kelas LocationManager hasil import android.location.Location;
    // yang pada aplikasi ini digunakan untuk mengambil data koordinat yang didaptkan olehg GPS
    LocationManager locationmanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CheckEnableGPS();

        //menghubungkan dari layout ke java, baik itu untuk EditText maupun Button
        nama = (EditText) findViewById(R.id.editText);
        l_long = (EditText) findViewById(R.id.e_long);
        l_lat = (EditText) findViewById(R.id.e_lat);

        save = (Button) findViewById(R.id.btnSave);
        lihat = (Button) findViewById(R.id.btnLihat);

        //eventhandler ketika tombol save di sentuh akan memanggil kelas TambahSekolah() yang merupakan innerclass dari kelas ini
        // kemudian memanggil method execute yang akan menjalankan method method yang ada pada InnerClass TambahSekolah
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // buat method pada background thread
                if (nama.getText().toString().isEmpty() ||l_long.getText().toString().isEmpty() || l_lat.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Isi data yang dibutuhkan", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this, "Data lengkap", Toast.LENGTH_SHORT).show();
                    new TambahSekolah().execute();
                }
            }
        });

        //event handler saat tombol lihat dibuka akan memindahkan dari halaman MainActivity ke halaman activityDUa
        lihat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(MainActivity.this, ActivityDUa.class);
                startActivity(a);
            }
        });

        //perintah perintah dibawah ini untuk mengambil data posisi dimana smartphone yang digunakan itu digunakan
        //untuk disimpan ke dalam database di server
        locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria cri = new Criteria();
        String provider = locationmanager.getBestProvider(cri, false);
        //melakukan pengecekkan terhadap ketersediaan provider, jika tersedia akan melakukan update data lokasi yang diketahui
        // jika tidak di ketahui maka akan menampilkan toast message.
        if (provider != null & !provider.equals(""))
        {
            Location location = locationmanager.getLastKnownLocation(provider);
            locationmanager.requestLocationUpdates(provider, 10000, 5000, this);
            if (location != null)
            {
                onLocationChanged(location);
            } else {
                Toast.makeText(getApplicationContext(), "location not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Provider is null", Toast.LENGTH_SHORT).show();
        }
    }

    //ketika sensor mendapatkan perubahan lokasi atau pergerakan dari smartphone maka akan melakukan perubahan teks
    // dari semula menjadi lokasi latitude dan longitude yang baru didapatkan oleh sensor
    @Override
    public void onLocationChanged(Location location) {
        l_lat.setText(""+location.getLatitude());
        l_long.setText(""+location.getLongitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    //kelas dibawah ini digunakan untuk menyimpan data kedalam server webservice
    class TambahSekolah extends AsyncTask<String, String, String> {
        //ketika data dicoba untuk disimpan di server maka akan menjalankan Progress dialog dengan pesan tertentu
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Menambah data..silahkan tunggu");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        //method dibawah ini dilakukan ketika pengguna ingin menyimpan data yang dimiliki, seluruh proses pemasukkan data ada pada method ini
        @Override
        protected String doInBackground(String... strings) {
            //inisialisasi string baru untuk persiapan disimpan ke webserver
            String Snama = nama.getText().toString();
            String SLong = l_long.getText().toString();
            String SLat = l_lat.getText().toString();

            // Parameter dengan nilai sesuai dengan yang telah dibuat diatas
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("nama", Snama));
            params.add(new BasicNameValuePair("long", SLong));
            params.add(new BasicNameValuePair("lat", SLat));

            // mengambil JSON Object dengan method POST
            JSONObject json = jsonParser.makeHttpRequest(url_addSekolah, "POST", params);

            // periksa respon log cat
            Log.d("Respon tambah anggota", json.toString());

            try {
                int sukses = json.getInt(TAG_SUKSES);
                if (sukses == 1) {

                    // jika sukses menambah data baru
                    Intent i = new Intent(getApplicationContext(),ActivityDUa.class);
                    startActivity(i);

                    // tutup activity ini
                    finish();
                } else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String file_url) {
            // hilangkan Progress dialog ketika selesai menambah data baru
            pDialog.dismiss();
        }
    }
    private void CheckEnableGPS(){
        String provider = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(!provider.equals("")){
            //GPS Enabled
            Toast.makeText(MainActivity.this, "GPS Enabled: " + provider,
                    Toast.LENGTH_LONG).show();
        }else{
            Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
            startActivity(intent);
        }

    }
}
