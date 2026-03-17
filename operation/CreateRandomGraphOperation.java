package operation;

import java.util.Random;
import graphStructure.*;

public class CreateRandomGraphOperation
{
  public static void createRandomNodes(Graph g, int numNodes, int width, int height)
  {
    createRandomNodes(g, numNodes, width, height, System.currentTimeMillis());
  }
  
  public static void createRandomNodes(Graph g, int numNodes, int width, int height, long seed)
  {
    g.deleteAll();
    boolean useX;
    double interval;
    Random random = new Random(seed);
    if ( height <= width )
    {
      useX = true;
      interval = (double)width / (double)numNodes;
    }
    else
    {
      useX = false;
      interval = (double)height / (double)numNodes;
    }
    for ( int i=0; i<numNodes; i++ )
    {
      if ( useX )
      {
        g.createNode(new Location(i*interval, random.nextDouble()*height));
      }
      else
      {
        g.createNode(new Location(random.nextDouble()*width, i*interval));
      }
    }
  }
}