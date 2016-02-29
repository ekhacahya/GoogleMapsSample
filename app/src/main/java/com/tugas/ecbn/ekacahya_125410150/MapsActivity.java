package com.tugas.ecbn.ekacahya_125410150;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    String namasekolah;
    Float lat,lon;

    // inisialisasi nama node dari json yang dihasilkan oleh php
    private static final String TAG_IDMEM = "id";
    private static final String TAG_NAMA = "nama";
    private static final String TAG_LONG = "long";
    private static final String TAG_LAT = "lat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiga);

        //menerima data yang disimpan di intent dari halaman sebelumnya
        Intent i = getIntent();
        namasekolah = i.getStringExtra(TAG_NAMA);
        lat = Float.parseFloat(i.getStringExtra(TAG_LAT));
        lon = Float.parseFloat(i.getStringExtra(TAG_LONG));

        // dari data yang didapat dari intent maka dijadikan koordinat LatLng
        LatLng sekolah = new LatLng(lat,lon);

        //Proses inisialisasi Maps untuk inisialisasi kode API ada di folder @values/google_maps_api,xml atau di AndroidManifest.xml
        SupportMapFragment mapFrag = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mMap = mapFrag.getMap();
        mMap.setMyLocationEnabled(true);
        mMap.addMarker(new MarkerOptions() //menambahkan posisi yang dituju (data yang dipilih/di klik)
                .position(sekolah)
                .title(namasekolah)); // nama dari marker terdantung data yang di dapat dari intent
        CameraPosition cameraPosition = new CameraPosition.Builder() //mengatur posisi kamera seperti target yang dituju oleh kamera maps, ketinggian dan lain lain
                .target(sekolah)
                .zoom(17)
                .bearing(0)
                .tilt(45)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE); //jenis maps yang digunakan
        mMap.getUiSettings().setZoomControlsEnabled(true); //menampilkan kontrol zoom (tombol + dan - )
        mMap.getUiSettings().setZoomGesturesEnabled(true); // mengizinkan zoon menggunakan gesture (pinch layar smartphone)
        mMap.getUiSettings().setCompassEnabled(true); // mengizinkan mengatur komps (untuk smartphone yang mendukung kompas)
        mMap.getUiSettings().setMyLocationButtonEnabled(true); //menampilkan tombol mylocation di pojok kanan atas layar (dari kita)

        Toast.makeText(getBaseContext(), "Sukses mendapatkan "+namasekolah,Toast.LENGTH_SHORT).show();
    }

}
