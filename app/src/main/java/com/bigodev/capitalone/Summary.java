package com.bigodev.capitalone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.reimaginebanking.api.java.models.Purchase;

public class Summary extends AppCompatActivity {

    TextView visitedVendor;
    TextView expensiveVendor;
    TextView expensivePurchase;
    TextView totalSpent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary);

        visitedVendor = (TextView) findViewById(R.id.visited_vendor);
        expensiveVendor = (TextView) findViewById(R.id.expensive_vendor);
        expensivePurchase = (TextView) findViewById(R.id.expensive_purchase);
        totalSpent = (TextView) findViewById(R.id.total_spent);

        calculateVisited();
        calculateExpensiveVendor();
        calculateExpensivePurchase();
        calculateTotal();

    }

    public void calculateVisited(){
        if(MapsActivity.purchaseCount == null){
            return;
        }

        int max = 0;
        String maxkey = "";

        for(String key : MapsActivity.purchaseCount.keySet()){
            if(MapsActivity.purchaseCount.get(key)>max){
                max = MapsActivity.purchaseCount.get(key);
                maxkey = key;
            }
        }

        visitedVendor.setText("Most Visited Vendor:\n" + MapsActivity.vendorNames.get(maxkey) + " with " + max + " visits");
    }

    public void calculateExpensiveVendor(){
        if(MapsActivity.purchaseTotal == null){
            return;
        }

        double max = 0;
        String maxkey = "";

        for(String key : MapsActivity.purchaseTotal.keySet()){
            if(MapsActivity.purchaseTotal.get(key)>max){
                max = MapsActivity.purchaseTotal.get(key);
                maxkey = key;
            }
        }

        expensiveVendor.setText("Vendor With Highest Purchase Total:\n" + MapsActivity.vendorNames.get(maxkey) + " with $" + roundOffTo2DecPlaces(max) + " spent");
    }

    public void calculateExpensivePurchase(){
        if(MapsActivity.allPurchases == null){
            return;
        }

        double max = 0;
        String maxkey = "";

        for(Purchase purchase : MapsActivity.allPurchases){
            if(purchase.getAmount()>max){
                max = purchase.getAmount();
                maxkey = purchase.getMerchant_id();;
            }
        }

        expensivePurchase.setText("Most Expensive Purchase:\nAt " + MapsActivity.vendorNames.get(maxkey) + " with $" + roundOffTo2DecPlaces(max) + " spent");
    }

    public void calculateTotal(){
        if(MapsActivity.allPurchases == null){
            return;
        }

        double total = 0;

        for(Purchase purchase : MapsActivity.allPurchases){
            total += purchase.getAmount();
        }

        totalSpent.setText("Total Spent This Week: $" + roundOffTo2DecPlaces(total));
    }

    String roundOffTo2DecPlaces(double val)
    {
        return String.format("%.2f", val);
    }
}
