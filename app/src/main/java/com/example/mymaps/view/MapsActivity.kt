package com.example.mymaps.view

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.example.mymaps.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.mymaps.databinding.ActivityMapsBinding
import com.example.mymaps.model.place
import com.example.mymaps.roomdb.PlaceDao
import com.example.mymaps.roomdb.PlaceDb
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager : LocationManager
    private lateinit var locationListener : LocationListener
    private lateinit var permissionlauncher : ActivityResultLauncher<String>
    private lateinit var sharedPreferences: SharedPreferences
    private var trackBoolean : Boolean? = null
    private var selectedlang : Double? = null
    private var selectedlat : Double? = null
    private lateinit var db : PlaceDb
    private lateinit var placedao : PlaceDao
    val compositeDisposable = CompositeDisposable()
    var placefromMain : place? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        registerLauncher()
        sharedPreferences = this.getSharedPreferences("com.example.mymaps", MODE_PRIVATE)
        trackBoolean = false
        selectedlang = 0.0
        selectedlat = 0.0

        db = Room.databaseBuilder(applicationContext, PlaceDb::class.java,"Places").build()
        placedao = db.PlaceDao()
        binding.saveButton.isEnabled = false
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this)

        val intent = intent
        val info = intent.getStringExtra("info")

        if(info == "new"){
            binding.saveButton.visibility = View.VISIBLE
            binding.deleteButton.visibility = View.GONE
            /*
        Location manager'?? tan??ml??yoruz. Location manager konuma dair t??m i??lemlerimizi yapar.
        Ayr??ca getSystem servis ile android sisteminin bir ??ok ??zelli??ine eri??ebiliyoruz. Biz sadece
        lokasyon servisini kullanaca????z ama, android studio bizim tam olarak lokasyon servisi d??nd??rd??????m??z?? anlam??yor.
        Bu sebep ile castin dedi??imiz i??lemi uyguluyoruz.(??ne??in as locationmaner yaz??yoruz.)
        */
            locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
            /* LocationManger bizim i??in bir??ok fonksiyon bar??nd??r??yor. Anl??k konum, h??z, v.b.*. Biz g??ncellendik??e olan??,
            alaca????z.
             */

            locationListener = object : LocationListener{
                override fun onLocationChanged(location: Location) {
                    //Ders 49. Son bilnene konumu almak. Kullan??c??n??n konumunu almal?? ve oraya bir i??aretleyici eklemeliyiz.
                    trackBoolean = sharedPreferences.getBoolean("trackBoolean", false)
                    if(trackBoolean == false){
                        val userLocation =LatLng(location.latitude, location.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                        sharedPreferences.edit().putBoolean("trackBoolean", true).apply()
                    }
                }

                override fun onProviderEnabled(provider: String) {
                    super.onProviderEnabled(provider)
                }

                override fun onProviderDisabled(provider: String) {
                    super.onProviderDisabled(provider)
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                }


                //Location listener aray??z??, s??n??f?? i??inde bir ??ok metod bar??nd??r??r.
                //Aray??zlerle (oop san??rsam.) ??al????maya bakmam laz??m.
            }
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                    //Bu k??s??mda izin verilmedi durumunu ele alaca????z.
                    Snackbar.make(binding.root, "Permission needed for Location", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                        //??zin ver butonuna t??klan??rsa Butona t??klarsak ne olaca????n?? yazd??????m??z yer.
                        permissionlauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }.show()

                }else{
                    permissionlauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }

            } else{
                //permission granted(izin verildi)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f, locationListener)
                //Son konumu alabiliriz.
                val lastlac = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                //Son konum olmayabilir. Bu y??zden if ile kontrol yap??yoruz.
                if(lastlac != null){
                    val lastUserLocation = LatLng(lastlac.latitude, lastlac.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15f))
                }
                mMap.isMyLocationEnabled = true
            }

        }else{
            mMap.clear()

            placefromMain = intent.getSerializableExtra("place") as? place

            placefromMain?.let {
                val latlng = LatLng(it.lat,it.lang)

                mMap.addMarker(MarkerOptions().position(latlng).title(it.name))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,13f))
                binding.placeName.setText(it.name)
                binding.saveButton.visibility = View.GONE
                binding.deleteButton.visibility = View.VISIBLE
            }
        }


        //??htiyca??m??z olan t??m ????eleri tan??mlad??k ama kullan??c??dan izin almay??da tan??mlamam??z gerikiyor. Hen??z kullanamay??z, ilk ??nce yukarda izni yaz??yoruz.

        /*
        locationListener = LocationListener { location ->
        bu bir y??ntem. Ba??ka bir y??ntem daha deniyece??iz. Ve o daha do??ru gibi.


        }*/


    }

    private fun registerLauncher(){

        permissionlauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){result ->
            if(result){
               //Permission granted (??zin verildi)
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f, locationListener)
                    val lastlac = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    //Son konum olmayabilir. Bu y??zden if ile kontrol yap??yoruz.
                    if(lastlac != null) {
                        val lastUserLocation = LatLng(lastlac.latitude, lastlac.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15f))
                    }
                    mMap.isMyLocationEnabled = true
                }

            }else{
                Toast.makeText(this@MapsActivity,"Permission Needed!", Toast.LENGTH_LONG).show()
            }

        }

    }

    override fun onMapLongClick(latLng: LatLng) {
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(latLng))
        selectedlat = latLng.latitude
        selectedlang = latLng.longitude
        binding.saveButton.isEnabled = true

    }
    fun save(view : View){

        if(selectedlat != null && selectedlang != null){
            val place = place(binding.placeName.text.toString(), selectedlat!!, selectedlang!!)
            //RxJava Kullan??m?? burada ba??l??yor.
            compositeDisposable.add(
                placedao.insert(place)
                    .subscribeOn(Schedulers.io())//io threadine giriyoruz.
                    .observeOn(AndroidSchedulers.mainThread())//Android'de g??zlemleyece??imizi beliritoruz.
                    .subscribe(this::handleResponse)//Buraya referans verip bitince bu fonksiyon ??al????t??r??lacak diyoruz.
            )

        }

    }
    //i??lem bitince ana men??ye d??nemizi sa??layacak fonksiyon.
    private fun handleResponse(){
        val intent = Intent(this,MainActivity::class.java)
        //????lem bittikten sonra t??m aktiviteleri temizleyecek
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        //sonra main aktivitiye d??necek.
        startActivity(intent)
    }

    fun delete(view: View){
        placefromMain?.let {
            compositeDisposable.add(
                placedao.delete(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponse)


            )
        }



    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}