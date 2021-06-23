Arena A;
boolean paused = true;
boolean collisionHappened = false;
int gameFrameRate = 10; //every 5 actual frames, the game steps forward one

void setup(){
  size(500,550); //boardsize: 480x480
  frameRate(60);
  A = new Arena(height,width);
  A.spawnFruit(8,6);
  A.spawnSnakeStart();

  
  A.Display();
  A.displayObjects();
}

void gameOver(){
}

void displayPauseSubtext(){}

void displayPause(){
  fill(#FFFFFF);
  textSize(32);
  textAlign(CENTER,CENTER);
  int textCoordX = (A.spaceSize*6)+10;
  int textCoordY = (A.spaceSize*6)+10;
  text("Paused",textCoordX,textCoordY);
  displayPauseSubtext();
}

void dumpTextToConsole(){
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

void keyPressed(){
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

void mousePressed(){
  if (paused){
    paused = false;
  } else {
    paused = true;
  }
}

void draw(){
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
