import com.phonecompany.billing.Bill;
import com.phonecompany.billing.TelephoneBillCalculator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BillingTest {


    // This test checks if the calculations are being done correctly for periods
    // spanning over midnight, partial peak hours in first 5 minutes, and periods over 5 minutes in length.
    // Result matches the amounts I got on paper, so any discrepancies are due to misunderstood instructions on my side.
    @Test
    void basicTest() {
        TelephoneBillCalculator telephoneBillingCalculator = new Bill();

        String input = """
                420774577453,13-01-2020 18:10:15,13-01-2020 18:12:57
                420774577453,13-01-2020 18:10:15,13-01-2020 18:19:57
                420774577452,13-01-2020 07:59:15,13-01-2020 08:02:57
                420774577452,13-01-2020 23:59:15,14-01-2020 00:04:57""";
        BigDecimal result = telephoneBillingCalculator.calculate(input);
        assertEquals(BigDecimal.valueOf(6.2).setScale(2, RoundingMode.HALF_UP), result, "Test failed, refer to recent changes to code");
    }
}