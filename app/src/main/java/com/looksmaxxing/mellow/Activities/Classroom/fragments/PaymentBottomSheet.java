package com.looksmaxxing.mellow.Activities.Classroom.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.looksmaxxing.mellow.Activities.Classroom.Activities.PaymentsActivity;
import com.looksmaxxing.mellow.databinding.FragmentPaymentBottomSheetBinding;

public class PaymentBottomSheet extends BottomSheetDialogFragment {

    FragmentPaymentBottomSheetBinding binding;
    String userID;

    public PaymentBottomSheet(String userID) {
        this.userID = userID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPaymentBottomSheetBinding.inflate(inflater, container, false);
        binding.btnPay.setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(), PaymentsActivity.class);
            intent.putExtra("userId",this.userID);
            startActivity(intent);
        });


        return binding.getRoot();
    }


}