package com.network.plantfyth;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.network.plantfyth.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private Integer usuarioId;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.activity_main); // ← só isso, sem binding

        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,R.id.navigation_chatbot, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();

        NavController navController = Navigation.findNavController(
                this, R.id.nav_host_fragment_activity_main);


        NavigationUI.setupWithNavController(navView, navController);

        View rootView = findViewById(R.id.container);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect visibleFrame = new Rect();
            rootView.getWindowVisibleDisplayFrame(visibleFrame);
            int totalHeight = rootView.getRootView().getHeight();
            int heightDiff = totalHeight - visibleFrame.height();
            boolean keyboardVisible = heightDiff > totalHeight * 0.15;
            navView.setVisibility(keyboardVisible ? View.GONE : View.VISIBLE);
        });
    }
}
