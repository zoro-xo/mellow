package com.looksmaxxing.mellow.Activities.Classroom.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.looksmaxxing.mellow.Activities.Server.Server_Activity;
import com.looksmaxxing.mellow.databinding.ActivityPaymentsBinding;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentsActivity extends AppCompatActivity implements PaymentResultListener {
    ActivityPaymentsBinding binding;
    private static final String RAZORPAY_KEY_ID = "rzp_test_Y302rVCUHyavmP";
    private static final String SUBSCRIPTION_PLAN_ID = "sub_O9wRUAHFWESJQ1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        startSubscriptionFlow();
    }

    private void startSubscriptionFlow() {
        Checkout checkout = new Checkout();
        checkout.setKeyID(RAZORPAY_KEY_ID);

        JSONObject options = new JSONObject();
        try {
            options.put("subscription_plan_id", SUBSCRIPTION_PLAN_ID);
            options.put("amount", 7900);
            checkout.open(PaymentsActivity.this, options);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentId) {
        startActivity(new Intent(PaymentsActivity.this, Server_Activity.class));
        // Handle successful subscription payment
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int code, String response) {
        // Handle failed subscription payment
        Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
    }
}