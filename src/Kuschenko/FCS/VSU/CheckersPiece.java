package Kuschenko.FCS.VSU;

import javax.swing.*;
import java.awt.*;

public class CheckersPiece extends JButton {
    private int color; // 0 - пусто, 1 - first, 2 - second
    private boolean isKing;

    public CheckersPiece(int color) {
        this.color = color;
        this.isKing = false;
        setPreferredSize(new Dimension(80, 80));
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isKing() {
        return isKing;
    }

    public void makeKing(boolean isKing) {
        this.isKing = isKing;
    }
}

