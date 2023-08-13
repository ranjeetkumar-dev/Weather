package com.skye.weather

import android.os.Binder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.search.SearchView
import com.skye.weather.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Query
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.locks.Condition

class MainActivity : AppCompatActivity() {
    private val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData(cityName = "Jabalpur")
        SearchCity()

    }
    private fun SearchCity() {
        val searchView=binding.searchView
        searchView.setOnQueryTextListener(object :android.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return true

            }

        })
    }

    private fun fetchWeatherData(cityName:String) {
       val retrofit =Retrofit.Builder()
           .addConverterFactory(GsonConverterFactory.create())
           .baseUrl("https://api.openweathermap.org/data/2.5/")
           .build().create(ApiInterFace::class.java)

        val response =retrofit.getWeatherData(cityName, appid = "b29e81994068038d00f4f918337ec434", units = "metric" )
        response.enqueue(object : Callback<Weather>{
            override fun onResponse(call: Call<Weather>, response: Response<Weather>) {

                val responseBody=response.body()
                if (response.isSuccessful && responseBody !=null){

                    val temperature =responseBody.main.temp.toString()

                    val humidity=responseBody.main.humidity
                    val windSpeed=responseBody.wind.speed
                    val sunRise=responseBody.sys.sunrise.toLong()
                    val sunSet=responseBody.sys.sunset.toLong()
                    val seaLevel=responseBody.main.pressure
                    val condition=responseBody.weather.firstOrNull()?.main?: "uknown"
                    val maxTemp=responseBody.main.temp_max
                    val minTemp=responseBody.main.temp_min

                    binding.temp.text="$temperature °C"
                    binding.weather.text=condition
                    binding.maxTemp.text="Max Temp: $maxTemp °C"
                    binding.minTemp.text="Min Temp: $minTemp °C"
                    binding.humidity.text="$humidity %"
                    binding.wind.text="$windSpeed m/S"  //bugggggggggggg
                    binding.sunrise.text="${time(sunRise)}"
                    binding.sunset.text="${time(sunSet)}"
                    binding.sea.text="$seaLevel hPa"
                    binding.condition.text=condition
                    binding.day.text=dayName(System.currentTimeMillis())
                    binding.date.text=date()
                    binding.cityName.text="$cityName"


                    changeImagesAccordingToWeatherCondition(condition)

                }

            }

            override fun onFailure(call: Call<Weather>, t: Throwable) {

            }
        })

    }

    private fun changeImagesAccordingToWeatherCondition(condition: String) {
        when(condition){

            "Clear Sky","Sunny","Clear" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Partly Clouds","Clouds","Overcast","Moist","Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }


        }
        binding.lottieAnimationView.playAnimation()

    }
    private fun date(): String {
        val sdf =SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))

    }
    private fun time(timestamp: Long): String {
        val sdf =SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))

    }
    fun dayName(timestamp: Long):String{
        val sdf =SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}
