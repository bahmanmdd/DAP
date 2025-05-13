package pgraph.util;

import java.awt.*;
import java.lang.reflect.Field;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 13.01.2013
 * Time: 19:56
 * To change this template use File | Settings | File Templates.
 */
public class Pen {
    public static final float DEFAULT_THICKNESS = 1.5f;

    public enum PenStyle{ PS_Normal, PS_Pointed, PS_Dashed}  ;
    public Color color = Color.black;
    public float thickness = DEFAULT_THICKNESS;
    public PenStyle style;

    public Pen(Color c, float t,PenStyle s)
    {
        color=c;
        thickness=t;
        style = s;
    }

    public Pen(Color c,PenStyle s)
    {
        color=c;
        style = s;
    }

    public Pen()
    {
    }

    public Pen(Color c)
    {
        color=c;
        style = PenStyle.PS_Normal;
    }

    public Pen(String str)
    {
        fromString(str);
    }

    private void fromString(String str) {
       String[] tokens = str.split(" ");
       /*todo:*/
       try
       {
           Field f = Color.class.getField(tokens[0]);
           color = (Color)f.get(null);
       }
       catch (Exception e)
       {
           color = Color.black;
       }
       style = DefaultPen.style;
       thickness = DefaultPen.thickness;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pen pen = (Pen) o;

        if (thickness != pen.thickness) return false;
        if (!color.equals(pen.color)) return false;
        if (style != pen.style) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = color != null ? color.hashCode() : 0;
        temp = thickness != +0.0d ? Double.doubleToLongBits(thickness) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (style != null ? style.hashCode() : 0);
        return result;
    }

    public static final Pen DefaultPen= new Pen(Color.black,PenStyle.PS_Normal);
    public static final Pen IntersectingEdgePen= new Pen(Color.red,PenStyle.PS_Normal);
}
