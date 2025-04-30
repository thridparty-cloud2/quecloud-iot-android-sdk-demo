package com.quectel.app.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.quectel.app.demo.ui.HomeActivity
import com.quectel.app.demo.ui.StartActivity
import com.quectel.app.usersdk.service.QuecUserService

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        QuecUserService.checkUserLoginState {
            if (it.isSuccess && it.data) {
                val intent = Intent(this@MainActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this@MainActivity, StartActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        QuecUserService.setTokenInvalidCallBack {
            val intent = Intent(this@MainActivity, StartActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}