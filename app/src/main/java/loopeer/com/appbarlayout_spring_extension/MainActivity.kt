package loopeer.com.appbarlayout_spring_extension

import android.content.Intent
import android.os.Bundle
import android.view.View

import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onNormalAppBarLayoutClick(view: View) {
        startActivity(Intent(this, NormalAppBarLayoutActivity::class.java))
    }

    fun onSpringAppBarLayoutClick(view: View) {
        startActivity(Intent(this, SpringAppBarLayoutActivity::class.java))
    }

    fun onSpringTabAppBarLayoutClick(view: View) {
        startActivity(Intent(this, SpringAppBarLayoutWithTabActivity::class.java))
    }
}
