package com.example.runrun

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.runrun.databinding.ActivityMainScreenBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainScreenActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, ListFragment.OnFabClickListener {
    lateinit var binding : ActivityMainScreenBinding
    lateinit var toggle : ActionBarDrawerToggle
    // sharedPreferences를 위한 변수
    lateinit var sharedPreference : SharedPreferences
    // 로그인 auth 구현 중
    lateinit var headerView : View
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    class MyFragmentPagerAdapter(activity: FragmentActivity, private val notiNm: String?, private val busNm: String?, private val staNm: String?, private val selectedDays: String?, private val whenToWhen: String?, private val uri: String?) : FragmentStateAdapter(activity) {
        private val fragments: List<Fragment>

        init {
            val listFragment = ListFragment().apply {
                arguments = Bundle().apply {
                    putString("notiNm", notiNm)
                    putString("busNm", busNm)
                    putString("staNm", staNm)
                    putString("selectedDays", selectedDays)
                    putString("whenToWhen", whenToWhen)
                    Log.d("MainScreenActivity", "MyFragmentPagerAdapter-uri값: $uri")
                    putString("uri", uri.toString()) // 이미지 업로드 구현 중
                }
            }
            fragments = listOf(listFragment, AddFragment(), YoutubeFragment(), SatelliteFragment())
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
        val notiNm = intent.getStringExtra("notiNm")
        val busNm = intent.getStringExtra("busNm")
        val staNm = intent.getStringExtra("staNm")
        val selectedDays = intent.getStringExtra("selectedDays")
        val whenToWhen = intent.getStringExtra("whenToWhen")
        var uri = intent.getStringExtra("uri") // 이미지 업로드 구현 중
        Log.d("MainScreenActivity", "onCreate()-intent로 받은 uri값: $uri")

        // Initialize ViewPager and TabLayout
        viewPager = binding.viewpager
        tabLayout = binding.tabs

        val adapter = MyFragmentPagerAdapter(this, notiNm, busNm, staNm, selectedDays, whenToWhen, uri)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "List"
                1 -> "Add"
                2 -> "Weather"
                3 -> "Satellite"
                else -> null
            }
        }.attach()

        // Add Tab Selection Listener
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 1 && !MyApplication.checkAuth()) {
                    Log.d("MainScreenActivity", "onTabSelected - 인증 안하고 Add 탭 선택")
                    // If Add tab is selected and user is not authenticated
                    Toast.makeText(baseContext, "Please authenticate first!", Toast.LENGTH_LONG).show()
                    // Prevent selecting the tab
                    Handler(Looper.getMainLooper()).post {
                        tabLayout.selectTab(tabLayout.getTabAt(0))
                    }
                }
            }
            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }
            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })

        // 드로어를 어떻게 가져올 거야? 액션바를 건들이면!
        toggle = ActionBarDrawerToggle(this, binding.drawer, R.string.drawer_opened, R.string.drawer_closed)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()
        binding.mainDrawerView.setNavigationItemSelectedListener(this) // drawer에서 메뉴를 처리하기 위해서

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
            // TODO(" 추가하거나 제거하거나 하시오~ ")
        }
        return false
    }

    // 이걸 연결해야 토클버튼 눌렀을 때 드로어가 열림. 이걸로 로그인 버튼 만듦
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
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

    override fun onFabClick() {
        viewPager.currentItem = 1 // AddFragment로 넘기기
    }

}