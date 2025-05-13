package pgraph;

import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dindar.oz
 * Date: 12/14/12
 * Time: 4:41 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ObstacleGenerator
{
    public List<? extends ObstacleInterface> generate() throws IOException;
}
