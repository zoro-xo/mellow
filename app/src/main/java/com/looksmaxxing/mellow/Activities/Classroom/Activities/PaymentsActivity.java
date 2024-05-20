package com.looksmaxxing.mellow.Activities.Classroom.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.looksmaxxing.mellow.Activities.Server.Server_Activity;
import com.looksmaxxing.mellow.databinding.ActivityPaymentsBinding;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PaymentsActivity extends AppCompatActivity implements PaymentResultListener {
    ActivityPaymentsBinding binding;
    private static final String RAZORPAY_KEY_ID = "rzp_test_Y302rVCUHyavmP";
    private static final String SUBSCRIPTION_PLAN_ID = "sub_O9wRUAHFWESJQ1";
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        this.userID = getIntent().getStringExtra("userId");
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
        updatePaymentStatus();
        startActivity(new Intent(PaymentsActivity.this, Server_Activity.class));
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
    }

    private void updatePaymentStatus() {
        DatabaseReference refUserRegister = FirebaseDatabase.getInstance().getReference().child("Users").child("Registration").child(userID);

        HashMap<String, Object> updatedData = new HashMap<>();
        updatedData.put("isPaid", true);
        refUserRegister.updateChildren(updatedData);
    }

    @Override
    public void onPaymentError(int code, String response) {
        // Handle failed subscription payment
        Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
    }
}