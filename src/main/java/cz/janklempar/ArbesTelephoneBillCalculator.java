package cz.janklempar;

import com.phonecompany.billing.TelephoneBillCalculator;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ArbesTelephoneBillCalculator implements TelephoneBillCalculator {

    private static final String DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);

    private static double vypocetCeny(Date startTime, long durationInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startTime);

        // Nastavíme časy 8:00 a 16:00 pro porovnání
        Calendar startOfDay = (Calendar) cal.clone();
        startOfDay.set(Calendar.HOUR_OF_DAY, 8);
        startOfDay.set(Calendar.MINUTE, 0);
        startOfDay.set(Calendar.SECOND, 0);

        Calendar endOfDay = (Calendar) cal.clone();
        endOfDay.set(Calendar.HOUR_OF_DAY, 16);
        endOfDay.set(Calendar.MINUTE, 0);
        endOfDay.set(Calendar.SECOND, 0);

        double cena = 0.0;

        for (long i = 0; i < durationInMinutes; i++) {
            cal.add(Calendar.MINUTE, 1);  // Přidáme každou další započatou minutu

            double sazba;
            if (cal.compareTo(startOfDay) >= 0 && cal.compareTo(endOfDay) < 0) {
                sazba = 1.0; // V intervalu <8:00:00,16:00:00)
            } else {
                // Mimo tento interval
                sazba = 0.5;
            }
            if (i >= 5) {
                // Pokud hovor trvá déle jak 5 minut, další minuty jsou účtovány sazbou 0,2 Kč
                // pro optimalizaci lze zde použít break a vypočítat zbytek násobením zbytku intervalu zmenšenou sazbou.
                sazba = 0.2;
            }

            cena += sazba;
        }

        return cena;
    }

    @Override
    public BigDecimal calculate(String vstupHovory) {


        // Mapy pro sledovani poctu hovoru a celkove ceny volani jednotlivych hovoru
        Map<String, Integer> pocetVolani = new HashMap<>();
        Map<String, Double> cenyVolani = new HashMap<>();

        // Celková cena všech hovorů
        double celkovaCena = 0.0;


        // Seznam všech zpracovaných časových intervalů (začátek a konec hovoru)
        List<CallInterval> zpracovaneIntervaly = new ArrayList<>();


        String line;
        try (BufferedReader br = new BufferedReader(new StringReader(vstupHovory))) {

            // Čteme vstupní data řádek po řádku
            while ((line = br.readLine()) != null) {

                // Rozdělujeme řádek pomocí čárky
                String[] data = line.split(",");

                if (data.length != 3) {
                    System.out.println("CHYBA VSTUPU - Chybný formát vstupních dat (počet parametrů pro call): " + line + "\n");
                    continue;
                }

                // Získáváme telefonní číslo a údaje o jednotlivých callech.
                String phoneNumber = data[0];
                String startTimeStr = data[1];
                String endTimeStr = data[2];

                // U validací jsem se rozhodl při chybném vstupu pouze nezapočítat řádek csv

                // Validace telefonního čísla (jen číslice)
                if (!isValidPhoneNumber(phoneNumber)) {
                    System.out.println("\"CHYBA VSTUPU - Chybné telefonní číslo: " + phoneNumber + "\n");
                    continue;
                }

                // Validace časových údajů a jejich formátu
                Date startTime;
                Date endTime;
                try {
                    startTime = dateFormat.parse(startTimeStr);
                    endTime = dateFormat.parse(endTimeStr);
                } catch (ParseException e) {
                    System.out.println("\"CHYBA VSTUPU - Chybný formát data a času: " + startTimeStr + " nebo " + endTimeStr  + "\n");
                    continue;
                }

                // Validace, že začátek hovoru je před koncem
                if (!startTime.before(endTime)) {
                    System.out.println("CHYBA VSTUPU - Začátek hovoru musí být před koncem: " + startTimeStr + " - " + endTimeStr  + "\n");
                    continue;
                }

                // Validace překrývání časových intervalů
                CallInterval novyInterval = new CallInterval(startTime, endTime);
                if (isOverlapping(novyInterval, zpracovaneIntervaly)) {
                    System.out.println("Časový interval hovoru se překrývá s jiným hovorem: " + startTimeStr + " - " + endTimeStr);
                    continue;
                }

                // Vypočítáme délku hovoru v milisekundách
                long durationInMilliseconds = endTime.getTime() - startTime.getTime();
                long durationInMinutes = (durationInMilliseconds / 1000) / 60;

                // Přidáme jednu započatou minutu navíc, pokud nejsme na hraně přesné minuty
                if (durationInMilliseconds % (60 * 1000) != 0) {
                    durationInMinutes += 1;
                }

                // Výpočet ceny hovoru
                double cena = vypocetCeny(startTime, durationInMinutes);

                // Zvýšíme celkovou cenu o cenu aktuálního hovoru
                celkovaCena += cena;

                // Zaznamenáme počet volání a celkovou cenu pro každé číslo
                pocetVolani.put(phoneNumber, pocetVolani.getOrDefault(phoneNumber, 0) + 1);
                cenyVolani.put(phoneNumber, cenyVolani.getOrDefault(phoneNumber, 0.0) + cena);

                // Výstup pro jednotlivé hovory (pro debugování)
                System.out.println("Telefonní číslo: " + phoneNumber);
                System.out.println("Délka hovoru: " + durationInMinutes + " minut");
                System.out.println("Cena hovoru: " + cena + " Kč");
                System.out.println();
            }

            // Najdeme nejčastěji volané číslo
            String nejcastejsiCislo = null;
            int maxPocetVolani = 0;
            double maxCena = 0.0;

            for (Map.Entry<String, Integer> entry : pocetVolani.entrySet()) {
                String cislo = entry.getKey();
                int pocet = entry.getValue();
                double cena = cenyVolani.get(cislo);

                if (pocet > maxPocetVolani || (pocet == maxPocetVolani && cena > maxCena)) {
                    nejcastejsiCislo = cislo;
                    maxPocetVolani = pocet;
                    maxCena = cena;
                }
            }

            // Odečteme cenu hovorů na nejčastěji volané číslo od celkové ceny
            if (nejcastejsiCislo != null) {
                System.out.println("Nejčastěji volané číslo: " + nejcastejsiCislo);
                System.out.println("Toto číslo bylo voláno " + maxPocetVolani + "x s celkovou cenou: " + maxCena + " Kč");
                celkovaCena -= cenyVolani.get(nejcastejsiCislo);
            }

            // Výstup celkové ceny
            System.out.println("Celková cena všech hovorů (bez nejčastěji volaného čísla): " + celkovaCena + " Kč");

            // ošetříme převod z double na big decimal a zaokrouhlíme u problémových hodnot vzhůru

            BigDecimal celkovaCenaD = new BigDecimal(Double.toString(celkovaCena));
            BigDecimal zaokrouhlenaCelkovaCena = celkovaCenaD.setScale(2, RoundingMode.HALF_UP);

            return zaokrouhlenaCelkovaCena;



        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\d+"); // Zajišťuje, že řetězec obsahuje jen číslice
    }

    // Kontrola, zda nový časový interval nepřekrývá již existující intervaly
    // Nebylo v zadání, ale dle logiky callů mi přišlo v pořádku validaci rozšířit.
    // Nelze aby probíhaly hovory na stejné číslo.
    private static boolean isOverlapping(CallInterval novyInterval, List<CallInterval> zpracovaneIntervaly) {
        for (CallInterval interval : zpracovaneIntervaly) {
            if (novyInterval.overlapsWith(interval)) {
                return true;
            }
        }
        return false;
    }

}
