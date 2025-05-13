package pgraph.util;

import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 28.01.2013
 * Time: 22:03
 * To change this template use File | Settings | File Templates.
 */
public class StringUtil {
    public static Point2D.Double pointFromString(String str)
    {
        String[] parts = str.split(" ");
        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
        Point2D.Double p = new Point2D.Double(x,y);
        return p;
    }

    public static String pointToString(Point2D.Double p)
    {
        return p.getX() + " " + p.getY();
    }

    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    public static String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s);
    }


    public static final String DATE_FORMAT_NOW = "dd_MM_yyyy";

    public static String now() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }

    public static DecimalFormat getMyDecimalFormatter() {
        DecimalFormat nf = new DecimalFormat();
        nf.setGroupingUsed(false);
        nf.setMaximumFractionDigits(3);
        nf.setMinimumFractionDigits(3);
        DecimalFormatSymbols custom=new DecimalFormatSymbols();
        custom.setDecimalSeparator('.');
        nf.setDecimalFormatSymbols(custom);
        return nf;
    }
}
