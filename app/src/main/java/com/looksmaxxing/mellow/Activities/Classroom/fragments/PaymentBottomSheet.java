package com.looksmaxxing.mellow.Activities.Classroom.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.looksmaxxing.mellow.Activities.Classroom.Activities.PaymentsActivity;
import com.looksmaxxing.mellow.R;
import com.looksmaxxing.mellow.databinding.FragmentPaymentBottomSheetBinding;

public class PaymentBottomSheet extends BottomSheetDialogFragment {

    FragmentPaymentBottomSheetBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPaymentBottomSheetBinding.inflate(inflater, container, false);
        binding.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), PaymentsActivity.class);
                startActivity(intent);

            }
        });


        return binding.getRoot();
    }


}