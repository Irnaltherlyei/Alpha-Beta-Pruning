import java.util.ArrayList;
import java.util.List;

import rahmen.OthelloArena;
public class Othello
{

    private static String protokolldatei="C:/tmp/wettkampfSS22.log";

    public static void main(String[] args)
    {
        /* Es muss ein OthelloArena-Objekt erzeugt
         * werden. Bei der Erzeugung werden die am Wettkampf
         * teilnehmenden Spieler in Form einer Liste von
         * Othello-Spielern uebergeben. Durch die Erzeugung des
         * OthelloArena-Objekts wird der Wettkampf gestartet.
         */

        //Spielerliste aufbauen
        List<String> spieler =
                new ArrayList<String>();
        //Die Spieler
        spieler.add("spieler.Referenzspieler:1"); //Referenzspieler in Standardeinstellung
        //spieler.add("spieler.paul.Spieler:4");
        spieler.add("spieler.adrian.Spieler:6");

        new OthelloArena(150,  //Gesamtbedenkzeit in Sekunden
                spieler,                //Spielerliste
                protokolldatei,         //Dateiname f�r Ergebnisausgaben
                true					  //debug
        );
    }
}