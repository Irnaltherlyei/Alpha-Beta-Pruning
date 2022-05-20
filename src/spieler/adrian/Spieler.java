package spieler.adrian;

import spieler.Farbe;
import spieler.OthelloSpieler;
import spieler.Zug;
import spieler.ZugException;

public class Spieler implements OthelloSpieler{
    @Override
    public Zug berechneZug(Zug zug, long l, long l1) throws ZugException {
        return null;
    }

    @Override
    public void neuesSpiel(Farbe farbe, int i) {

    }

    @Override
    public String meinName() {
        return "adrian";
    }

    private boolean isMoveValid(int x, int y){
        return true;
    }

    private Object getBestMove(int depth){
        return null;
    }

    private Object getPossibleMoves(){
        return null;
    }

    private int getPointsMove(int x, int y){
        return 0;
    }

    private boolean move(int x, int y){
        return true;
    }
}
