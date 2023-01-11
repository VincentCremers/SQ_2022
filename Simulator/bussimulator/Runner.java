package bussimulator;

import java.lang.reflect.Array;
import java.util.*;

import tijdtools.TijdFuncties;

public class Runner implements Runnable {

    private static HashMap<Integer, ArrayList<Bus>> busStart = new HashMap<Integer, ArrayList<Bus>>();
    private static ArrayList<Bus> actieveBussen = new ArrayList<Bus>();
    private static int interval = 1000;
    private static int syncInterval = 5;

    private static void addBus(int starttijd, Bus bus) {
        ArrayList<Bus> bussen = new ArrayList<Bus>();
        if (busStart.containsKey(starttijd)) {
            bussen = busStart.get(starttijd);
        }
        bussen.add(bus);
        busStart.put(starttijd, bussen);
        bus.setbusID(starttijd);
    }

    private static int startBussen(int tijd) {
        for (Bus bus : busStart.get(tijd)) {
            actieveBussen.add(bus);
        }
        busStart.remove(tijd);
        return (!busStart.isEmpty()) ? Collections.min(busStart.keySet()) : -1;
    }

    public static void moveBussen(int nu) {
        Iterator<Bus> itr = actieveBussen.iterator();
        while (itr.hasNext()) {
            Bus bus = itr.next();
            boolean eindpuntBereikt = bus.move();
            if (eindpuntBereikt) {
                bus.sendLastETA(nu);
                itr.remove();
            }
        }
    }

    public static void sendETAs(int nu) {
        Iterator<Bus> itr = actieveBussen.iterator();
        while (itr.hasNext()) {
            Bus bus = itr.next();
            bus.sendETAs(nu);
        }
    }

    public static int initBussen() {
        ArrayList<Bus> bussen = new ArrayList<>();
        ArrayList<Integer> startTijden = new ArrayList<>(Arrays.asList(3, 5, 4, 6, 3, 5, 4, 6, 12, 10, 3, 5, 14, 16, 13, 3, 5, 4, 6, 3, 5, 4, 6, 12, 10, 3, 5, 14, 16, 13));

        for (int i = 0; i < 2; i++) {
            int richting = i == 0 ? 1 : -1;

            bussen.add(new Bus(Lijnen.LIJN1, Bedrijven.ARRIVA, richting));
            bussen.add(new Bus(Lijnen.LIJN2, Bedrijven.ARRIVA, richting));
            bussen.add(new Bus(Lijnen.LIJN3, Bedrijven.ARRIVA, richting));
            bussen.add(new Bus(Lijnen.LIJN4, Bedrijven.ARRIVA, richting));
            bussen.add(new Bus(Lijnen.LIJN5, Bedrijven.FLIXBUS, richting));
            bussen.add(new Bus(Lijnen.LIJN6, Bedrijven.QBUZZ, richting));
            bussen.add(new Bus(Lijnen.LIJN7, Bedrijven.QBUZZ, richting));
            bussen.add(new Bus(Lijnen.LIJN1, Bedrijven.ARRIVA, richting));
            bussen.add(new Bus(Lijnen.LIJN4, Bedrijven.ARRIVA, richting));
            bussen.add(new Bus(Lijnen.LIJN5, Bedrijven.FLIXBUS, richting));
            bussen.add(new Bus(Lijnen.LIJN8, Bedrijven.QBUZZ, richting));
            bussen.add(new Bus(Lijnen.LIJN8, Bedrijven.QBUZZ, richting));
            bussen.add(new Bus(Lijnen.LIJN3, Bedrijven.ARRIVA, richting));
            bussen.add(new Bus(Lijnen.LIJN4, Bedrijven.ARRIVA, richting));
            bussen.add(new Bus(Lijnen.LIJN5, Bedrijven.FLIXBUS, richting));
        }

        for (int i = 0; i < bussen.size(); i++) {
            addBus(startTijden.get(i), bussen.get(i));
        }

        return Collections.min(busStart.keySet());
    }

    @Override
    public void run() {
        int tijd = 0;
        int counter = 0;
        TijdFuncties tijdFuncties = new TijdFuncties();
        tijdFuncties.initSimulatorTijden(interval, syncInterval);
        int volgende = initBussen();
        while ((volgende >= 0) || !actieveBussen.isEmpty()) {
            counter = tijdFuncties.getCounter();
            tijd = tijdFuncties.getTijdCounter();
            System.out.println("De tijd is:" + tijdFuncties.getSimulatorWeergaveTijd());
            volgende = (counter == volgende) ? startBussen(counter) : volgende;
            moveBussen(tijd);
            sendETAs(tijd);
            try {
                tijdFuncties.simulatorStep();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
