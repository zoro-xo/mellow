package com.looksmaxxing.mellow.Activities.Classroom.Models;

public class SubscriptionDetails {
    private String id;
    private String planId;
    private String status;
    private String currentStart;
    private String currentEnd;
    private String totalCount;
    private String paidCount;
    private String customerNotify;

    public SubscriptionDetails() {
        // Default constructor required for calls to DataSnapshot.getValue(SubscriptionDetails.class)
    }

    public SubscriptionDetails(String id, String planId, String status, String currentStart, String currentEnd, String totalCount, String paidCount, String customerNotify) {
        this.id = id;
        this.planId = planId;
        this.status = status;
        this.currentStart = currentStart;
        this.currentEnd = currentEnd;
        this.totalCount = totalCount;
        this.paidCount = paidCount;
        this.customerNotify = customerNotify;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCurrentStart() { return currentStart; }
    public void setCurrentStart(String currentStart) { this.currentStart = currentStart; }

    public String getCurrentEnd() { return currentEnd; }
    public void setCurrentEnd(String currentEnd) { this.currentEnd = currentEnd; }

    public String getTotalCount() { return totalCount; }
    public void setTotalCount(String totalCount) { this.totalCount = totalCount; }

    public String getPaidCount() { return paidCount; }
    public void setPaidCount(String paidCount) { this.paidCount = paidCount; }

    public String getCustomerNotify() { return customerNotify; }
    public void setCustomerNotify(String customerNotify) { this.customerNotify = customerNotify; }
}
