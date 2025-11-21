package com.example.epicquestcardcollection.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.epicquestcardcollection.ui.activities.OnboardingFragment;

/**
 * Adaptador para el ViewPager del onboarding.
 * Gestiona la creación de los fragments para cada pantalla.
 */
public class OnboardingPagerAdapter extends FragmentStateAdapter {

    // Arrays con los recursos para cada pantalla
    private final int[] imageResources;
    private final int[] titleResources;
    private final int[] descriptionResources;

    public OnboardingPagerAdapter(@NonNull FragmentActivity fragmentActivity,
                                  int[] imageResources,
                                  int[] titleResources,
                                  int[] descriptionResources) {
        super(fragmentActivity);
        this.imageResources = imageResources;
        this.titleResources = titleResources;
        this.descriptionResources = descriptionResources;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return OnboardingFragment.newInstance(
                imageResources[position],
                titleResources[position],
                descriptionResources[position]
        );
    }

    @Override
    public int getItemCount() {
        return imageResources.length;
    }
}
