package graphStructure;

import java.awt.Point;

public class Location
{
  private double X;
  private double Y;
  private int x;
  private int y;

  public Location(Point aPoint)
  {
    X = x = aPoint.x;
    Y = y = aPoint.y;
  }

  public Location(Location aLocation)
  {
    x = aLocation.intX();
    y = aLocation.intY();
    X = aLocation.doubleX();
    Y = aLocation.doubleY();
  }

  public Location(int x, int y)
  {
    X = this.x = x;
    Y = this.y = y;
  }

  public Location(double X, double Y)
  {
    this.X = X;
    this.Y = Y;
    x = (int)Math.round(X);
    y = (int)Math.round(Y);
  }

  public int intX() { return x; }

  public int intY() { return y; }

  public double doubleX() { return X; }

  public double doubleY() { return Y; }

  public void setX(double newX)
  {
    X = newX;
    x = (int)Math.round(newX);
  }

  public void setY(double newY)
  {
    Y = newY;
    y = (int)Math.round(newY);
  }

  public void translate( int dx, int dy )
  {
    X += dx;
    x += dx;
    Y += dy;
    y += dy;
  }

  public boolean equals( Object o )
  {
    if ( o instanceof Location )
    {
      Location l = (Location)o;
      return l.X == X && l.Y == Y;
    }
    else if ( o instanceof Point )
    {
      Point p = (Point)o;
      return p.x == x && p.y == y;
    }
    else
    {
      return false;
    }
  }

  public String toString()
  {
    return ("Location [" + X + " (" + x + "), " + Y + " (" + y +")]" );
  }
}