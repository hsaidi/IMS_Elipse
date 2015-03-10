package com.example.elipse.ims;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hsaidi on 04/03/2015.
 */

public class PrincipalView extends View {

    // Nodes
    public static final int PROTEIN = 1;
    public static final int MOLECULE = 2;
    public static final int PHOSPHO = 3;
    public static final int DEGRAD = 4;
    public static final int CONCEPT = 5;
    public static final int BINDING = 6;

    // Connexions
    public static final int CDEGRADATION = 1;
    public static final int CACTIVATION = 2;
    public static final int CBINDING = 3;
    public static final int INHIBITION = 4;
    public static final int PHOSPHORISATION = 5;

    public static final int HEIGHT = 40;
    public static final int TOLSELECTION = 10;

    // defines paint and canvas
    private Paint PaintView;

    private List<Connexion> connexionList;
    private List<Node> nodeList;
    private int selectedid = -1;
    private int lastselectedid = -1;
    private int selectedid_anchor = 0;
    private int lastselectedid_anchor = 0;

    private int id = 0;
    // Get the screen's density scale
    final float scale = getResources().getDisplayMetrics().density;

    final MainActivity context;
    PopupWindow NodesMenu;
    PopupWindow ConnexionsMenu;
    // For the touches events

    private float _Touch_X;
    private float _Touch_Y;
    private final float SCROLL_THRESHOLD = 10;
    private boolean _isClicked;

    public PrincipalView(Context _context, AttributeSet attrs) {
        super(_context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setupPaint();

        nodeList = new ArrayList<Node>();
        connexionList = new ArrayList<Connexion>();

        this.context = (MainActivity) _context;
        NodesMenu = null;
    }


    @Override
    protected void onDraw(Canvas canvas) {

        int element = 0;

        // Drawing Connexions
        element = 0;
        for (Connexion p : connexionList) {

            switch (p.getType()) {
                case CDEGRADATION : drawCDegradation(canvas, p);break;
                case CACTIVATION : drawCActivation(canvas, p); break;
                case CBINDING : drawCBinding(canvas, p); break;
                case INHIBITION : drawInhibition(canvas, p); break;
                case PHOSPHORISATION : drawPhosphorisation(canvas, p); break;
                default:  break;

            }

            element++;
        }

        // Drawing Nodes

        element = 0;
        for (Node p : nodeList) {

            switch (p.getType()) {
                case PROTEIN : drawProtein(canvas, p.getPosition().x, p.getPosition().y,element); break;
                case MOLECULE : drawMolecule(canvas, p.getPosition().x, p.getPosition().y, element); break;
                case PHOSPHO : drawPhospho(canvas, p.getPosition().x, p.getPosition().y, element); break;
                case DEGRAD : drawDegrad(canvas, p.getPosition().x, p.getPosition().y, element); break;
                case CONCEPT : drawConcept(canvas, p.getPosition().x, p.getPosition().y, element); break;
                case BINDING : drawBinding(canvas, p.getPosition().x, p.getPosition().y, element); break;
                default:  break;

            }
            element++;
        }

        SetSelected(canvas);

    }

    // Setup paint with color and stroke styles
    private void setupPaint() {
        PaintView = new Paint();
        PaintView.setColor(Color.BLACK);
        PaintView.setAntiAlias(true);
        PaintView.setStrokeWidth(5);
        PaintView.setStyle(Paint.Style.FILL);// Avec remplissage
        PaintView.setStrokeJoin(Paint.Join.ROUND);
        PaintView.setStrokeCap(Paint.Cap.ROUND);
    }


    // Touches on the screen
    @Override
    public boolean onTouchEvent(MotionEvent event) {


        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                _Touch_X = event.getX();
                _Touch_Y = event.getY();
                lastselectedid = selectedid;
                lastselectedid_anchor = selectedid_anchor;
                CheckSelected(Math.round(_Touch_X), Math.round(_Touch_Y));
                _isClicked = true;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (_isClicked) {
                    if(lastselectedid==-1) // Pour les connexion, si pas d'elements selectionnés
                        {if(selectedid==-1)
                            ShowNodesMenu(new Point(Math.round(_Touch_X), Math.round(_Touch_Y)));
                        else
                            postInvalidate();}
                    else
                    { if(selectedid!=-1)
                        {   ShowConnexionsMenu(new Point(Math.round(_Touch_X), Math.round(_Touch_Y)), lastselectedid, selectedid,lastselectedid_anchor,selectedid_anchor);
                            selectedid=-1;
                            lastselectedid=-1;
                            postInvalidate();
                        }
                      else
                            postInvalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (_isClicked && Math.abs(_Touch_X - event.getX()) > SCROLL_THRESHOLD || Math.abs(_Touch_Y - event.getY()) > SCROLL_THRESHOLD) {
                    Log.i("Touch : ", "Draging x = "+event.getX()+" y = "+event.getY());
                    if(selectedid!=-1)
                    {   nodeList.get(selectedid).setPosition( new Point(Math.round(event.getX()), Math.round(event.getY())));
                        postInvalidate();
                    }
                    _isClicked = false;
                }
                break;
            default:
                //BindingMenu.dismiss();
                break;
        }

        return true;
    }



    protected boolean CheckSelected(int x, int y)
    {
        int element = 0;
        for (Node p : nodeList) {

            if ((x<p.getPosition().x+ nodeList.get(element).getSize())&&(x>p.getPosition().x- nodeList.get(element).getSize()))
                if ((y<p.getPosition().y+ HEIGHT + TOLSELECTION)&&(y>p.getPosition().y- HEIGHT + TOLSELECTION))
                    {selectedid = element;

                     // Selecting the anchor:
                     double temp = distance(x,y,nodeList.get(selectedid).getAnchor_pos()[0]);
                     selectedid_anchor = 0;
                     double distance = temp ;
                     Log.i("Anchor","Selected ID : "+selectedid+" Selected Anchor : 0 Distance : "+distance);


                        for(int i=1;i<4;i++)
                        {
                            temp = distance(x,y,nodeList.get(selectedid).getAnchor_pos()[i]);
                            if(temp<distance)
                            {
                                distance = temp;
                                selectedid_anchor = i;
                            }
                            Log.i("Anchor","Selected ID : "+selectedid+" Selected Anchor : "+i+" Distance : "+temp);
                        }
                        Log.i("Anchor","Choosen Selected ID : "+selectedid+" Selected Anchor : "+selectedid_anchor+" Distance : "+distance);



                     return true;}

            element++;
        }
        selectedid = -1;
        return false;
    }

    private double distance(int x1, int y1, Point p)
    {
      return Math.sqrt(Math.pow(p.x-x1,2)+Math.pow(p.y-y1,2));
    }


    protected void SetSelected(Canvas canvas)
    {
        // Setting the selected item
        if(selectedid!=-1)
        {
            int rayon = 4;

            PaintView.setColor(Color.GREEN);
            PaintView.setStrokeWidth(2);
            PaintView.setStyle(Paint.Style.FILL);

            for(int i=0;i<4;i++)
                canvas.drawCircle(nodeList.get(selectedid).getAnchor_pos()[i].x, nodeList.get(selectedid).getAnchor_pos()[i].y, rayon, PaintView);

        }
    }

    protected void drawProtein(Canvas canvas, int x, int y,int element)
    {

        int textsize = 20 ;
        int width = (int) (nodeList.get(element).getText().length()*textsize/scale);
        int height = HEIGHT;

        int indx = width/2;
        int indy = height / 2;

        PaintView.setColor(Color.BLACK);
        PaintView.setStrokeWidth(0);
        PaintView.setStyle(Paint.Style.STROKE);
        Rect R = new Rect();
        R.set(x-indx,y-indy,x+indx,y+indy);
        //canvas.drawRect(R,PaintView);

        // Drawing the text now
        PaintView.setStyle(Paint.Style.FILL);
        PaintView.setTextSize(20);
        canvas.drawText(nodeList.get(element).getText(), x-indx+12, y+7, PaintView);

        nodeList.get(element).setSize(width);
    }

    protected void drawMolecule(Canvas canvas, int x, int y,int element)
    {
        PaintView.setColor(Color.BLACK);
        PaintView.setStrokeWidth(2);
        PaintView.setStyle(Paint.Style.STROKE);

        int textsize = 20 ;
        int width = (int) (nodeList.get(element).getText().length()*textsize/scale);
        int height = HEIGHT;

        int indx = width/2;
        int indy = height / 2;

        int roundw = indx - ((int) (width*0.33));
        int roundh = indy - ((int) (height*0.425));

        Path path = new Path();
        path.moveTo(x-indx, y-roundh);
        path.quadTo(x - indx, y - indy, x - roundw, y - indy);
        path.lineTo(x + roundw, y - indy);
        path.quadTo(x+indx, y-indy,x+indx, y-roundh);
        path.lineTo(x+indx, y+roundh);
        path.quadTo(x+indx, y+indy,x+roundw, y+indy);
        path.lineTo(x-roundw, y+indy);
        path.quadTo(x-indx, y+indy,x-indx, y+roundh);
        path.lineTo(x-indx, y-roundh);
        canvas.drawPath(path,PaintView);

        // Drawing the text now
        PaintView.setStyle(Paint.Style.FILL);
        PaintView.setTextSize(textsize);
        canvas.drawText(nodeList.get(element).getText(), x-indx+12, y+7, PaintView);

        nodeList.get(element).setSize(width);
    }

    protected void drawPhospho(Canvas canvas, int x, int y,int element)
    {
        int rayon = 10;
        PaintView.setColor(Color.BLUE);
        PaintView.setStrokeWidth(2);
        PaintView.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x, y,  rayon, PaintView);
        nodeList.get(element).setSize(rayon);
    }

    protected void drawDegrad(Canvas canvas, int x, int y,int element)
    {
        PaintView.setColor(Color.RED);
        PaintView.setStrokeWidth(2);
        PaintView.setStyle(Paint.Style.STROKE);

        int width = 40;
        int height = HEIGHT;

        int indx = width/2;
        int indy = height / 2;

        int roundw = indx - ((int) (width*0.5));
        int roundh = indy - ((int) (height*0.5));

        Path path = new Path();
        path.moveTo(x-indx, y-roundh);
        path.quadTo(x - indx, y - indy, x - roundw, y - indy);
        path.lineTo(x + roundw, y - indy);
        path.quadTo(x+indx, y-indy,x+indx, y-roundh);
        path.lineTo(x+indx, y+roundh);
        path.quadTo(x+indx, y+indy,x+roundw, y+indy);
        path.lineTo(x-roundw, y+indy);
        path.quadTo(x-indx, y+indy,x-indx, y+roundh);
        path.lineTo(x-indx, y-roundh);
        path.moveTo(x+indx, y-indy);
        path.lineTo(x-indx, y+indy);
        canvas.drawPath(path,PaintView);

        nodeList.get(element).setSize(width);
    }

    protected void drawConcept(Canvas canvas, int x, int y,int element)
    {
        int textsize = 20 ;

        int width = (int) (nodeList.get(element).getText().length()*textsize/scale);
        int height = HEIGHT;

        int indx = width/2;
        int indy = height / 2;

        PaintView.setColor(Color.BLACK);
        PaintView.setStrokeWidth(2);
        PaintView.setStyle(Paint.Style.STROKE);
        Rect R = new Rect();
        R.set(x-indx,y-indy,x+indx,y+indy);
        canvas.drawRect(R,PaintView);

        // Drawing the text now
        PaintView.setStyle(Paint.Style.FILL);
        PaintView.setTextSize(20);
        canvas.drawText(nodeList.get(element).getText(), x-indx+12, y+7, PaintView);
        nodeList.get(element).setSize(width);
        }

    protected void drawBinding(Canvas canvas, int x, int y,int element)
    {
        int rayon = 10;
        PaintView.setColor(Color.BLACK);
        PaintView.setStrokeWidth(2);
        PaintView.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x, y, rayon, PaintView);
        nodeList.get(element).setSize(rayon);
    }

    private void drawCDegradation(Canvas canvas, Connexion p) {

        PaintView.setColor(Color.RED);
        PaintView.setStrokeWidth(2);
        PaintView.setStyle(Paint.Style.STROKE);

        int x1 = nodeList.get(p.getIndice_dep()).getAnchor_pos()[p.getAncre_dep()].x;
        int y1 = nodeList.get(p.getIndice_dep()).getAnchor_pos()[p.getAncre_dep()].y;
        int x2 = nodeList.get(p.getIndice_arr()).getAnchor_pos()[p.getAncre_arr()].x;
        int y2 = nodeList.get(p.getIndice_arr()).getAnchor_pos()[p.getAncre_arr()].y;

        canvas.drawPath(getPath(x1,y1,x2,y2,p.getAncre_dep(),p.getAncre_arr()), PaintView);

    }

    private void drawPhosphorisation(Canvas canvas, Connexion p) {

        PaintView.setColor(Color.RED);
        PaintView.setStrokeWidth(2);
        PaintView.setStyle(Paint.Style.STROKE);

        int x1 = nodeList.get(p.getIndice_dep()).getAnchor_pos()[p.getAncre_dep()].x;
        int y1 = nodeList.get(p.getIndice_dep()).getAnchor_pos()[p.getAncre_dep()].y;
        int x2 = nodeList.get(p.getIndice_arr()).getAnchor_pos()[p.getAncre_arr()].x;
        int y2 = nodeList.get(p.getIndice_arr()).getAnchor_pos()[p.getAncre_arr()].y;

        canvas.drawPath(getPath(x1,y1,x2,y2,p.getAncre_dep(),p.getAncre_arr()), PaintView);

    }

    private void drawInhibition(Canvas canvas, Connexion p) {

        PaintView.setColor(Color.RED);
        PaintView.setStrokeWidth(2);
        PaintView.setStyle(Paint.Style.STROKE);

        int x1 = nodeList.get(p.getIndice_dep()).getAnchor_pos()[p.getAncre_dep()].x;
        int y1 = nodeList.get(p.getIndice_dep()).getAnchor_pos()[p.getAncre_dep()].y;
        int x2 = nodeList.get(p.getIndice_arr()).getAnchor_pos()[p.getAncre_arr()].x;
        int y2 = nodeList.get(p.getIndice_arr()).getAnchor_pos()[p.getAncre_arr()].y;

        canvas.drawPath(getPath(x1,y1,x2,y2,p.getAncre_dep(),p.getAncre_arr()), PaintView);

    }

    private void drawCBinding(Canvas canvas, Connexion p) {

        PaintView.setColor(Color.RED);
        PaintView.setStrokeWidth(2);
        PaintView.setStyle(Paint.Style.STROKE);

        int x1 = nodeList.get(p.getIndice_dep()).getAnchor_pos()[p.getAncre_dep()].x;
        int y1 = nodeList.get(p.getIndice_dep()).getAnchor_pos()[p.getAncre_dep()].y;
        int x2 = nodeList.get(p.getIndice_arr()).getAnchor_pos()[p.getAncre_arr()].x;
        int y2 = nodeList.get(p.getIndice_arr()).getAnchor_pos()[p.getAncre_arr()].y;

        canvas.drawPath(getPath(x1,y1,x2,y2,p.getAncre_dep(),p.getAncre_arr()), PaintView);

    }

    private void drawCActivation(Canvas canvas, Connexion p) {

        PaintView.setColor(Color.RED);
        PaintView.setStrokeWidth(2);
        PaintView.setStyle(Paint.Style.STROKE);

        int x1 = nodeList.get(p.getIndice_dep()).getAnchor_pos()[p.getAncre_dep()].x;
        int y1 = nodeList.get(p.getIndice_dep()).getAnchor_pos()[p.getAncre_dep()].y;
        int x2 = nodeList.get(p.getIndice_arr()).getAnchor_pos()[p.getAncre_arr()].x;
        int y2 = nodeList.get(p.getIndice_arr()).getAnchor_pos()[p.getAncre_arr()].y;

        canvas.drawPath(getPath(x1,y1,x2,y2,p.getAncre_dep(),p.getAncre_arr()), PaintView);

    }

    protected Path getPath(int x1,int y1,int x2,int y2,int d,int a)
    {
        Path path = new Path();
        int marge = 10;

        switch (d) {
            case 1:
                switch (a) {
                    case 1:
                        if(x1<x2)
                            if(y1<y2)
                                {path.moveTo(x1 , y1);
                                 path.lineTo(x1 , y1-marge);
                                 path.lineTo(x2 , y1-marge);
                                 path.lineTo(x2 , y2);}
                                else
                                {
                                path.moveTo(x1 , y1);
                                path.lineTo(x1 , y2-marge);
                                path.lineTo(x2 , y2-marge);
                                path.lineTo(x2 , y2);
                                }
                        else
                            if(y1<y2)
                                {path.moveTo(x1 , y1);
                                 path.lineTo(x1 , y1-marge);
                                 path.lineTo(x2 , y1-marge);
                                 path.lineTo(x2 , y2);}
                                else
                                {path.moveTo(x1 , y1);
                                 path.lineTo(x1 , y2-marge);
                                 path.lineTo(x2 , y2-marge);
                                 path.lineTo(x2 , y2);}

                        break;
                    case 2:
                        if(x1<x2)
                            if(y1<y2)
                            {path.moveTo(x1 , y1);
                                path.lineTo(x1 , y1-marge);
                                path.lineTo(x2+marge , y1-marge);
                                path.lineTo(x2+marge , y2);
                                path.lineTo(x2 , y2);}
                            else
                            {path.moveTo(x1 , y1);
                                path.lineTo(x1 , y1-marge);
                                path.lineTo(x2+marge , y1-marge);
                                path.lineTo(x2+marge , y2);
                                path.lineTo(x2 , y2);}
                        else
                        if(y1<y2)
                        {   path.moveTo(x1 , y1);
                            path.lineTo(x1 , y1-marge);
                            path.lineTo(x2+marge , y1-marge);
                            path.lineTo(x2+marge , y2);
                            path.lineTo(x2 , y2);}
                        else
                        {   path.moveTo(x1 , y1);
                            path.lineTo(x1 , y2);
                            path.lineTo(x2 , y2);
                        }

                        break;
                    case 3:
                        if(x1<x2)
                            if(y1<y2)
                            {path.moveTo(x1 , y1);
                                path.lineTo(x1 , y1-marge);
                                path.lineTo(x1+(x2-x1)/2, y1-marge);
                                path.lineTo(x1+(x2-x1)/2, y2+marge);
                                path.lineTo(x2, y2+marge);
                                path.lineTo(x2 , y2);}
                            else
                            {
                                path.moveTo(x1 , y1);
                                path.lineTo(x1 , y1-marge);
                                path.lineTo(x2 , y1-marge);
                                path.lineTo(x2 , y2);
                            }
                        else
                    if(y1<y2)
                    {path.moveTo(x1 , y1);
                        path.lineTo(x1 , y1-marge);
                        path.lineTo(x1+(x2-x1)/2, y1-marge);
                        path.lineTo(x1+(x2-x1)/2, y2+marge);
                        path.lineTo(x2, y2+marge);
                        path.lineTo(x2 , y2);}
                    else
                    {path.moveTo(x1 , y1);
                        path.lineTo(x1 , y1-marge);
                        path.lineTo(x2 , y1-marge);
                        path.lineTo(x2 , y2);}

                    break;
                    case 0:
                        if(x1<x2)
                            if(y1<y2)
                            {path.moveTo(x1 , y1);
                                path.lineTo(x1 , y1-marge);
                                path.lineTo(x2-marge , y1-marge);
                                path.lineTo(x2-marge , y2);
                                path.lineTo(x2 , y2);}
                            else
                            {   path.moveTo(x1 , y1);
                                path.lineTo(x1 , y2);
                                path.lineTo(x2 , y2);}
                        else
                        if(y1<y2)
                        {path.moveTo(x1 , y1);
                            path.lineTo(x1 , y1-marge);
                            path.lineTo(x2-marge , y1-marge);
                            path.lineTo(x2-marge , y2);
                            path.lineTo(x2 , y2);}
                        else
                        {path.moveTo(x1 , y1);
                            path.lineTo(x1 , y1-marge);
                            path.lineTo(x2-marge , y1-marge);
                            path.lineTo(x2-marge , y2);
                            path.lineTo(x2 , y2);}

                        break;
                    default:
                        break;
                }
                break;
            case 2:
                switch (a) {
                    case 1:
                        int tempx = x1;
                        int tempy = y1;
                        x1 = x2;
                        y1 = y2;
                        x2 = tempx;
                        y2 = tempy;
                        if(x1<x2)
                            if(y1<y2)
                            {path.moveTo(x1 , y1);
                                path.lineTo(x1 , y1-marge);
                                path.lineTo(x2+marge , y1-marge);
                                path.lineTo(x2+marge , y2);
                                path.lineTo(x2 , y2);}
                            else
                            {path.moveTo(x1 , y1);
                                path.lineTo(x1 , y1-marge);
                                path.lineTo(x2+marge , y1-marge);
                                path.lineTo(x2+marge , y2);
                                path.lineTo(x2 , y2);}
                        else
                        if(y1<y2)
                        {   path.moveTo(x1 , y1);
                            path.lineTo(x1 , y1-marge);
                            path.lineTo(x2+marge , y1-marge);
                            path.lineTo(x2+marge , y2);
                            path.lineTo(x2 , y2);}
                        else
                        {   path.moveTo(x1 , y1);
                            path.lineTo(x1 , y2);
                            path.lineTo(x2 , y2);
                        }

                    break;
                    case 3:
                        break;
                    case 2:
                        break;
                    case 0:
                        if(x1<x2)
                            if(y1<y2)
                            {   path.moveTo(x1 , y1);
                                path.lineTo(x1+marge , y1);
                                path.lineTo(x1+marge , y2);
                                path.lineTo(x2 , y2);}
                            else
                            {   path.moveTo(x1 , y1);
                                path.lineTo(x1+marge , y1);
                                path.lineTo(x1+marge , y2);
                                path.lineTo(x2 , y2);}
                        else
                        if(y1<y2)
                        {path.moveTo(x1 , y1);
                            path.lineTo(x1+marge , y1);
                            path.lineTo(x1+marge , y1+(y2-y1)/2);
                            path.lineTo(x2-marge , y1+(y2-y1)/2);
                            path.lineTo(x2-marge , y2);
                            path.lineTo(x2 , y2);}
                        else
                        {path.moveTo(x1 , y1);
                            path.lineTo(x1+marge , y1);
                            path.lineTo(x1+marge , y1+(y2-y1)/2);
                            path.lineTo(x2-marge , y1+(y2-y1)/2);
                            path.lineTo(x2-marge , y2);
                            path.lineTo(x2 , y2);}

                        break;
                    default:
                        break;
                }
                break;
            case 3:
                switch (a) {
                    case 1:
                        int tempx = x1;
                        int tempy = y1;
                        x1 = x2;
                        y1 = y2;
                        x2 = tempx;
                        y2 = tempy;
                        if(x1<x2)
                            if(y1<y2)
                            {path.moveTo(x1 , y1);
                                path.lineTo(x1 , y1-marge);
                                path.lineTo(x1+(x2-x1)/2, y1-marge);
                                path.lineTo(x1+(x2-x1)/2, y2+marge);
                                path.lineTo(x2, y2+marge);
                                path.lineTo(x2 , y2);}
                            else
                            {
                                path.moveTo(x1 , y1);
                                path.lineTo(x1 , y1-marge);
                                path.lineTo(x2 , y1-marge);
                                path.lineTo(x2 , y2);
                            }
                        else
                        if(y1<y2)
                        {path.moveTo(x1 , y1);
                            path.lineTo(x1 , y1-marge);
                            path.lineTo(x1+(x2-x1)/2, y1-marge);
                            path.lineTo(x1+(x2-x1)/2, y2+marge);
                            path.lineTo(x2, y2+marge);
                            path.lineTo(x2 , y2);}
                        else
                        {path.moveTo(x1 , y1);
                            path.lineTo(x1 , y1-marge);
                            path.lineTo(x2 , y1-marge);
                            path.lineTo(x2 , y2);}

                        break;
                    case 3:
                        break;
                    case 2:
                        break;
                    case 0:
                        if(x1<x2)
                            if(y1<y2)
                            {path.moveTo(x1 , y1);
                                path.lineTo(x1 , y2);
                                path.lineTo(x2 , y2);}
                            else
                            {path.moveTo(x1 , y1);
                                path.lineTo(x1 , y1+marge);
                                path.lineTo(x2-marge , y1+marge);
                                path.lineTo(x2-marge , y2);
                                path.lineTo(x2 , y2);}
                        else
                        if(y1<y2)
                        {   path.moveTo(x1 , y1);
                            path.lineTo(x1 , y1+marge);
                            path.lineTo(x2-marge , y1+marge);
                            path.lineTo(x2-marge , y2);
                            path.lineTo(x2 , y2);}
                        else
                        {   path.moveTo(x1 , y1);
                            path.lineTo(x1 , y1+marge);
                            path.lineTo(x2-marge , y1+marge);
                            path.lineTo(x2-marge , y2);
                            path.lineTo(x2 , y2);}

                        break;
                    default:
                        break;
                }
                break;
            case 0:
                switch (a) {
                    case 1:
                        int tempx = x1;
                        int tempy = y1;
                        x1 = x2;
                        y1 = y2;
                        x2 = tempx;
                        y2 = tempy;
                        if(x1<x2)
                            if(y1<y2)
                            {path.moveTo(x1 , y1);
                                path.lineTo(x1 , y1-marge);
                                path.lineTo(x2-marge , y1-marge);
                                path.lineTo(x2-marge , y2);
                                path.lineTo(x2 , y2);}
                            else
                            {   path.moveTo(x1 , y1);
                                path.lineTo(x1 , y2);
                                path.lineTo(x2 , y2);}
                        else
                        if(y1<y2)
                        {path.moveTo(x1 , y1);
                            path.lineTo(x1 , y1-marge);
                            path.lineTo(x2-marge , y1-marge);
                            path.lineTo(x2-marge , y2);
                            path.lineTo(x2 , y2);}
                        else
                        {path.moveTo(x1 , y1);
                            path.lineTo(x1 , y1-marge);
                            path.lineTo(x2-marge , y1-marge);
                            path.lineTo(x2-marge , y2);
                            path.lineTo(x2 , y2);}

                        break;
                    case 3:
                        tempx = x1;
                        tempy = y1;
                        x1 = x2;
                        y1 = y2;
                        x2 = tempx;
                        y2 = tempy;
                        if(x1<x2)
                            if(y1<y2)
                            {path.moveTo(x1 , y1);
                                path.lineTo(x1 , y2);
                                path.lineTo(x2 , y2);}
                            else
                            {path.moveTo(x1 , y1);
                                path.lineTo(x1 , y1+marge);
                                path.lineTo(x2-marge , y1+marge);
                                path.lineTo(x2-marge , y2);
                                path.lineTo(x2 , y2);}
                        else
                        if(y1<y2)
                        {   path.moveTo(x1 , y1);
                            path.lineTo(x1 , y1+marge);
                            path.lineTo(x2-marge , y1+marge);
                            path.lineTo(x2-marge , y2);
                            path.lineTo(x2 , y2);}
                        else
                        {   path.moveTo(x1 , y1);
                            path.lineTo(x1 , y1+marge);
                            path.lineTo(x2-marge , y1+marge);
                            path.lineTo(x2-marge , y2);
                            path.lineTo(x2 , y2);}

                        break;
                    case 2:
                        tempx = x1;
                        tempy = y1;
                        x1 = x2;
                        y1 = y2;
                        x2 = tempx;
                        y2 = tempy;
                        if(x1<x2)
                            if(y1<y2)
                            {   path.moveTo(x1 , y1);
                                path.lineTo(x1+marge , y1);
                                path.lineTo(x1+marge , y2);
                                path.lineTo(x2 , y2);}
                            else
                            {   path.moveTo(x1 , y1);
                                path.lineTo(x1+marge , y1);
                                path.lineTo(x1+marge , y2);
                                path.lineTo(x2 , y2);}
                        else
                        if(y1<y2)
                        {path.moveTo(x1 , y1);
                            path.lineTo(x1+marge , y1);
                            path.lineTo(x1+marge , y1+(y2-y1)/2);
                            path.lineTo(x2-marge , y1+(y2-y1)/2);
                            path.lineTo(x2-marge , y2);
                            path.lineTo(x2 , y2);}
                        else
                        {path.moveTo(x1 , y1);
                            path.lineTo(x1+marge , y1);
                            path.lineTo(x1+marge , y1+(y2-y1)/2);
                            path.lineTo(x2-marge , y1+(y2-y1)/2);
                            path.lineTo(x2-marge , y2);
                            path.lineTo(x2 , y2);}

                        break;
                    case 0:
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }

        return path;
    }

    protected Path getPath_2(int x1,int y1,int x2,int y2,int d,int a)
    {Path path = new Path();
        return path;
    }

    protected Path getPath_3(int x1,int y1,int x2,int y2,int marge)
    {Path path = new Path();

        path.moveTo(x1 , y1);
        path.lineTo(x1 , y1-marge);
        path.lineTo(x2 , y1-marge);
        path.lineTo(x2 , y2);

        return path;
    }

    protected Path getPath_4(int x1,int y1,int x2,int y2,int d,int a)
    {Path path = new Path();
        return path;
    }

    protected Path getPath_5(int x1,int y1,int x2,int y2,int d,int a)
    {Path path = new Path();
        return path;
    }

    // The method that displays the popup.
    private void ShowNodesMenu(final Point p) {

        int popupWidth = 120;
        int popupHeight = 360;

        // Inflate the popup_layout.xml
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_layout, viewGroup);

        // Creating the PopupWindow
        NodesMenu = new PopupWindow(layout, popupWidth, popupHeight, true);

        Button binding = (Button) layout.findViewById(R.id.binding);

        binding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nodeList.add(new Node(new Point(p.x, p.y), BINDING, "", "Binding_"+id, 0));
                id++;
                NodesMenu.dismiss();
                postInvalidate();
            }
        });

        Button protein = (Button) layout.findViewById(R.id.protein);

        protein.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nodeList.add(new Node(new Point(p.x, p.y), PROTEIN, "", "Protein_"+id, 0));
                id++;
                NodesMenu.dismiss();
                postInvalidate();
            }
        });

        Button phospho = (Button) layout.findViewById(R.id.phospho);

        phospho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nodeList.add(new Node(new Point(p.x, p.y), PHOSPHO, "", "Phospho_"+id, 0));
                id++;
                NodesMenu.dismiss();
                postInvalidate();
            }
        });

        Button molecule = (Button) layout.findViewById(R.id.molecule);

        molecule .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nodeList.add(new Node(new Point(p.x, p.y), MOLECULE, "", "Molecule_"+id, 0));
                id++;
                NodesMenu.dismiss();
                postInvalidate();
            }
        });

        Button concept = (Button) layout.findViewById(R.id.concept);

        concept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nodeList.add(new Node(new Point(p.x, p.y), CONCEPT, "", "Concept_"+id, 0));
                id++;
                NodesMenu.dismiss();
                postInvalidate();
            }
        });

        Button degrad = (Button) layout.findViewById(R.id.degrad);

        degrad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nodeList.add(new Node(new Point(p.x, p.y), DEGRAD, "", "Degrad_"+id, 0));
                id++;
                NodesMenu.dismiss();
                postInvalidate();
            }
        });
        // Displaying the ProteinMenu at the specified location
        NodesMenu.showAtLocation(layout, Gravity.NO_GRAVITY, p.x, p.y);

    }

    // The method that displays the popup.
    private void ShowConnexionsMenu(final Point p,final int indice_dep,final int indice_arr,final int indice_dep_anchor,final int indice_arr_anchor) {

        int popupWidth = 160;
        int popupHeight = 300;

        // Inflate the popup_layout.xml
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup_connexions);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_connexions, viewGroup);

        // Creating the PopupWindow
        ConnexionsMenu = new PopupWindow(layout, popupWidth, popupHeight, true);

        Button binding = (Button) layout.findViewById(R.id.bind);

        binding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "binding", Toast.LENGTH_SHORT).show();
                connexionList.add(new Connexion(nodeList.get(indice_dep).getText(), nodeList.get(indice_arr).getText(), indice_dep, indice_arr,indice_dep_anchor,indice_arr_anchor, "CBINDING_"+id,CBINDING));
                id++;
                ConnexionsMenu.dismiss();
                postInvalidate();
            }
        });

        Button activation = (Button) layout.findViewById(R.id.activation);

        activation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Activation", Toast.LENGTH_SHORT).show();
                connexionList.add(new Connexion(nodeList.get(indice_dep).getText(), nodeList.get(indice_arr).getText(), indice_dep, indice_arr,indice_dep_anchor,indice_arr_anchor, "CACTIVATION_"+id,CACTIVATION));
                id++;
                ConnexionsMenu.dismiss();
                postInvalidate();
            }
        });

        Button degradation = (Button) layout.findViewById(R.id.degradation);

        degradation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Degra", Toast.LENGTH_SHORT).show();
                connexionList.add(new Connexion(nodeList.get(indice_dep).getText(), nodeList.get(indice_arr).getText(), indice_dep, indice_arr,indice_dep_anchor,indice_arr_anchor, "CDEGRADATION_"+id,CDEGRADATION));
                id++;
                ConnexionsMenu.dismiss();
                postInvalidate();
            }
        });

        Button inhibition = (Button) layout.findViewById(R.id.inhibition);

        inhibition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Inhi", Toast.LENGTH_SHORT).show();
                connexionList.add(new Connexion(nodeList.get(indice_dep).getText(), nodeList.get(indice_arr).getText(), indice_dep, indice_arr,indice_dep_anchor,indice_arr_anchor, "INHIBITION_"+id,INHIBITION));
                id++;
                ConnexionsMenu.dismiss();
                postInvalidate();
            }
        });

        Button phosphorisation = (Button) layout.findViewById(R.id.phosphorisation);

        phosphorisation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Phospho", Toast.LENGTH_SHORT).show();
                connexionList.add(new Connexion(nodeList.get(indice_dep).getText(), nodeList.get(indice_arr).getText(), indice_dep, indice_arr,indice_dep_anchor,indice_arr_anchor, "PHOSPHORISATION_"+id,PHOSPHORISATION));
                id++;
                ConnexionsMenu.dismiss();
                postInvalidate();
            }
        });
        // Displaying the ProteinMenu at the specified location
        ConnexionsMenu.showAtLocation(layout, Gravity.NO_GRAVITY, p.x, p.y);

    }
}