package com.synchronoss.saw.extract.traversal;

public class TrieTreeSymbols implements Comparable<TrieTreeSymbols> {

  public String symbol="";
  
  public int leftSpace=0;
  
  public int level=0;

  public TrieTreeSymbols(String symbol, int leftSpace, int level) {
      super();
      this.symbol = symbol;
      this.leftSpace = leftSpace;
      this.level = level;
  }

  public TrieTreeSymbols(String symbol) {
      super();
      this.symbol = symbol;
  }

  @Override
  public String toString() {
      return "TreeChar [symbol=" + symbol + ", level=" + level+", leftSpace=" + leftSpace +  "]";
  }

  @Override
  public int compareTo(TrieTreeSymbols arg0) {
      if(level>arg0.level){
          return 1;
      }
      if(level<arg0.level){
          return -1;
      }
      return 0;
  }
}
