import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class SnakeGame extends PApplet {

Arena A;
boolean paused = true;
boolean collisionHappened = false;
int gameFrameRate = 10; //every 5 actual frames, the game steps forward one

public void setup(){
   //boardsize: 480x480
  frameRate(60);
  A = new Arena(height,width);
  A.spawnFruit(8,6);
  A.spawnSnakeStart();

  
  A.Display();
  A.displayObjects();
}

public void gameOver(){
}

public void displayPauseSubtext(){}

public void displayPause(){
  fill(0xffFFFFFF);
  textSize(32);
  textAlign(CENTER,CENTER);
  int textCoordX = (A.spaceSize*6)+10;
  int textCoordY = (A.spaceSize*6)+10;
  text("Paused",textCoordX,textCoordY);
  displayPauseSubtext();
}

public void dumpTextToConsole(){
println("paused: "+paused+
       "\ncollisionHappened: "+collisionHappened+
       "\nA.aHeight: "+A.aHeight+
       "\nA.aWidth: "+A.aWidth+
       "\nA.boardHeight: "+A.boardHeight+
       "\nA.boardWidth: "+A.boardWidth+
       "\nA.xDim: "+A.xDim+
       "\nA.yDim: "+A.yDim+
       "\nA.xOffset: "+A.xOffset+
       "\nA.yOffset: "+A.yOffset+
       "\nA.menuSize: "+A.menuSize+
       "\nA.maxSpaces: "+A.maxSpaces+
       "\nA.spaceSize: "+A.spaceSize+
       "\nA.squareColor1: "+A.squareColor1+
       "\nA.squareColor2: "+A.squareColor2+
       "\nA.objectOffset: "+A.objectOffset+
       // objects?
       "\nA.numEmptySpaces: "+A.numEmptySpaces+
       // mainSnake ?
        "\nA.snakeAte: "+A.snakeAte+
        "\n"+A.toStringObjects());
}

public void keyPressed(){
  if(keyPressed){
    if (key==CODED){
      if (keyCode==UP && A.mainSnake.direction != 'S'){
        A.mainSnake.direction = 'N';
      } else if (keyCode==DOWN && A.mainSnake.direction != 'N'){
        A.mainSnake.direction = 'S';
      } else if (keyCode==RIGHT && A.mainSnake.direction != 'W'){
        A.mainSnake.direction = 'E';
      } else if (keyCode==LEFT && A.mainSnake.direction != 'E'){
        A.mainSnake.direction = 'W';
      }
    }
  }
}

public void mousePressed(){
  if (paused){
    paused = false;
  } else {
    paused = true;
  }
}

public void draw(){
  if(paused){
    displayPause();
  }
  if(frameCount%gameFrameRate == 0 && !paused){
    //background(0);
    
    //**check for collisions (in upcoming move)
    collisionHappened = A.detectCollision();
    if(collisionHappened){
      gameOver();
      exit();
    }
    
    //**move snake
    
    if(!collisionHappened){
      try {
        A.moveSnake();
      } catch (Exception e) {
        dumpTextToConsole();
        throw e;
      }//try/catch
    }//if no collision, move snake
    
    //**lengthen snake
    //**spawn new fruit if necessary
    if(A.snakeAte){
      A.snakeEatFruit();
      A.snakeAte = false;
    }
    
    //**draw it all
    A.Display();
    A.displayObjects();
  }//gameFrameRate
}
public class Arena {
  int aHeight;
  int aWidth;
  int boardHeight; //dimensions in pixels
  int boardWidth;
  int xDim; //dimensions in game tiles
  int yDim;
  int xOffset = 10;
  int yOffset = 10;
  int menuSize = 50;
  int maxSpaces;
  int spaceSize = 40; //size of each game tile
  int squareColor1;
  int squareColor2;
  
  float objectOffset;
  
  ArenaObject [][] objects;
  int numEmptySpaces;
  
  Snake mainSnake;
  boolean snakeAte;
  
  Arena(){
    aHeight = 100;
    aWidth = 100;
    boardHeight = 10;
    boardWidth = 10;
    maxSpaces = 1;
    snakeAte = false;
  }
  
  Arena (int h,int w){
    aHeight = h;
    aWidth = w;
    boardHeight = h-((xOffset * 2)+menuSize); 
    boardWidth = w-(yOffset * 2);
    xDim = boardHeight/spaceSize;
    yDim = boardWidth/spaceSize;
    maxSpaces = xDim*yDim;
    numEmptySpaces = maxSpaces;
    squareColor1 =0xff1EAF1F;
    squareColor2 =0xff6BCB6B;
    objectOffset = spaceSize/4;
    
    objects = new ArenaObject[xDim][yDim];
    snakeAte = false;
  }
  
  public String toStringObjects(){
    String retString = "non-snake objects: ";
    for(int i = 0; i < xDim; i++){
      for(int j = 0; i < yDim; j++){
        if (objects[i][j] != null){
          if (!objects[i][j].isSnake){
            retString = retString +"\n" + objects[i][j].toString();
          }
        }
      }
    }
    retString = retString + "\n**snakes**\n" + mainSnake.toString();
    return retString;
  }
  
  public boolean squareIsWall(int xCoord, int yCoord){
    if (xCoord >= xDim || yCoord >= yDim || xCoord < 0 || yCoord < 0){
      //println("squareIsWall: "+xCoord+","+yCoord);
      return true;
    } else {
      return false;
    }
  }
  
  public boolean squareIsFilled(int xCoord, int yCoord){
    //println("squareIsFilled: "+xCoord+","+yCoord);
    if (xCoord >= xDim || yCoord >= yDim || xCoord < 0 || yCoord < 0){
      return true;
    } else
    return objects[xCoord][yCoord] != null;
  }
  
  public boolean squareIsEmpty(int xCoord, int yCoord){
    if(xCoord >= xDim || yCoord >= yDim){
      return false;
    }
    return objects[xCoord][yCoord] == null; 
  }
  
  public void checkSpaces(){
    println("x | y | empty?");
    for (int i = 0; i < xDim; i++){
      for (int j = 0; j < yDim; j++){
        println(i+" | "+j+" | "+squareIsFilled(i,j));
      }
    }
  }//checkSpaces
  
  public void addToGrid(ArenaObject obj, int xCoord, int yCoord ){
    if(squareIsFilled(xCoord,yCoord)){
      throw new RuntimeException("Arena.addToGrid: Space is not null");
    }
    objects[xCoord][yCoord] = obj;
    numEmptySpaces--;
  }//addToGrid
  
  public void deleteFromGrid(int xCoord, int yCoord){
    if(squareIsEmpty(xCoord,yCoord)){
      throw new RuntimeException("Arena.deleteFromGrid: tried to delete an empty space");
    }
    objects[xCoord][yCoord] = null;
    numEmptySpaces++;
  }//deleteFromGrid
   
  public Position findEmptyPosition(){
    if (numEmptySpaces == 0){
      throw new RuntimeException("Arena.findEmptyPosition: no available spaces to return");
    }
    int xCoord, yCoord;
    xCoord = PApplet.parseInt(random(xDim));
    yCoord = PApplet.parseInt(random(yDim));
    while(squareIsFilled(xCoord,yCoord)){
      xCoord = PApplet.parseInt(random(xDim));
      yCoord = PApplet.parseInt(random(yDim));
    }
    return new Position(xCoord,yCoord);
  }
  
  public Position spawnFruit(int x, int y){
    Position fruitPos = new Position(x,y);
    Fruit newFruit = new Fruit(fruitPos, spaceSize-10);
    addToGrid(newFruit,x,y);
    
    return fruitPos;
  }// spawnFruit
  
  public Position spawnRandomFruit(){    
    Position fruitPos = findEmptyPosition();
    spawnFruit(fruitPos.x,fruitPos.y);
    
    return fruitPos;
  }
  
  public Position spawnSnakeStart(){
    Position bobPos = new Position(3,3);
    Snake bob = new Snake(20,bobPos,'E');
    addToGrid(bob,bob.pos.x,bob.pos.y);
    mainSnake = bob;
    bob.A = this;
    bob.addTail();
    return bob.pos;
  }
  
  public void snakeEatFruit(){
    mainSnake.addTail();
    spawnRandomFruit();
  }

  public boolean detectCollision(){ //returns true if collision
    Position nextPos = mainSnake.nextPosition();
    int nextX = nextPos.x;
    int nextY = nextPos.y;
    if (squareIsWall(nextX,nextY)){
      return true;
    } else if (squareIsFilled(nextX,nextY)){
      //if fruit
      if(objects[nextX][nextY].isFruit){
        snakeAte = true;
        deleteFromGrid(nextX,nextY);
        return false;
      } else if (objects[nextX][nextY].isSnake){
        return true;
      }//else if is fruit/is snake
      return false;
    }//else if square filled
    return false;
  }//detectCollision
  
  public Position moveSnake(){
    return mainSnake.move();
  }

  public void displayObjects(){
    Position currentPos;
    float currentX;
    float currentY;
    for (int i = 0; i < xDim; i++){
      for (int j = 0; j < yDim; j++){
        if (objects[i][j] != null){
          objects[i][j].Display();
        }        
      }
    }
  } // displayObjects
  
  public void Display(){
    background(0xff007601);
    int curXPos;
    int curYPos;
    for(int i = 0; i < xDim; i++){
      for(int j = 0; j< yDim; j++){
        if (i%2 == 0) { 
          if(j%2==0){
            fill(squareColor1);
          } else {
            fill(squareColor2);
          }
        } else {// x is odd
          if(j%2==0){
            fill(squareColor2);
          } else {
            fill(squareColor1);            
          }
        }
        curXPos = xOffset + (i*spaceSize);
        curYPos = yOffset + (j*spaceSize);
        square(curXPos,curYPos,spaceSize);
        
      } //for j
    } //for i
  } //Display
} // Arena class
public class ArenaObject {
  Position pos;
  int c;
  boolean isFruit;
  boolean isSnake;
  int size;
  public void Display(){}
} // ArenaObject class
public class CollisionException extends Exception { 
    public CollisionException(String errorMessage) {
        super(errorMessage);
    }
}
public class Fruit extends ArenaObject {
  
  float fruitOffset;

  Fruit(Position xy, int s){
   pos = xy;
   c = 0xffFF1F1F;
   size = s;
   isSnake = false;
   isFruit = true;
   fruitOffset = 5;
  }
  
  public void Display() {
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
  
  public float xCoord(float xCoord, float xOffset){
    return xOffset + xBorderOffset + (xCoord * positionSize);  
  }
  
  public float yCoord(float yCoord, float yOffset){
    return yOffset + yBorderOffset + (yCoord * positionSize);
  }
}
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
 
 public void Display(){
   float x = pos.xCoord(pos.x, snakeOffset);
   float y = pos.yCoord(pos.y, snakeOffset);
   fill(c);
   square(x,y,size);
 } //display
 
 public Position nextPosition(){
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
 
  public Position move(){
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

  public Position addTail(){
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
  public void settings() {  size(500,550); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "SnakeGame" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
