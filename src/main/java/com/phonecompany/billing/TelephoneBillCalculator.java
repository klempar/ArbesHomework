package com.phonecompany.billing;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;

public interface TelephoneBillCalculator {

    BigDecimal calculate (String phoneLog) throws IOException, ParseException;

}
