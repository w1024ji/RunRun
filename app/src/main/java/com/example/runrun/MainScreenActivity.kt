package com.example.runrun

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.runrun.databinding.ActivityMainScreenBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayoutMediator

class MainScreenActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding : ActivityMainScreenBinding
    lateinit var toggle : ActionBarDrawerToggle

    class MyFragmentPagerAdapter(activity: FragmentActivity, private val notiNm: String, private val busNm: String, private val staNm: String, private val selectedDays: String, private val whenToWhen: String) : FragmentStateAdapter(activity) {
        private val fragments: List<Fragment>

        init {
            val listFragment = ListFragment().apply {
                arguments = Bundle().apply {
                    putString("notiNm", notiNm)
                    putString("busNm", busNm)
                    putString("staNm", staNm)
                    putString("selectedDays", selectedDays)
                    putString("whenToWhen", whenToWhen)
                }
            }
            fragments = listOf(listFragment, AddFragment(), WeatherFragment(), SatelliteFragment())
        }

        override fun getItemCount(): Int {
            return fragments.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainScreenActivity", "onCreate() 실행. onSavedInstanceState값: $savedInstanceState")
        super.onCreate(savedInstanceState)
        binding = ActivityMainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ConfirmActivity가 보낸 인텐트 데이터 받기
        val notiNm = intent.getStringExtra("notiNm") ?: ""
        val busNm = intent.getStringExtra("busNm") ?: ""
        val staNm = intent.getStringExtra("staNm") ?: ""
        val selectedDays = intent.getStringExtra("selectedDays") ?: ""
        val whenToWhen = intent.getStringExtra("whenToWhen") ?: ""
        Log.d("MainScreenActivity", "intent로 받은 notiNm값: $notiNm , busNm값: $busNm, staNm값: $staNm, selectedDays값: $selectedDays, whenToWhen값: $whenToWhen")

        // Setup ViewPager with the adapter
        val adapter = MyFragmentPagerAdapter(this, notiNm, busNm, staNm, selectedDays, whenToWhen)
        binding.viewpager.adapter = adapter
        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->
            tab.text = "TAB ${position + 1}"
        }.attach()

        // 드로어를 어떻게 가져올 거야? 액션바를 건들이면!
        toggle = ActionBarDrawerToggle(this, binding.drawer, R.string.drawer_opened, R.string.drawer_closed)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()
        binding.mainDrawerView.setNavigationItemSelectedListener(this)

    } // onCreate()

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.item_info -> {
//                Log.d("mobile app", "Navigation Menu : 메뉴 1")
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://m.duksung.ac.kr"))
                startActivity(intent)
            }
            R.id.item_map -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir/서울역/수유역/"))
                startActivity(intent)
            }
            R.id.item_gallery -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("content://media/internal/images/media"))
                startActivity(intent)
            }
            R.id.item_call -> {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:02-911"))
                startActivity(intent)
            }
            R.id.item_mail -> {
                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:w1024ji@gmail.com"))
                startActivity(intent)
            }
        }
        return false
    }

    // 이걸 연결해야 토클버튼 눌렀을 때 드로어가 열림. 이걸로 로그인 버튼 만듦
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
//        if (item.itemId==R.id.menu_login) run {
//            Toast.makeText(this, "개발 중 입니다", Toast.LENGTH_SHORT).show();
//        }
        return super.onOptionsItemSelected(item)
    }

}