package com.example.elipse.ims;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

/**
 * Created by hsaidi on 08/03/2015.
 */
public class Node {

    private Point position;
    private int type;
    private String id;
    private String text;
    private int size;
    private Point[] anchor_pos;

    public Node(Point position, int type, String id, String text, int size) {
        this.position = position;
        this.type = type;
        this.id = id;
        this.text = text;
        this.size = size;
        anchor_pos = new Point[4];
        Update_anchor();


    }

    public Point[] getAnchor_pos() {
        return anchor_pos;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
        Update_anchor();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
        Update_anchor();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        Update_anchor();
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
        Update_anchor();
    }

    public void Update_anchor() {

        int x = getPosition().x;
        int y = getPosition().y;
        int sizew = getSize()/2;
        int sizeh = PrincipalView.HEIGHT/2;
        int rayon = 4;

        if ((getType()==PrincipalView.BINDING)||(getType()==PrincipalView.PHOSPHO))
            sizeh = sizew;


        switch (getType()) {
            case PrincipalView.PROTEIN :
            case PrincipalView.MOLECULE :
            case PrincipalView.CONCEPT :
                anchor_pos[0] = new Point (x - sizew, y);
                anchor_pos[2] = new Point (x + sizew, y);
                anchor_pos[1] = new Point (x, y- sizeh);
                anchor_pos[3] = new Point (x, y+ sizeh);
                break;
            case PrincipalView.PHOSPHO :
            case PrincipalView.DEGRAD :
            case PrincipalView.BINDING :
                anchor_pos[0] = new Point (x - sizew - rayon, y);
                anchor_pos[2] = new Point (x + sizew + rayon, y);
                anchor_pos[1] = new Point (x, y- sizeh - rayon);
                anchor_pos[3] = new Point (x, y+ sizeh + rayon);
                break;

    }
    }
}
