package com.onewaytripcalltaxi.driver.earningchart;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by developer on 12/11/16.
 */
public class Earningresponse {
    public String message;
    public List<TodayEarning> today_earnings = new ArrayList<>();
    public List<WeeklyEarning> weekly_earnings = new ArrayList<>();
    int status;
    int bank_id_status;
    public String min_wallet_amount;
    public List<WithDrawArray> withdraw_array;

    public static class TodayEarning {

        public String total_trips;
        public String total_amount;
        public String total_amount_on_my_way;

    }

    public class WeeklyEarning {

        public List<Float> trip_amount = new ArrayList<>();
        public List<String> day_list = new ArrayList<>();
        public String date_text, this_week_earnings;

    }

    public class WithDrawArray {

        public String driver_wallet_pending_amount;

        public String trip_amount;

        public String trip_pending_amount;

        public String total_amount;

        public String driver_wallet_amount;

        public String driver_trip_wallet_amount;
        public String wallet_balance;

        public String driver_incetive_amount;
        public String driver_incentive_pending_amount;

    }
}
