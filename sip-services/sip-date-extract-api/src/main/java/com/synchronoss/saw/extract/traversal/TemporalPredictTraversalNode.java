package com.synchronoss.saw.extract.traversal;

import java.util.ArrayList;
import java.util.List;

public class TemporalPredictTraversalNode {

  public char charcter;
  boolean isDigit = false;
  public int level;
  public List<TemporalPredictTraversalNode> childern;
  public TemporalPredictTraversalNode parent;
  public boolean explictDateFragment=false;

  public TemporalPredictTraversalNode getChild(char c) {
      if (childern == null) {
          return null;
      }
      for (TemporalPredictTraversalNode child : childern) {
          if (c == child.charcter) {
              return child;
          }
      }
      return null;
  }

  public void addChild(TemporalPredictTraversalNode child) {
      if (childern == null) {
          childern = new ArrayList<TemporalPredictTraversalNode>();
      }
      child.level=this.level+1;
      childern.add(child);
  }
  
  public boolean hasChildern(){
       if (childern == null) {
           return false;
       }else{
           return true;
       }
  }
  
  public int childrenCount(){
       if (childern == null) {
           return 0;
       }else{
           return childern.size();
       }
  }

  @Override
  public String toString() {
      return "PredictionModelNode [charcter=" + charcter + ", isDigit=" + isDigit + ", level=" + level + ", parent=" + parent + ", explictDateFragment=" + explictDateFragment
              + ", childern=" + childern + "]";
  }

}
