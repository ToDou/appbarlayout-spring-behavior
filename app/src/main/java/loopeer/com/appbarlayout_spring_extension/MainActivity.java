package loopeer.com.appbarlayout_spring_extension;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onNormalAppBarLayoutClick(View view) {
        startActivity(new Intent(this, NormalAppBarLayoutActivity.class));
    }

    public void onSpringAppBarLayoutClick(View view) {
        startActivity(new Intent(this, SpringAppBarLayoutActivity.class));
    }

    public void onSpringTabAppBarLayoutClick(View view) {
        startActivity(new Intent(this, SpringAppBarLayoutWithTabActivity.class));
    }

    public void onFlingFixTabAppBarLayoutClick(View view) {
        startActivity(new Intent(this, AppBarFlingFixTabActivity.class));
    }
}
