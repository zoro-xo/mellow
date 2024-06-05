package com.looksmaxxing.mellow.Activities.Classroom.Activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.looksmaxxing.mellow.Activities.Classroom.Models.SubscriptionDetails;
import com.looksmaxxing.mellow.Activities.GetStartedActivity;
import com.looksmaxxing.mellow.Activities.Server.Server_Activity;
import com.looksmaxxing.mellow.databinding.ActivityPaymentsBinding;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.razorpay.RazorpayClient;
import com.razorpay.Subscription;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PaymentsActivity extends AppCompatActivity implements PaymentResultListener {
    ActivityPaymentsBinding binding;
    private static final String RAZORPAY_KEY_ID = "rzp_live_dcHv9S6F6BKaDY";
    private static final String YOUR_KEY_SECRET = "Iwxci5njliV1G3wZCmEHrPei";
    private static final String PLAN_ID = "plan_OIZZCoQHe272wA";
    String userID;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        this.userID = getIntent().getStringExtra("userId");
//        startSubscriptionFlow();
        new CreateSubscriptionTask().execute();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        addUserToGroup(userID, "Uni_Group_No_1_Looksmaxxing with Aswan");
    }

    private class CreateSubscriptionTask extends AsyncTask<Void, Void, Subscription> {

        @Override
        protected Subscription doInBackground(Void... voids) {
            try {
                RazorpayClient razorpay = new RazorpayClient(RAZORPAY_KEY_ID, YOUR_KEY_SECRET);

                JSONObject subscriptionRequest = new JSONObject();
                subscriptionRequest.put("plan_id", PLAN_ID);
                subscriptionRequest.put("total_count", 24);
                subscriptionRequest.put("quantity", 1);
                subscriptionRequest.put("customer_notify", 1);

                Subscription subscription = razorpay.Subscriptions.create(subscriptionRequest);
                Log.d("Subscription", "Subscription ID: " + subscription.get("id"));

                saveSubscriptionDetailsToFirebase(subscription);

                return subscription;
            } catch (Exception e) {
                Log.e("SubscriptionError", "Error creating subscription", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Subscription subscription) {
            if (subscription != null) {
                startSubscriptionFlow(subscription.get("id"));
            } else {
                // Handle subscription creation failure
                Toast.makeText(PaymentsActivity.this, "Failed to create subscription", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveSubscriptionDetailsToFirebase(Subscription subscription) {

        SubscriptionDetails subscriptionDetails = new SubscriptionDetails(
                subscription.get("id"),
                subscription.get("plan_id"),
                subscription.get("status"),
                subscription.get("current_start").toString(),
                subscription.get("current_end").toString(),
                subscription.get("total_count").toString(),
                subscription.get("paid_count").toString(),
                subscription.get("customer_notify").toString()
        );

        DatabaseReference userSubscriptionsRef = mDatabase.child("Users").child("Subscriptions").child(userID);
        userSubscriptionsRef.setValue(subscriptionDetails)
                .addOnSuccessListener(aVoid -> Log.d("PAYY01", "Subscription details saved successfully!"))
                .addOnFailureListener(e -> Log.e("PAYY02", "Failed to save subscription details", e));

    }


    private void startSubscriptionFlow(String subscriptionID) {
        Checkout checkout = new Checkout();
        checkout.setKeyID(RAZORPAY_KEY_ID);

        JSONObject options = new JSONObject();
        try {
            options.put("subscription_plan_id", subscriptionID);
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
        startActivity(new Intent(PaymentsActivity.this, GetStartedActivity.class));
    }

    private void addUserToGroup(String userId, String groupId) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userUid = currentUser.getUid();
            DatabaseReference userGroupRef = mDatabase.child("user_groups")
                    .child(userUid)
                    .child(groupId);

            // Example data to be added
            Map<String, Object> userGroup = new HashMap<>();
            userGroup.put("addedOrJoined", "Added");
            String dateTime = getCurrentDateTime();
            userGroup.put("dateTime", dateTime);
            // Set the data in Realtime Database
            userGroupRef.setValue(userGroup)
                    .addOnSuccessListener(aVoid -> {
//                        Toast.makeText(this, "User added to group successfully!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "");
                    })
                    .addOnFailureListener(e -> {
//                        Toast.makeText(this, "Error adding user to group", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error adding user to group", e);
                    });
        } else {
            Log.w(TAG, "Current user is null");
        }

    }

    private String getCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
        return dateFormat.format(calendar.getTime());
    }
}