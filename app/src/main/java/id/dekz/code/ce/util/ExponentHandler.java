package id.dekz.code.ce.util;

import java.math.BigDecimal;

/**
 * Created by DEKZ on 2/27/2016.
 */
public class ExponentHandler {

    public ExponentHandler() {
    }

    public String removeExponent(String amount) {
        return String.valueOf(new BigDecimal(amount));
    }
}
