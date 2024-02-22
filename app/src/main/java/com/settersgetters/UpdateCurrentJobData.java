package com.settersgetters;

import java.util.HashMap;

/**
 * Created by AK on 12/21/2016.
 */

public class UpdateCurrentJobData {

    public static HashMap<String,String>  updateJobData(){
        HashMap<String,String> currentJobData = new  HashMap<String,String>();
        currentJobData.put("jobType","0");currentJobData.put("JobId","0");
        currentJobData.put("pickupLoc","0");currentJobData.put("dropLoc","0");
        currentJobData.put("name","0");currentJobData.put("mobileNo","0");
        currentJobData.put("pickupLatitude","0");currentJobData.put("pickupLongitude","0");
        currentJobData.put("dropLatitude","0");currentJobData.put("dropLongitude","0");
        currentJobData.put("baseFare","0");currentJobData.put("waitingTime","0");
        currentJobData.put("totalTripDist","0");currentJobData.put("totalTripDuration","0");
        currentJobData.put("startTime","0");currentJobData.put("endTime","0");
        currentJobData.put("startDate","0");currentJobData.put("endDate","0");
        currentJobData.put("totalBill","0");currentJobData.put("paymentMode","0");
        currentJobData.put("hotspotStatus","0");currentJobData.put("jobStatus","0");
        currentJobData.put("startDateTimes","0"); currentJobData.put("customerPickUpTime","0");
        currentJobData.put("htmlBill","0");currentJobData.put("promoCode","0");
        currentJobData.put("typeOfTrip","0");currentJobData.put("runningFare","0");currentJobData.put("startGpsOdo","0.0");
        return currentJobData;
    }

}
