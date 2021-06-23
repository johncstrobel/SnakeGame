public class Fruit extends ArenaObject {
  
  float fruitOffset;

  Fruit(Position xy, int s){
   pos = xy;
   c = #FF1F1F;
   size = s;
   isSnake = false;
   isFruit = true;
   fruitOffset = 5;
  }
  
  void Display() {
    float x = pos.xCoord(pos.x, fruitOffset);
    float y = pos.yCoord(pos.y, fruitOffset);
    fill(c);
    square(x,y,size);
  }
  
  public String toString(){
    String retString;
    retString = "fruitOffset: "+Float.toString(fruitOffset);
    retString = retString + "\nposition:" + pos.toString();
    retString = retString + "\nsize: " + size;
    return retString;
  }
  
}//fruit class
