// -*- coding: utf-8 -*-

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SeptNains {
    static private SimpleDateFormat sdf = new SimpleDateFormat("hh'h 'mm'mn 'ss','SSS's'");



    public static void main(String[] args) throws InterruptedException {

        Date début = new Date(System.currentTimeMillis());
        System.out.println("[" + sdf.format(début) + "] Début du programme.");
        
        final BlancheNeige bn = new BlancheNeige();
        final int nbNains = 7;
        final String noms [] = {"Simplet", "Dormeur",  "Atchoum", "Joyeux", "Grincheux",
                                "Prof", "Timide"};
        final Nain nain [] = new Nain [nbNains];
        for(int i = 0; i < nbNains; i++) nain[i] = new Nain(noms[i],bn);
        for(int i = 0; i < nbNains; i++) nain[i].start();
        Thread.sleep(5000);
        for(int i = 0; i < nbNains; i++) nain[i].interrupt();

        for(int i = 0; i < nbNains; i++) nain[i].join();

        System.out.println("Tous les nains ont terminé.");

    }
}    

class BlancheNeige {
    ArrayList<String> waitList = new ArrayList<>();

    private volatile boolean libre = true;        // Initialement, Blanche-Neige est libre.
    public synchronized void requérir () {
        System.out.println("\t" + Thread.currentThread().getName()
                           + " veut la ressource.");
        waitList.add(Thread.currentThread().getName());
    }

    public synchronized void accéder () throws InterruptedException {
        while ( ! libre || !(waitList.get(0).equals(Thread.currentThread().getName())) )
            wait();   // Le nain s'endort sur l'objet bn
        libre = false;
        waitList.remove(Thread.currentThread().getName());
        System.out.println("\t" + Thread.currentThread().getName()
                           + " accède à la ressource.");
    }

    public synchronized void relâcher () {
        System.out.println("\t" + Thread.currentThread().getName()
                           + " relâche la ressource.");
        libre = true;
        notifyAll();

    }
}

class Nain extends Thread {
    private BlancheNeige bn;

    static private SimpleDateFormat sdf = new SimpleDateFormat("hh'h 'mm'mn 'ss','SSS's'");

    public Nain(String nom, BlancheNeige bn) {
        this.setName(nom);
        this.bn = bn;
    }
    public void run() {
        while(true) {
            bn.requérir();
            try {
                bn.accéder();
            } catch (InterruptedException e) {
                break;
            }
            System.out.println("[" +sdf.format(System.currentTimeMillis())+"] " +getName() + " a un accès (exclusif) à Blanche-Neige.");
            long time =0;
            try {
                time = System.currentTimeMillis();
                sleep(2000);
            } catch (InterruptedException e) {
                try {
                    sleep(2000-(System.currentTimeMillis()-time));
                } catch (InterruptedException ex) {
                }
                break;
            }
            System.out.println("[" +sdf.format(System.currentTimeMillis())+"] " +getName() + " s'apprête à quitter Blanche-Neige.");
            bn.relâcher();
        }
        System.out.println( "[" +sdf.format(System.currentTimeMillis())+"] " +getName() + " a terminé!");
    }	
}

/*
$ make
$ java SeptNains
[09h 34mn 01,834s] Début du programme.
	Simplet veut la ressource.
	Simplet accède à la ressource.
	Timide veut la ressource.
	Prof veut la ressource.
	Grincheux veut la ressource.
	Joyeux veut la ressource.
	Atchoum veut la ressource.
	Dormeur veut la ressource.
Simplet a un accès (exclusif) à Blanche-Neige.
Simplet s'apprête à quitter à Blanche-Neige.
	Simplet relâche la ressource.
	Simplet veut la ressource.
	Simplet accède à la ressource.
Simplet a un accès (exclusif) à Blanche-Neige.
	Timide accède à la ressource.
Timide a un accès (exclusif) à Blanche-Neige.
	Dormeur accède à la ressource.
Dormeur a un accès (exclusif) à Blanche-Neige.
	Atchoum accède à la ressource.
Atchoum a un accès (exclusif) à Blanche-Neige.
	Joyeux accède à la ressource.
Joyeux a un accès (exclusif) à Blanche-Neige.
	Grincheux accède à la ressource.
Grincheux a un accès (exclusif) à Blanche-Neige.
	Prof accède à la ressource.
Prof a un accès (exclusif) à Blanche-Neige.
^C
*/
