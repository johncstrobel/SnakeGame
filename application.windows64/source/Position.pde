public class Position {
  int x;
  int y;
  float positionSize = 40;
  float xBorderOffset = 10;
  float yBorderOffset = 10;
  
  Position(int xcoord,int ycoord){x = xcoord; y = ycoord;}
  Position(){x=0;y=0;}
  
  public String toString(){
    String retString = "("+x+","+y+")";
    return retString;
  }
  
  float xCoord(float xCoord, float xOffset){
    return xOffset + xBorderOffset + (xCoord * positionSize);  
  }
  
  float yCoord(float yCoord, float yOffset){
    return yOffset + yBorderOffset + (yCoord * positionSize);
  }
}
