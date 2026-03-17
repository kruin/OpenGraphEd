package userInterface.menuAndToolBar;

import java.awt.Point;

public class CursorPositionThread extends Thread
{
  private final MenuAndToolBar menuAndToolbar;
  private Point lastCursorPoint;
  private boolean mouseIn;

  public CursorPositionThread(MenuAndToolBar menuAndToolbar)
  {
    super();
    this.menuAndToolbar = menuAndToolbar;
    setPriority(2);
  }

  public void mouseIn(boolean mouseIn)
  {
    this.mouseIn = mouseIn;
  }

  public void run()
  {
    while ( mouseIn )
    {
      try
      {
        Thread.sleep(100);
      }
      catch ( InterruptedException ie )
      {
        
      }
      if ( menuAndToolbar.getCursorPoint() != lastCursorPoint )
      {
        if ( menuAndToolbar.getCursorPoint() == null )
        {
          lastCursorPoint = null;
          menuAndToolbar.setCursorLocationText( "" );
        }
        else
        {
          lastCursorPoint = menuAndToolbar.getCursorPoint();
          final Point  cp = lastCursorPoint;
          Runnable updateController = new Runnable()
          {
            public void run()
            {
              menuAndToolbar.setCursorLocationText( "<html>x:" + cp.x +
                                                "<br>y:" + cp.y );
            }
          };
          try
          {
            javax.swing.SwingUtilities.invokeAndWait(updateController);
          }
          catch (Exception e)
          {
            e.printStackTrace();
          }
        }
      }
    }
    menuAndToolbar.updateCursorLocation(null);
    menuAndToolbar.setCursorLocationText("");
  }
}