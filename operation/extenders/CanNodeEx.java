package operation.extenders;

import graphStructure.*;

public class CanNodeEx extends NodeExtender
{
  protected int canonicalNumber;
  protected int outerFaceEdgeCount;
  protected boolean isOnOuterFace;
  protected CanNodeEx candidateLeft;
  protected CanNodeEx candidateRight;

  public CanNodeEx()
  {
    super();
    canonicalNumber = -1;
  }
  
  public void setCanonicalNumber(int canNum) { canonicalNumber = canNum; }
  
  public int getCanonicalNumber() { return canonicalNumber; }
  
  public void setOuterFaceEdgeCount(int count) { outerFaceEdgeCount = count; }
  
  public int getOuterFaceEdgeCount() { return outerFaceEdgeCount; }
  
  public void incrementOuterFaceEdgeCount() { outerFaceEdgeCount++; }
  
  public void decrementOuterFaceEdgeCount() { outerFaceEdgeCount--; }
  
  public void setIsOnOuterFace(boolean isOn) { isOnOuterFace = isOn; }
  
  public boolean isOnOuterFace() { return isOnOuterFace; }
  
  public CanNodeEx getCandidateLeft() { return candidateLeft; }
  
  public void setCandidateLeft(CanNodeEx cand) { candidateLeft = cand; }
  
  public CanNodeEx getCandidateRight() { return candidateRight; }
  
  public void setCandidateRight(CanNodeEx cand) { candidateRight = cand; }
}