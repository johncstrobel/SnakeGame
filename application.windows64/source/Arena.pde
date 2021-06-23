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
  color squareColor1;
  color squareColor2;
  
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
    squareColor1 =#1EAF1F;
    squareColor2 =#6BCB6B;
    objectOffset = spaceSize/4;
    
    objects = new ArenaObject[xDim][yDim];
    snakeAte = false;
  }
  
  String toStringObjects(){
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
  
  boolean squareIsWall(int xCoord, int yCoord){
    if (xCoord >= xDim || yCoord >= yDim || xCoord < 0 || yCoord < 0){
      //println("squareIsWall: "+xCoord+","+yCoord);
      return true;
    } else {
      return false;
    }
  }
  
  boolean squareIsFilled(int xCoord, int yCoord){
    //println("squareIsFilled: "+xCoord+","+yCoord);
    if (xCoord >= xDim || yCoord >= yDim || xCoord < 0 || yCoord < 0){
      return true;
    } else
    return objects[xCoord][yCoord] != null;
  }
  
  boolean squareIsEmpty(int xCoord, int yCoord){
    if(xCoord >= xDim || yCoord >= yDim){
      return false;
    }
    return objects[xCoord][yCoord] == null; 
  }
  
  void checkSpaces(){
    println("x | y | empty?");
    for (int i = 0; i < xDim; i++){
      for (int j = 0; j < yDim; j++){
        println(i+" | "+j+" | "+squareIsFilled(i,j));
      }
    }
  }//checkSpaces
  
  void addToGrid(ArenaObject obj, int xCoord, int yCoord ){
    if(squareIsFilled(xCoord,yCoord)){
      throw new RuntimeException("Arena.addToGrid: Space is not null");
    }
    objects[xCoord][yCoord] = obj;
    numEmptySpaces--;
  }//addToGrid
  
  void deleteFromGrid(int xCoord, int yCoord){
    if(squareIsEmpty(xCoord,yCoord)){
      throw new RuntimeException("Arena.deleteFromGrid: tried to delete an empty space");
    }
    objects[xCoord][yCoord] = null;
    numEmptySpaces++;
  }//deleteFromGrid
   
  Position findEmptyPosition(){
    if (numEmptySpaces == 0){
      throw new RuntimeException("Arena.findEmptyPosition: no available spaces to return");
    }
    int xCoord, yCoord;
    xCoord = int(random(xDim));
    yCoord = int(random(yDim));
    while(squareIsFilled(xCoord,yCoord)){
      xCoord = int(random(xDim));
      yCoord = int(random(yDim));
    }
    return new Position(xCoord,yCoord);
  }
  
  Position spawnFruit(int x, int y){
    Position fruitPos = new Position(x,y);
    Fruit newFruit = new Fruit(fruitPos, spaceSize-10);
    addToGrid(newFruit,x,y);
    
    return fruitPos;
  }// spawnFruit
  
  Position spawnRandomFruit(){    
    Position fruitPos = findEmptyPosition();
    spawnFruit(fruitPos.x,fruitPos.y);
    
    return fruitPos;
  }
  
  Position spawnSnakeStart(){
    Position bobPos = new Position(3,3);
    Snake bob = new Snake(20,bobPos,'E');
    addToGrid(bob,bob.pos.x,bob.pos.y);
    mainSnake = bob;
    bob.A = this;
    bob.addTail();
    return bob.pos;
  }
  
  void snakeEatFruit(){
    mainSnake.addTail();
    spawnRandomFruit();
  }

  boolean detectCollision(){ //returns true if collision
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
  
  Position moveSnake(){
    return mainSnake.move();
  }

  void displayObjects(){
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
  
  void Display(){
    background(#007601);
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
