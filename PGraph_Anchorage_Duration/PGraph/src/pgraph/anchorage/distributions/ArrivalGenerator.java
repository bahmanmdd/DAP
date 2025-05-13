package pgraph.anchorage.distributions;

import pgraph.anchorage.ArrivalInterface;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 07.11.2013
 * Time: 09:02
 * To change this template use File | Settings | File Templates.
 */
public interface ArrivalGenerator {

   public ArrivalInterface generate();
   public void setScale(double scale);
   public void reset();


}
