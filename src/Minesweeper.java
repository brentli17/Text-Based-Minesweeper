import java.util.*;

public class Minesweeper {
    private static int size;
    private static String [][] fieldActual;     //field with filled numbers and mines
    private static String [][] fieldDisplay;    //field that is displayed to the player
    private static int [][] revealHelper;    //used to aid in revealing, 0s are unvisited squares, 1s are visited
    private static final String MINE = " X ";
    private static final String FLAG = " F ";
    private static int row = 0;
    private static int col = 0;

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        boolean gameOver = false;
        double difficulty;

        System.out.println("Input size: ");
        size = scanner.nextInt();
        System.out.println("Choose Difficulty: \n- 1 Easy\n- 2 Moderate\n- 3 Hard\n- 4 Expert");
        difficulty = scanner.nextInt();

        //determines the number of mines that will be on fieldActual
        double numberOfMines = (size*size)*(difficulty / 10.0);

        fieldActual = new String[size][size];
        fieldDisplay = new String[size][size];
        revealHelper = new int [size][size];

        //initialize fieldActual and fieldDisplay with blanks
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                fieldActual[i][j] = "[ ]";
                fieldDisplay[i][j] = "[ ]";
            }
        }

        addMines(numberOfMines);
        int clickStatus;
        int nextMove;

        updateDanger();
        System.out.println();
        //displayFieldActual();

        //main game loop
        while(!gameOver){
            displayFieldDisplay();
            displayMenu();
            nextMove = scanner.nextInt();

            //verify input is valid
            while(nextMove != 1 && nextMove != 2 && nextMove != 3){
                System.out.println("Invalid choice");
                displayMenu();
                nextMove = scanner.nextInt();
            }

            switch (nextMove){
                case 1:     //select a space
                    nextMoveHelper();
                    clickStatus = selectSpace(row, col);
                    if(clickStatus == 1){   //square is a mine, player dies
                        gameOver = true;
                        revealAllMines();
                        displayFieldDisplay();
                        System.out.println("========================================\n" +
                                           "                You lost!               \n" +
                                           "========================================");
                    }
                    else{   //square is not a mine, reveal area
                        resetRevealHelper();
                        reveal(row,col);
                    }
                    break;

                case 2:     //place a flag
                    nextMoveHelper();
                    placeFlag(row, col);
                    break;

                case 3:     //remove a flag
                    nextMoveHelper();
                    removeFlag(row, col);
                    break;

                case 4:     //undo

                    break;
            }
            //check if all mines have been flagged, and all flags are on mines
            if(checkWin()){
                gameOver = true;
                System.out.println("========================================\n" +
                                   "                 You won!               \n" +
                                   "========================================");
            }
        }
    }

    //undos the previous move
    public static void undo(){
        
    }

    //reveals all empty spaces around the spot that was clicked
    public static int reveal(int r, int c){
        int rBound = r + 1;
        int cBound = c + 1;

        //if out of bounds, return
        if(r < 0 || c < 0 || r > size - 1 || c > size - 1){
            return 0;
        }

        //if already visited, return
        if(revealHelper[r][c] == 1){
            return 0;
        }

        revealHelper[r][c] = 1;

        //set displayed squares in the displayed field
        if(fieldActual[r][c].equals("[ ]")){   //change [ ] to empty spaces
            fieldDisplay[r][c] = "   ";
        }
        else{   //copy the numbers into the display field
            fieldDisplay[r][c] = fieldActual[r][c];
            return 0;
        }

        //if number, return
        if(fieldActual[r][c].equals("[1]") ||
           fieldActual[r][c].equals("[2]") ||
           fieldActual[r][c].equals("[3]") ||
           fieldActual[r][c].equals("[4]") ||
           fieldActual[r][c].equals("[5]") ||
           fieldActual[r][c].equals("[6]") ||
           fieldActual[r][c].equals("[7]") ||
           fieldActual[r][c].equals("[8]")){
           return 0;
        }

        //search adjacent squares
        for(int i = r - 1; i <= rBound; i++){
            for(int j = c - 1; j <= cBound; j++){

                //skip inputted square
                if(i == r && j == c){
                    j++;
                }

                reveal(i,j);
            }
        }
        return 0;
    }

    //either reveal a portion of fieldActual or kill the player
    public static int selectSpace(int r, int c){
        if(fieldActual[r][c].equals(MINE)){
            return 1;
        }
        else{
            return 2;
        }
    }

    //checks to see if all mines have been found with flags
    public static boolean checkWin(){
        boolean won = true;
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){

                if(fieldDisplay[i][j].equals(FLAG)){        //flag is not placed on a mine
                    if(!fieldActual[i][j].equals(MINE)){
                        won = false;
                    }
                }

                if(fieldActual[i][j].equals(MINE)){        //mine does not have a flag
                    if(!fieldDisplay[i][j].equals(FLAG)){
                        won = false;
                    }
                }

            }
        }
        return won;
    }

    //places a flag
    public static void placeFlag(int r, int c){
        fieldDisplay[r][c] = FLAG;
    }

    //removes a flag
    public static void removeFlag(int r, int c){
        fieldDisplay[r][c] = "[ ]";
    }

    //updates fieldActual after anything happens to it
    public static void updateDanger(){
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                if(detectDanger(i,j) != -1 && detectDanger(i,j) != 0){
                    fieldActual[i][j] = "[" + detectDanger(i,j) + "]";
                }
            }
        }
    }

    //returns the number of mines adjacent to the given square
    public static int detectDanger(int x, int y){
        //see if the passed in square is a mine
        if(fieldActual[x][y].equals(MINE)){
            return -1;
        }
        int dangerValue = 0;
        for (int i = x-1; i <= x+1; i++){
            for(int j = y-1; j <= y+1 ; j++){
                //out of bounds detection
                if(!(i < 0 || i > size - 1 || j < 0 || j > size - 1)){
                    if(fieldActual[i][j].equals(MINE)){
                        dangerValue++;
                    }
                }
            }
        }
        return dangerValue;
    }

    //add a specified amount of mines to fieldActual
    public static void addMines(double minesCount){
        Random rand = new Random();
        int minesPlaced = 0;

        while(minesPlaced < minesCount) {
            int randX = rand.nextInt(size);
            int randY = rand.nextInt(size);
            if (!(fieldActual[randY][randX].equals(MINE))) {
                fieldActual[randY][randX] = MINE;
                minesPlaced++;
            }
        }
    }

    //displays fieldDisplay
    public static void displayFieldDisplay(){
        //print top row
        System.out.print("\n     ");
        for(int i = 0; i < size; i++){
            if(i < 9){
                System.out.print(i + 1 + "  ");
            }
            else{
                System.out.print(i + 1 + " ");
            }
        }
        //print top border
        System.out.print("\n    ");
        for(int i = 0; i < size; i++){
            System.out.print("---");
        }
        System.out.println();
        //print sides
        for (int i = 0; i < size; i++){
            if(i < 9){
                System.out.print(i + 1 + " | ");
            }
            else{
                System.out.print(i + 1 + "| ");
            }
            for (int j = 0; j < size; j++){
                System.out.print(fieldDisplay[i][j]);
            }
            if(i < 9){
                System.out.print(" | " + (i + 1));
            }
            else{
                System.out.print(" |" + (i + 1));
            }
            System.out.println();
        }
        //print bottom border
        System.out.print("    ");
        for(int i = 0; i < size; i++){
            System.out.print("---");
        }
        //print bottom row
        System.out.print("\n     ");
        for(int i = 0; i < size; i++){
            if(i < 9){
                System.out.print(i + 1 + "  ");
            }
            else{
                System.out.print(i + 1 + " ");
            }
        }
        System.out.println();
    }

    //displays fieldActual
    public static void displayFieldActual(){
        System.out.print("\n    ");
        for(int i = 0; i < size; i++){
            if(i < 9){
                System.out.print(i + 1 + "  ");
            }
            else{
                System.out.print(i + 1 + " ");
            }
        }
        System.out.println();
        for (int i = 0; i < size; i++){
            if(i < 9){
                System.out.print(i + 1 + "  ");
            }
            else{
                System.out.print(i + 1 + " ");
            }
            for (int j = 0; j < size; j++){
                System.out.print(fieldActual[i][j]);
            }
            System.out.println();
        }
    }

    //display revealHelper
    public static void displayRevealHelper(){
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                System.out.print(revealHelper[i][j]);
            }
            System.out.println();
        }
    }

    //reveal all mines once the player dies
    public static void revealAllMines(){
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                if(fieldActual[i][j].equals(MINE)){
                    fieldDisplay[i][j] = MINE;
                }
            }
        }
    }

    //resets the revealHelper array to all 0s
    public static void resetRevealHelper(){
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                revealHelper[i][j] = 0;
            }
        }
    }

    //helper function for next move switch
    public static void nextMoveHelper(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter column: ");
        col = scanner.nextInt();

        //verify column input
        while(col < 1 || col > size){
            System.out.println("Invalid column\nEnter column:");
            col = scanner.nextInt();
        }

        System.out.println("Enter row: ");
        row = scanner.nextInt();

        //verify row input
        while(row < 1 || row > size){
            System.out.println("Invalid row\nEnter row:");
            row = scanner.nextInt();
        }

        //user inputs row/col #s starting from 1 instead of 0, correct row and col #s
        row--;
        col--;
    }

    //displays a menu of options for the player
    public static void displayMenu(){
        System.out.println("\n" +
                           "==============Menu==============\n" +
                           "1 -               Select a space\n" +
                           "2 -                 Place a flag\n" +
                           "3 -                Remove a flag\n" +
                           "4 -           Undo previous move\n" +
                           "================================");
    }
}