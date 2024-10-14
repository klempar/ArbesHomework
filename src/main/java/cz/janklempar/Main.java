package cz.janklempar;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException, ParseException {

        ArbesTelephoneBillCalculator calculator = new ArbesTelephoneBillCalculator();

        // Pro vstup z konzolové aplikace beru jako standard její hlavní vstupní metodu.
        // Pro potřeby spouštění jsem zvolil první parametr, který musí být ve formátu csv, jinak
        // jsem přistupoval k úkolu, jako implementaci .
        if (args.length == 0) {
            System.out.println("Doplňte vstupní parametr ve vstupním csv formátu.");
            return;
        }
        final BigDecimal cenaHovoru = calculator.calculate(args[0]);

        System.out.println(cenaHovoru);

    }
}