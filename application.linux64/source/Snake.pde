public class Snake extends ArenaObject {
 char direction; //north south east west (N S E W)
                 //indicates the direction THIS SEGMENT will next move
                 //n: +0,-1
                 //s: +0,+1
                 //e: +1,+0
                 //w: -1,+0
 float snakeOffset;
 Snake tail; 
 int numTails;
 Arena A;

 Snake(int s, Position xy, char dir){
   c = color(255);
   pos = xy;
   direction = dir;
   tail = null;
   isSnake = true;
   isFruit = false;
   size = s;
   snakeOffset = 10;
   numTails = 1;
 } //constructor
 
  public String toString(){
    String retString = "color: " + c;
    retString = retString +"\nposition: " + pos.toString();
    retString = retString + "\ndirection: " + direction;
    retString = retString + "\nsize: " + size;
    retString = retString + "\nsnakeOffset: " + snakeOffset;
    retString = retString + "\nnumTails: " + numTails;
    retString = retString + "\n tail: \n"+tail.toString();
    return retString;
  }
 
 void Display(){
   float x = pos.xCoord(pos.x, snakeOffset);
   float y = pos.yCoord(pos.y, snakeOffset);
   fill(c);
   square(x,y,size);
 } //display
 
 Position nextPosition(){
   Position nextPosition; 
   if (direction == 'N'){ //this unit will travel north next
     //pos.x,pos.y-1),'N'
     nextPosition = new Position(pos.x,pos.y-1);
   } else if (direction == 'S') {
     //pos.x,pos.y+1),'S'
     nextPosition = new Position(pos.x,pos.y+1);
   } else if (direction == 'E') {
     //pos.x+1,pos.y),'E'
     nextPosition = new Position(pos.x+1,pos.y);
   } else if (direction == 'W') {
     //pos.x-1,pos.y),'W'
     nextPosition = new Position(pos.x-1,pos.y);
   } else {
     throw new RuntimeException("snake.nextPosition: improper direction char");
   }
   return nextPosition;
 }
 
  Position move(){
    //println(pos.x+","+pos.y);
    A.deleteFromGrid(pos.x,pos.y); // delete self from grid
    if(tail != null){
      tail.move();
      tail.direction = direction;
    }
    this.pos = this.nextPosition(); 
    try {
      A.addToGrid(this, pos.x,pos.y); //re-add self to grid
    } catch(RuntimeException e){
       println(e.toString());
       throw new RuntimeException("snake.move: caught an exception in move()");
    }
    return pos;
 } //move

  Position addTail(){
    if (tail != null) {
      return tail.addTail();
    } else {
      Position tailPos;
      if (direction == 'N'){ //spawn tail segment in opposite direction of current movement
        tailPos = new Position(pos.x,pos.y+1);
      } else if (direction == 'S') {
        tailPos = new Position(pos.x,pos.y-1);
      } else if (direction == 'E') {
        tailPos = new Position(pos.x-1,pos.y);
      } else if (direction == 'W') {
        tailPos = new Position(pos.x+1,pos.y);
      } else {
        throw new RuntimeException("snake.addTail: improper direction char");
      }
      
      tail = new Snake(size,tailPos,direction);
      A.addToGrid(tail,tailPos.x,tailPos.y);
      tail.A = this.A;
      numTails++;
      return tailPos;
    }// if else
  } //addTail
  
} // snake class
