package com.example.epicquestcardcollection.ui.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.example.epicquestcardcollection.R;

/**
 * Fragment para cada pantalla del onboarding.
 * Muestra imagen, título y descripción configurables.
 */
public class OnboardingFragment extends Fragment {

    private static final String ARG_IMAGE_RES = "image_res";
    private static final String ARG_TITLE_RES = "title_res";
    private static final String ARG_DESCRIPTION_RES = "description_res";

    private ImageView imageOnboarding;
    private TextView textTitle;
    private TextView textDescription;

    public OnboardingFragment() {
        // Required empty public constructor
    }

    /**
     * Crea una nueva instancia del fragment con los recursos especificados
     */
    public static OnboardingFragment newInstance(@DrawableRes int imageRes,
                                                 @StringRes int titleRes,
                                                 @StringRes int descriptionRes) {
        OnboardingFragment fragment = new OnboardingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_IMAGE_RES, imageRes);
        args.putInt(ARG_TITLE_RES, titleRes);
        args.putInt(ARG_DESCRIPTION_RES, descriptionRes);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_onboarding, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupContent();
    }

    private void initViews(View view) {
        imageOnboarding = view.findViewById(R.id.imageOnboarding);
        textTitle = view.findViewById(R.id.textTitle);
        textDescription = view.findViewById(R.id.textDescription);
    }

    private void setupContent() {
        Bundle args = getArguments();
        if (args != null) {
            int imageRes = args.getInt(ARG_IMAGE_RES);
            int titleRes = args.getInt(ARG_TITLE_RES);
            int descriptionRes = args.getInt(ARG_DESCRIPTION_RES);

            imageOnboarding.setImageResource(imageRes);
            textTitle.setText(titleRes);
            textDescription.setText(descriptionRes);
        }
    }
}