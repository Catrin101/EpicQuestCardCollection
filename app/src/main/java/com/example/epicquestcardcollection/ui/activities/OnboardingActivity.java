package com.example.epicquestcardcollection.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.viewpager2.widget.ViewPager2;

import com.example.epicquestcardcollection.R;
import com.example.epicquestcardcollection.base.BaseActivity;
import com.example.epicquestcardcollection.data.local.SessionManager;
import com.example.epicquestcardcollection.ui.adapters.OnboardingPagerAdapter;
import com.example.epicquestcardcollection.utils.AppConstants;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

/**
 * Actividad de onboarding que muestra una introducción a la aplicación.
 * Permite a los usuarios saltar o navegar por las pantallas de bienvenida.
 */
public class OnboardingActivity extends BaseActivity {

    private ViewPager2 viewPagerOnboarding;
    private SpringDotsIndicator dotsIndicator;
    private Button btnSkip;
    private Button btnNext;

    private OnboardingPagerAdapter pagerAdapter;
    private SessionManager sessionManager;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_onboarding;
    }

    @Override
    protected void initializeUI() {
        viewPagerOnboarding = findViewById(R.id.viewPagerOnboarding);
        dotsIndicator = findViewById(R.id.dotsIndicator);
        btnSkip = findViewById(R.id.btnSkip);
        btnNext = findViewById(R.id.btnNext);

        sessionManager = SessionManager.getInstance(this);
        setupViewPager();
        updateButtonText();
    }

    @Override
    protected void setupListeners() {
        btnSkip.setOnClickListener(v -> skipOnboarding());
        btnNext.setOnClickListener(v -> handleNextButtonClick());

        viewPagerOnboarding.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateButtonText();
            }
        });
    }

    private void setupViewPager() {
        // Recursos para cada pantalla de onboarding
        int[] imageResources = {
                R.drawable.ic_onboarding_1,
                R.drawable.ic_onboarding_2,
                R.drawable.ic_onboarding_3
        };

        int[] titleResources = {
                R.string.onboarding_title_1,
                R.string.onboarding_title_2,
                R.string.onboarding_title_3
        };

        int[] descriptionResources = {
                R.string.onboarding_description_1,
                R.string.onboarding_description_2,
                R.string.onboarding_description_3
        };

        pagerAdapter = new OnboardingPagerAdapter(
                this,
                imageResources,
                titleResources,
                descriptionResources
        );

        viewPagerOnboarding.setAdapter(pagerAdapter);
        dotsIndicator.setViewPager2(viewPagerOnboarding);
    }

    private void updateButtonText() {
        int currentItem = viewPagerOnboarding.getCurrentItem();
        if (currentItem == pagerAdapter.getItemCount() - 1) {
            btnNext.setText(R.string.get_started);
            btnSkip.setVisibility(View.GONE);
        } else {
            btnNext.setText(R.string.next);
            btnSkip.setVisibility(View.VISIBLE);
        }
    }

    private void handleNextButtonClick() {
        int currentItem = viewPagerOnboarding.getCurrentItem();
        if (currentItem < pagerAdapter.getItemCount() - 1) {
            viewPagerOnboarding.setCurrentItem(currentItem + 1);
        } else {
            completeOnboarding();
        }
    }

    private void skipOnboarding() {
        completeOnboarding();
    }

    private void completeOnboarding() {
        // Marcar que el onboarding fue completado
        sessionManager.setFirstTimeCompleted();

        // Navegar a la siguiente actividad (Login o Dashboard según sesión)
        navigateToNextScreen();
    }

    private void navigateToNextScreen() {
        // TODO: En futuras iteraciones, verificar si hay sesión activa
        // Por ahora siempre vamos al Login
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
