package com.example.runrun

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.runrun.databinding.ActivityMainScreenBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayoutMediator

class MainScreenActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding : ActivityMainScreenBinding
    lateinit var toggle : ActionBarDrawerToggle
    // sharedPreferences를 위한 변수
    lateinit var sharedPreference : SharedPreferences
    // 로그인 auth 구현 중
    lateinit var headerView : View


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

    override fun onResume() {
        super.onResume()
        // sharedPreference 연결
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(this)
        val color = sharedPreference.getString("color", "#00ff00")
        binding.tabs.setBackgroundColor(Color.parseColor(color))
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
        // drawer에서 메뉴를 처리하기 위해서
        binding.mainDrawerView.setNavigationItemSelectedListener(this)

        // auth 로그인 구현 중
        headerView = binding.mainDrawerView.getHeaderView(0) // 내비게이션 뷰의 헤더 레이아웃
        val button = headerView.findViewById<Button>(R.id.btnAuth) // 로그인 버튼 가져오기
        button.setOnClickListener {
            Log.d("MainScreenAcitivity", "로그인 버튼의 setOnClickListener")
            val intent = Intent(this, AuthActivity::class.java)
            if(button.text.equals("로그인")){
                intent.putExtra("status", "logout")
            }
            else if (button.text.equals("로그아웃")){
                intent.putExtra("status", "login")
            }

            startActivity(intent)
            binding.drawer.closeDrawers()
        }

    } // onCreate()

    override fun onStart() {
        super.onStart()

        val button = headerView.findViewById<Button>(R.id.btnAuth)
        val tv = headerView.findViewById<TextView>(R.id.tvID)

        if(MyApplication.checkAuth()){
            button.text = "로그아웃"
            tv.text = "${MyApplication.email}님 \n 반갑습니다!"
        }
        else{
            button.text = "로그인"
            tv.text = "안녕하세요.."
        }
    }

    // Drawer 메뉴
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

//            R.id.item_info -> {
////                Log.d("mobile app", "Navigation Menu : 메뉴 1")
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://m.duksung.ac.kr"))
//                startActivity(intent)
//            }
//            R.id.item_map -> {
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir/서울역/수유역/"))
//                startActivity(intent)
//            }
//            R.id.item_gallery -> {
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("content://media/internal/images/media"))
//                startActivity(intent)
//            }
//            R.id.item_call -> {
//                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:02-911"))
//                startActivity(intent)
//            }
//            R.id.item_mail -> {
//                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:w1024ji@gmail.com"))
//                startActivity(intent)
//            }
        }
        return false
    }

    // 이걸 연결해야 토클버튼 눌렀을 때 드로어가 열림. 이걸로 로그인 버튼 만듦
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        /*
           if (item.itemId==R.id.menu_login) run {
           Toast.makeText(this, "개발 중 입니다", Toast.LENGTH_SHORT).show();
        }
         */
        if(item.itemId == R.id.menu_main_setting){
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_setting, menu) // 인자menu에 menu_setting을 연결
        return super.onCreateOptionsMenu(menu)
    }


}