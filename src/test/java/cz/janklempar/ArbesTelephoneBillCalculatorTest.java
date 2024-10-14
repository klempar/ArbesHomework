package cz.janklempar;


import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class ArbesTelephoneBillCalculatorTest {

    ArbesTelephoneBillCalculator calculator;

    @Before
    public void setUp()  {
        calculator = new ArbesTelephoneBillCalculator();
    }

    @Test
    public void calculate_testFromDocumentation()  {
        String inputCSV =
                "420774577453,13-01-2020 18:10:15,13-01-2020 18:12:57\n" +
                "420776562353,18-01-2020 08:59:20,18-01-2020 09:10:00";
        BigDecimal actual = calculator.calculate(inputCSV);
        BigDecimal expected = new BigDecimal("1.50");

        assertEquals(expected, actual);

    }

    @Test
    public void calculate_testOkInput_moreRows() {

        String inputCSV =
                "420774577453,13-01-2020 08:10:00,13-01-2020 08:15:00\n" +
                        "420774577454,13-01-2020 09:00:00,13-01-2020 09:10:00\n" +
                        "420774577455,13-01-2020 09:30:00,13-01-2020 09:40:00\n" +
                        "420774577453,13-01-2020 10:00:00,13-01-2020 10:12:00\n" +
                        "420774577454,13-01-2020 10:30:00,13-01-2020 10:35:00\n" +
                        "420774577456,13-01-2020 11:00:00,13-01-2020 11:20:00\n" +
                        "420774577457,13-01-2020 11:30:00,13-01-2020 11:45:00\n" +
                        "420774577455,13-01-2020 12:00:00,13-01-2020 12:10:00\n" +
                        "420774577453,13-01-2020 12:20:00,13-01-2020 12:25:00\n" +
                        "420774577454,13-01-2020 12:40:00,13-01-2020 12:50:00\n" +
                        "420774577456,13-01-2020 13:00:00,13-01-2020 13:10:00\n" +
                        "420774577457,13-01-2020 13:20:00,13-01-2020 13:30:00\n" +
                        "420774577455,13-01-2020 13:40:00,13-01-2020 13:50:00\n" +
                        "420774577453,13-01-2020 14:00:00,13-01-2020 14:10:00\n" +
                        "420774577454,13-01-2020 14:20:00,13-01-2020 14:30:00\n" +
                        "420774577456,13-01-2020 14:40:00,13-01-2020 14:55:00\n" +
                        "420774577455,13-01-2020 15:10:00,13-01-2020 15:25:00\n" +
                        "420774577453,13-01-2020 15:30:00,13-01-2020 15:35:00\n" +
                        "420774577454,13-01-2020 15:40:00,13-01-2020 15:50:00\n" +
                        "420774577455,13-01-2020 16:00:00,13-01-2020 16:10:00";

        // 420774577453 je voláno 5x.
        // 420774577454 je voláno 5x. celková očekávaná cena 29.0 Kč
        // 420774577455 je voláno 5x.

        // 420774577456 je voláno 3x.
        // 420774577457 je voláno 2x.


        BigDecimal actual = calculator.calculate(inputCSV);
        BigDecimal expected = new BigDecimal("89.90");

        assertEquals(expected, actual);

    }

    @Test
    public void calculate_okInput_A() {
        String inputCSV =
                "420774577453,13-01-2020 07:50:00,13-01-2020 08:15:00\n" +
                        "420774577454,13-01-2020 16:10:00,13-01-2020 16:15:30\n" +
                        "420774577455,13-01-2020 15:55:00,13-01-2020 16:05:00\n" +
                        "420774577453,14-01-2020 09:00:00,14-01-2020 09:10:00\n" +
                        "420774577454,14-01-2020 11:10:00,14-01-2020 11:15:00";
        BigDecimal actual = calculator.calculate(inputCSV);
        BigDecimal expected = new BigDecimal("13.20");

        assertEquals(expected, actual);
    }


    @Test
    public void calculate_testOneNumberInput()  {
        String inputCSV =
                "420774577453,13-01-2020 18:10:15,13-01-2020 18:12:57\n";

        BigDecimal actual = calculator.calculate(inputCSV);
        BigDecimal expected = new BigDecimal("0.00");
        assertEquals(expected, actual);

    }

    @Test
    public void calculate_testEndedTheSameTime()  {
        // nenašel jsem v zadání. Bral jsem jako špatný input
        String inputCSV =
                "420774577453,13-01-2020 18:10:15,13-01-2020 18:10:15\n";
        BigDecimal actual = calculator.calculate(inputCSV);
        BigDecimal expected = new BigDecimal("0.00");
        assertEquals(expected, actual);

    }

    @Test
    public void calculate_invalidNumberA()  {
        String inputCSV =  "a1b3,13-01-2020 18:10:15,13-01-2020 18:12:57\n";
        BigDecimal actual = calculator.calculate(inputCSV);
        BigDecimal expected = new BigDecimal("0.00");
        assertEquals(expected, actual);

    }
    @Test
    public void calculate_invalidNumberB()  {
        String inputCSV = "420774577453,13-01-2020 07:50:00,13-01-2020 08:15:00\n" +
                "4207745774x4,13-01-2020 08:10:00,13-01-2020 08:15:00\n" +  // Neplatné číslo
                "4207745@7455,13-01-2020 09:00:00,13-01-2020 09:30:00\n" +  // Neplatné číslo
                "420774577456,13-01-2020 10:00:00,13-01-2020 10:45:00\n" +
                "420774577457,13-01-2020 11:00:00,13-01-2020 11:30:00";
        BigDecimal actual = calculator.calculate(inputCSV);
        BigDecimal expected = new BigDecimal("16.50");
        assertEquals(expected, actual);

    }


    @Test
    public void calculate_invalidInputDate_A() {
        String inputCSV =  "420774577453,13-01-200:15,13-01-2020 18:12:57\n";
        BigDecimal actual = calculator.calculate(inputCSV);
        BigDecimal expected = new BigDecimal("0.00");
        assertEquals(expected, actual);

    }

    @Test
    public void calculate_invalidInputDate_B() {
        String inputCSV = "420774577453,13-01-2020 07:50:00,13-01-2020 08:15:00\n" +
                "420774577454,13-01-2020 08:10:00,13-01-2020 08:15:00\n" +
                "420774577455,13-01-2020 09-00-2020 09:00:00,13-01-2020 09:30:00\n" +  // Špatný formát data
                "420774577456,13-01-2020 10:00:00,13-01-2020 10:45:00\n" +
                "420774577457,13-01-2020 11:00:00,13-01-2020 11:30:00";
        BigDecimal actual = calculator.calculate(inputCSV);
        BigDecimal expected = new BigDecimal("21.50");
        assertEquals(expected, actual);

    }


    @Test
    public void calculate_invalidInput_endBeforeStart() {
        String inputCSV = "420774577453,13-01-2020 07:50:00,13-01-2020 08:15:00\n" +
                "420774577454,13-01-2020 08:10:00,13-01-2020 08:15:00\n" +
                "420774577455,13-01-2020 09-00-2020 09:00:00,13-01-2020 09:30:00\n" +  // Špatný formát data
                "420774577456,13-01-2020 10:00:00,13-01-2020 10:45:00\n" +
                "420774577457,13-01-2020 11:00:00,13-01-2020 11:30:00";
        BigDecimal actual = calculator.calculate(inputCSV);
        BigDecimal expected = new BigDecimal("21.50");
        assertEquals(expected, actual);

    }

    @Test
    public void calculate_invalidNumberOfParameters() {
        String inputCSV = "420774577453,13-01-2020 07:50:00,13-01-2020 08:15:00\n" +
                "4207745774x3,13-01-2020 16:10:00,13-01-2020 16:15:30\n" +
                "420774577455,13-01-2020 15:55:00,13-01-2020 16:05:00\n" +
                "420774577453,14-01-2020 09:00:00,14-01-2020 09:10:00\n" +
                "420774577454,14-01-2020 11:10:00,14-01-2020 10:15:00";  // Chybné časové rozmezí

        BigDecimal actual = calculator.calculate(inputCSV);
        BigDecimal expected = new BigDecimal("5.50");
        assertEquals(expected, actual);



    }

}