package com.phonecompany.billing;

public class Main {
    public static void main(String[] args) {

        String input = """
                420774577453,13-01-2020 18:10:15,13-01-2020 18:12:57
                420774577453,13-01-2020 18:10:15,13-01-2020 18:19:57
                420774577452,13-01-2020 07:59:15,13-01-2020 08:02:57
                420774577452,13-01-2020 23:59:15,14-01-2020 00:04:57""";

        TelephoneBillCalculator telephoneBillingCalculator = new Bill();

        System.out.println("The total billed amount for provided input is: " + telephoneBillingCalculator.calculate(input) + " CZK");
    }
}
