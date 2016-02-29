package com.tugas.ecbn.ekacahya_125410150;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


public class ActivityDUa extends ListActivity {
    // Progress Dialog
    private ProgressDialog pDialog;

    //inisialisasifloat button
    ImageButton fab;

    // Membuat objek JSONParser
    JSONParser jParser = new JSONParser();
    ArrayList<HashMap<String, String>> memberList;

    // inisialisasi url daftarsekolah.php
    private static String url_semua_anggota = "http://uas-eka.zz.mu/daftarsekolah.php";

    // inisialisasi nama node dari json yang dihasilkan oleh php
    private static final String TAG_SUKSES = "sukses";
    private static final String TAG_SEKOLAH = "sekolah";
    private static final String TAG_IDMEM = "id";
    private static final String TAG_NAMA = "nama";
    private static final String TAG_LONG = "long";
    private static final String TAG_LAT = "lat";

    // buat JSONArray member
    JSONArray member = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dua);


        // Hashmap untuk ListView
        memberList = new ArrayList<HashMap<String, String>>();

        // buat method untuk menampilkan data pada Background Thread
        new AmbilSekolah().execute();

        // ambil listview
        ListView lv = getListView();

        // pada saat mengklik salah satu nama member
        // lalu alihkan pada class EditanggotaActivity
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // ambil nilai dari ListItem yang dipilih
                String idmem = ((TextView) view.findViewById(R.id.idmem)).getText().toString();
                String sNama = ((TextView) view.findViewById(R.id.nSekolah)).getText().toString();
                String sLong = ((TextView) view.findViewById(R.id.nLong)).getText().toString();
                String sLat = ((TextView) view.findViewById(R.id.nLat)).getText().toString();

                // Starting new intent
                Intent in = new Intent(getApplicationContext(), MapsActivity.class);

                // kirim idmem ke activity Maps
                in.putExtra(TAG_IDMEM, idmem);
                in.putExtra(TAG_NAMA, sNama);
                in.putExtra(TAG_LONG, sLong);
                in.putExtra(TAG_LAT, sLat);

                // mulai activity baru dan dapatkan respon result kode 100
                startActivityForResult(in, 100);
            }
        });//
    }


    class AmbilSekolah extends AsyncTask<String, String, String> {

        // sebelum memulai background thread tampilkan Progress Dialog
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ActivityDUa.this);
            pDialog.setMessage("Mengambil Data Sekolah. Silahkan Tunggu...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            // membangun Parameter
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            // ambil JSON string dari URL
            JSONObject json = jParser.makeHttpRequest(url_semua_anggota, "GET",params);

            // cek log cat untuk JSON reponse
            Log.d("Semua Anggota: ", json.toString());

            try {

                // mengecek untuk TAG SUKSES
                int sukses = json.getInt(TAG_SUKSES);
                if (sukses == 1) {

                    // data ditemukan
                    // mengambil Array dari member
                    member = json.getJSONArray(TAG_SEKOLAH);

                    // looping data semua member/anggota
                    for (int i = 0; i < member.length(); i++) {
                        JSONObject c = member.getJSONObject(i);

                        // tempatkan setiap item json di variabel
                        String nama = c.getString(TAG_NAMA);
                        String slong = c.getString(TAG_LONG);
                        String slat = c.getString(TAG_LAT);

                        // buat new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // menambah setiap child node ke HashMap key => value
                        map.put(TAG_NAMA, nama);
                        map.put(TAG_LONG, slong);
                        map.put(TAG_LAT, slat);

                        // menambah HashList ke ArrayList
                        memberList.add(map);
                    }
                } else {

                    // tidak ditemukan data anggota/member
                    // Tampilkan layar tambahAnggotaActivity
                    Intent i = new Intent(getApplicationContext(),MainActivity.class);

                    // tutup semua activity sebelumnya
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        /**
         * setelah menyelesaikan background task hilangkan the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // hilangkan dialog setelah mendapatkan semua data member
            pDialog.dismiss();

            // update UI dari Background Thread
            runOnUiThread(new Runnable() {
                public void run() {

                    // update hasil parsing JSON ke ListView
                    ListAdapter adapter = new SimpleAdapter(
                            ActivityDUa.this, memberList,
                            R.layout.view,  new String[] {TAG_IDMEM, TAG_NAMA,TAG_LAT, TAG_LONG },
                                            new int[] {R.id.idmem, R.id.nSekolah,R.id.nLat,R.id.nLong});
                    // update listview
                    setListAdapter(adapter);
                }
            });
        }
    }
}
