/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qlearning;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
/**
 *
 * @author A
 */
public class QLearning {

    private static final double lr = 0.2; // Learning rate
    private static final double gamma = 0.1; // Eagerness - 0 looks in the near future, 1 looks in the distant future
    private static final int GridWidth = 10;
    private static final int GridHeight = 10;
    private final int statesCount = 243;
    private static int reward,rewardstep = 0;
    private final int penaltyWall = -5;
    private final int penaltyEmptyCan = -2;
    private static char[][] Grid;  // Maze read from file
    private static double[] R;       // Reward lookup
    private static double[][] Q;    // Q learning
    static String pools[];
    static int randomNum1=0,randomNum2=0;
    private static double E=1;

    private static String[] Actions={"MN","MS","ME","MW","PC"};
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
         QLearning ql = new QLearning();
         ql.initGrid();
         ql.initQMatrix();
         System.out.println("Training");
         ql.train();
         //ql.printQ();
         System.out.println("Testing");
         ql.test();
    }
    void printQ() {
        System.out.println("Q matrix");
        for (int i = 0; i < Q.length; i++) {
            System.out.print("From state " + i + ":  ");
            for (int j = 0; j < Q[i].length; j++) {
                System.out.printf("%6.2f ", (Q[i][j]));
            }
            System.out.println();
        }
    }
    
    public static void buildStrings(int length){
            int pool[] = {1, 2, 3};
            int[] indexes = new int[length];
            pools = new String[243];

          // In Java all values in new array are set to zero by default
          // in other languages you may have to loop through and set them.
            int pMax = pool.length;  // stored to speed calculation
            int z=0;
            while (indexes[0] < pMax){ //if the first index is bigger then pMax we are done
            // print the current permutation
            //for(int g=0;g<243;g++)
            
            for (int i = 0; i < length; i++){
              //System.out.print(pool[indexes[i]]);//print each character      
              if(i==0)
              pools[z] = Integer.toString(pool[indexes[i]]);    
              else
              pools[z] += pool[indexes[i]];    
              }
            z=z+1;
            //System.out.println();            
            // increment indexes
            indexes[length-1]++; // increment the last index
            for (int i = length-1; indexes[i] == pMax && i > 0; i--){ // if increment overflows 
              indexes[i-1]++;  // increment previous index
              indexes[i]=0;   // set current index to zero  
            }     
          }
//            for(int x=0;x<243;x++)
//            {
//                System.out.println("Value"+pools[x]);
//            }
            
}
            
    public static void initGrid() {

        //R = new int[statesCount][statesCount];
        Grid = new char[GridHeight][GridWidth];
        
        Random random = new Random();
        int randomNum = random.nextInt(4);
        
        int occupiedSpots = 0;

        while(occupiedSpots < 50){
        int x = random.nextInt(GridWidth);
        int y = random.nextInt(GridHeight);
        Grid[x][y]='C';
        occupiedSpots ++;        
        }
        
        for(int i=0;i<10;i++)
        {
            for(int j=0;j<10;j++)
            {
                if(Grid[i][j]!= 'C')
                {             
                    Grid[i][j]='E';
                }
             //   System.out.print(Grid[i][j]);
            }
            //System.out.println();
        }
        
        Random rand = new Random();
        int max=9,min=0;

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        randomNum1 = rand.nextInt((max - min) + 1) + min;
        randomNum2 = rand.nextInt((max - min) + 1) + min;

    }        
    
    public void initQMatrix()
    {        
        buildStrings(5);
        //System.out.println(pools[0]);
        
        Q = new double[statesCount][6];
        
       
        for(int a=0;a<243;a++)
        {
            for(int b=0;b<5;b++)
            {
//                if(b==0)
//                {
//                   Q[a][0]=Integer.valueOf(pools[a]);
//                }
//                else
                Q[a][b]=0;
            }
        }
//        for(int a=0;a<243;a++)
//        {
//            for(int b=0;b<6;b++)
//            {
//                System.out.print(Q[a][b]);
//            }
//            System.out.println();
//        }  
    }
    
    
    public static void train()
    {
        R=new double[5000];
        Random rand=new Random();
        String curState="";        
        String nextState="";
        int y=0,s=0;
        for(int N=0;N<5000;N++)        {
            y=y+1;
            s=s+1;
            initGrid();
            nextState="";
            curState="";
            reward=0;
            for(int M=0;M<200;M++)            {     
                reward=0;
                if(M==0){
                if(Grid[randomNum1][randomNum2]=='C')
                    {
                     curState +="2";   
                    }
                    else
                    {
                      curState +="1";  
                    }
                    if(randomNum1-1!=-1)
                    {if(Grid[randomNum1-1][randomNum2]=='C')
                    {
                        curState +="2";
                    }else
                    {
                      curState +="1";  
                    }
                    }
                    else{
                        curState +="3";
                    }
                    if(randomNum1+1!=10)
                    {
                        if(Grid[randomNum1+1][randomNum2]=='C')
                    {
                        curState +="2";
                    }else
                    {
                      curState +="1";  
                    }
                    }
                    else
                    {
                        curState +="3";
                    }
                    if(randomNum2-1!=-1)
                    {
                        if(Grid[randomNum1][randomNum2-1]=='C')
                    {
                        curState +="2";
                    }else
                    {
                      curState +="1";  
                    }
                    }
                    else
                    {
                        curState +="3";
                    }
                    if(randomNum2+1!=10)
                    {
                        if(Grid[randomNum1][randomNum2+1]=='C')
                    {
                        curState +="2";
                    }else
                    {
                      curState +="1";  
                    }
                    }
                    else
                    {
                        curState +="3";
                    }
                    
                }
                int index=0;
                String action="";
                //System.out.println("Cur:"+curState);                
                if(E>(1-E)){
                index = rand.nextInt(Actions.length);
                action = Actions[index];
                }else{
                 if(randomNum1-1!=-1)
                    {if(Grid[randomNum1-1][randomNum2]=='C')
                    {
                        action="MN";
                    }
                    }
                 else if(randomNum1+1!=10)
                     {if(Grid[randomNum1+1][randomNum2]=='C')
                    {
                        action="MS";
                    }
                    }
                 else if(randomNum2-1!=-1)
                 {if(Grid[randomNum1][randomNum2-1]=='C')
                    {
                        action="MW";
                    }
                    }
                 else if(randomNum2+1!=10)
                 {if(Grid[randomNum1][randomNum2+1]=='C')
                    {
                        action="ME";
                    }
                    }
                 else{
                     index = rand.nextInt(Actions.length);
                action = Actions[index];
                 }
                }
                //System.out.println(action);
                if(action.equals("MN"))
                {
                    randomNum1=randomNum1-1;
                    randomNum2=randomNum2;                    
                    
                    //crashes wall
                    if(randomNum1==-1 || randomNum2==-1 || randomNum2==10 || randomNum1==10)
                    {
                        reward+=-5;
                        int max=9,min=0;

                        // nextInt is normally exclusive of the top value,
                        // so add 1 to make it inclusive
                        randomNum1 = rand.nextInt((max - min) + 1) + min;
                        randomNum2 = rand.nextInt((max - min) + 1) + min;
                        nextState=curState;
                    }
                    else{
                    if(Grid[randomNum1][randomNum2]=='C')
                    {
                     nextState +="2";   
                     reward+=10;
                    }
                    else
                    {
                      nextState +="1";  
                    }
                    if(randomNum1-1!=-1)
                    {if(Grid[randomNum1-1][randomNum2]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else{
                        nextState +="3";
                    }
                    if(randomNum1+1!=10)
                    {
                        if(Grid[randomNum1+1][randomNum2]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else
                    {
                        nextState +="3";
                    }
                    if(randomNum2-1!=-1)
                    {
                        if(Grid[randomNum1][randomNum2-1]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else
                    {
                        nextState +="3";
                    }
                    if(randomNum2+1!=10)
                    {
                        if(Grid[randomNum1][randomNum2+1]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else
                    {
                        nextState +="3";
                    }
                    
                    }
                }
                else if(action.equals("MS"))
                {
                    randomNum1=randomNum1+1;
                    randomNum2=randomNum2;                               
                    
                      if(randomNum1==-1 || randomNum2==-1 || randomNum2==10 || randomNum1==10)
                    {
                        reward+=-5;
                        int max=9,min=0;

                        // nextInt is normally exclusive of the top value,
                        // so add 1 to make it inclusive
                        randomNum1 = rand.nextInt((max - min) + 1) + min;
                        randomNum2 = rand.nextInt((max - min) + 1) + min;
                        nextState=curState;
                    }
                    else{
                    if(Grid[randomNum1][randomNum2]=='C')
                    {
                     nextState +="2";   
                     reward+=10;
                    }
                    else
                    {
                      nextState +="1";  
                    }
                    if(randomNum1-1!=-1)
                    {if(Grid[randomNum1-1][randomNum2]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else{
                        nextState +="3";
                    }
                    if(randomNum1+1!=10)
                    {
                        if(Grid[randomNum1+1][randomNum2]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else
                    {
                        nextState +="3";
                    }
                    if(randomNum2-1!=-1)
                    {
                        if(Grid[randomNum1][randomNum2-1]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else
                    {
                        nextState +="3";
                    }
                    if(randomNum2+1!=10)
                    {
                        if(Grid[randomNum1][randomNum2+1]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else
                    {
                        nextState +="3";
                    }
                    
                    }
                }
                else if(action.equals("ME"))
                {
                    randomNum1=randomNum1;
                    randomNum2=randomNum2+1;            
                      if(randomNum1==-1 || randomNum2==-1 || randomNum2==10 || randomNum1==10)
                    {
                        reward+=-5;
                        int max=9,min=0;

                        // nextInt is normally exclusive of the top value,
                        // so add 1 to make it inclusive
                        randomNum1 = rand.nextInt((max - min) + 1) + min;
                        randomNum2 = rand.nextInt((max - min) + 1) + min;
                        nextState=curState;
                    }
                    else{
                    if(Grid[randomNum1][randomNum2]=='C')
                    {
                     nextState +="2";   
                     reward+=10;
                    }
                    else
                    {
                      nextState +="1";  
                    }
                    if(randomNum1-1!=-1)
                    {if(Grid[randomNum1-1][randomNum2]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else{
                        nextState +="3";
                    }
                    if(randomNum1+1!=10)
                    {
                        if(Grid[randomNum1+1][randomNum2]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else
                    {
                        nextState +="3";
                    }
                    if(randomNum2-1!=-1)
                    {
                        if(Grid[randomNum1][randomNum2-1]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else
                    {
                        nextState +="3";
                    }
                    if(randomNum2+1!=10)
                    {
                        if(Grid[randomNum1][randomNum2+1]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else
                    {
                        nextState +="3";
                    }
                    
                    }
                }
                else if(action.equals("MW"))
                {
                    randomNum1=randomNum1;
                    randomNum2=randomNum2-1;
                    
                    if(randomNum1==-1 || randomNum2==-1 || randomNum2==10 || randomNum1==10)
                    {
                        reward+=-5;
                        int max=9,min=0;
                        // nextInt is normally exclusive of the top value,
                        // so add 1 to make it inclusive
                        randomNum1 = rand.nextInt((max - min) + 1) + min;
                        randomNum2 = rand.nextInt((max - min) + 1) + min;
                        nextState=curState;
                    }
                    else{
                    if(Grid[randomNum1][randomNum2]=='C')
                    {
                     nextState +="2";   
                     reward+=10;
                    }
                    else
                    {
                      nextState +="1";  
                    }
                    if(randomNum1-1!=-1)
                    {if(Grid[randomNum1-1][randomNum2]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else{
                        nextState +="3";
                    }
                    if(randomNum1+1!=10)
                    {
                        if(Grid[randomNum1+1][randomNum2]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else
                    {
                        nextState +="3";
                    }
                    if(randomNum2-1!=-1)
                    {
                        if(Grid[randomNum1][randomNum2-1]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else
                    {
                        nextState +="3";
                    }
                    if(randomNum2+1!=10)
                    {
                        if(Grid[randomNum1][randomNum2+1]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else
                    {
                        nextState +="3";
                    }
                    
                    }
                }
                else if(action.equals("PC"))
                {
                 nextState=curState;
                 randomNum1=randomNum1;
                 randomNum2=randomNum2;        
                  if(randomNum1==-1 || randomNum2==-1 || randomNum2==10 || randomNum1==10)
                    {
                        reward+=-5;
                    }
                  if(Grid[randomNum1][randomNum2]!='C')
                      reward+=-1;
                }
                //System.out.println("Nxt:"+nextState);
                rewardstep=rewardstep+reward;
                //System.out.println("rew:"+reward);
                UpdateQVal(curState,nextState,index);
                curState=nextState;
                nextState="";
            }
            R[N]=rewardstep;
            if(y==49 && E>.1)
            {
                y=0;
                E=E-.01;
            }
            
            if(s==99)
            {
                s=0;
                System.out.println("Rewards:-"+R[N]);
            }
            
    }
        
        
    }
    
    public static void test()
    {
         R=new double[5000];
        Random rand=new Random();
        String curState="";        
        String nextState="";
        int y=0,s=0;
        for(int N=0;N<5000;N++)        {
            y=y+1;
            s=s+1;
            initGrid();
            nextState="";
            curState="";
            reward=0;
            for(int M=0;M<200;M++)            {     
                reward=0;
                if(M==0){
                if(Grid[randomNum1][randomNum2]=='C')
                    {
                     curState +="2";   
                    }
                    else
                    {
                      curState +="1";  
                    }
                    if(randomNum1-1!=-1)
                    {if(Grid[randomNum1-1][randomNum2]=='C')
                    {
                        curState +="2";
                    }else
                    {
                      curState +="1";  
                    }
                    }
                    else{
                        curState +="3";
                    }
                    if(randomNum1+1!=10)
                    {
                        if(Grid[randomNum1+1][randomNum2]=='C')
                    {
                        curState +="2";
                    }else
                    {
                      curState +="1";  
                    }
                    }
                    else
                    {
                        curState +="3";
                    }
                    if(randomNum2-1!=-1)
                    {
                        if(Grid[randomNum1][randomNum2-1]=='C')
                    {
                        curState +="2";
                    }else
                    {
                      curState +="1";  
                    }
                    }
                    else
                    {
                        curState +="3";
                    }
                    if(randomNum2+1!=10)
                    {
                        if(Grid[randomNum1][randomNum2+1]=='C')
                    {
                        curState +="2";
                    }else
                    {
                      curState +="1";  
                    }
                    }
                    else
                    {
                        curState +="3";
                    }
                    
                }
                int index=0;
                String action="";
                //System.out.println("Cur:"+curState);                
                if(E>(1-E)){
                index = rand.nextInt(Actions.length);
                action = Actions[index];
                }else{
                 if(randomNum1-1!=-1)
                    {if(Grid[randomNum1-1][randomNum2]=='C')
                    {
                        action="MN";
                    }
                    }
                 else if(randomNum1+1!=10)
                     {if(Grid[randomNum1+1][randomNum2]=='C')
                    {
                        action="MS";
                    }
                    }
                 else if(randomNum2-1!=-1)
                 {if(Grid[randomNum1][randomNum2-1]=='C')
                    {
                        action="MW";
                    }
                    }
                 else if(randomNum2+1!=10)
                 {if(Grid[randomNum1][randomNum2+1]=='C')
                    {
                        action="ME";
                    }
                    }
                 else{
                     index = rand.nextInt(Actions.length);
                action = Actions[index];
                 }
                }
                //System.out.println(action);
                if(action.equals("MN"))
                {
                    randomNum1=randomNum1-1;
                    randomNum2=randomNum2;                    
                    
                    //crashes wall
                    if(randomNum1==-1 || randomNum2==-1 || randomNum2==10 || randomNum1==10)
                    {
                        reward+=-5;
                        int max=9,min=0;

                        // nextInt is normally exclusive of the top value,
                        // so add 1 to make it inclusive
                        randomNum1 = rand.nextInt((max - min) + 1) + min;
                        randomNum2 = rand.nextInt((max - min) + 1) + min;
                        nextState=curState;
                    }
                    else{
                    if(Grid[randomNum1][randomNum2]=='C')
                    {
                     nextState +="2";   
                     reward+=10;
                    }
                    else
                    {
                      nextState +="1";  
                    }
                    if(randomNum1-1!=-1)
                    {if(Grid[randomNum1-1][randomNum2]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else{
                        nextState +="3";
                    }
                    if(randomNum1+1!=10)
                    {
                        if(Grid[randomNum1+1][randomNum2]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else
                    {
                        nextState +="3";
                    }
                    if(randomNum2-1!=-1)
                    {
                        if(Grid[randomNum1][randomNum2-1]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else
                    {
                        nextState +="3";
                    }
                    if(randomNum2+1!=10)
                    {
                        if(Grid[randomNum1][randomNum2+1]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else
                    {
                        nextState +="3";
                    }
                    
                    }
                }
                else if(action.equals("MS"))
                {
                    randomNum1=randomNum1+1;
                    randomNum2=randomNum2;                               
                    
                      if(randomNum1==-1 || randomNum2==-1 || randomNum2==10 || randomNum1==10)
                    {
                        reward+=-5;
                        int max=9,min=0;

                        // nextInt is normally exclusive of the top value,
                        // so add 1 to make it inclusive
                        randomNum1 = rand.nextInt((max - min) + 1) + min;
                        randomNum2 = rand.nextInt((max - min) + 1) + min;
                        nextState=curState;
                    }
                    else{
                    if(Grid[randomNum1][randomNum2]=='C')
                    {
                     nextState +="2";   
                     reward+=10;
                    }
                    else
                    {
                      nextState +="1";  
                    }
                    if(randomNum1-1!=-1)
                    {if(Grid[randomNum1-1][randomNum2]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else{
                        nextState +="3";
                    }
                    if(randomNum1+1!=10)
                    {
                        if(Grid[randomNum1+1][randomNum2]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else
                    {
                        nextState +="3";
                    }
                    if(randomNum2-1!=-1)
                    {
                        if(Grid[randomNum1][randomNum2-1]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else
                    {
                        nextState +="3";
                    }
                    if(randomNum2+1!=10)
                    {
                        if(Grid[randomNum1][randomNum2+1]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else
                    {
                        nextState +="3";
                    }
                    
                    }
                }
                else if(action.equals("ME"))
                {
                    randomNum1=randomNum1;
                    randomNum2=randomNum2+1;            
                      if(randomNum1==-1 || randomNum2==-1 || randomNum2==10 || randomNum1==10)
                    {
                        reward+=-5;
                        int max=9,min=0;

                        // nextInt is normally exclusive of the top value,
                        // so add 1 to make it inclusive
                        randomNum1 = rand.nextInt((max - min) + 1) + min;
                        randomNum2 = rand.nextInt((max - min) + 1) + min;
                        nextState=curState;
                    }
                    else{
                    if(Grid[randomNum1][randomNum2]=='C')
                    {
                     nextState +="2";   
                     reward+=10;
                    }
                    else
                    {
                      nextState +="1";  
                    }
                    if(randomNum1-1!=-1)
                    {if(Grid[randomNum1-1][randomNum2]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else{
                        nextState +="3";
                    }
                    if(randomNum1+1!=10)
                    {
                        if(Grid[randomNum1+1][randomNum2]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else
                    {
                        nextState +="3";
                    }
                    if(randomNum2-1!=-1)
                    {
                        if(Grid[randomNum1][randomNum2-1]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else
                    {
                        nextState +="3";
                    }
                    if(randomNum2+1!=10)
                    {
                        if(Grid[randomNum1][randomNum2+1]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else
                    {
                        nextState +="3";
                    }
                    
                    }
                }
                else if(action.equals("MW"))
                {
                    randomNum1=randomNum1;
                    randomNum2=randomNum2-1;
                    
                    if(randomNum1==-1 || randomNum2==-1 || randomNum2==10 || randomNum1==10)
                    {
                        reward+=-5;
                        int max=9,min=0;
                        // nextInt is normally exclusive of the top value,
                        // so add 1 to make it inclusive
                        randomNum1 = rand.nextInt((max - min) + 1) + min;
                        randomNum2 = rand.nextInt((max - min) + 1) + min;
                        nextState=curState;
                    }
                    else{
                    if(Grid[randomNum1][randomNum2]=='C')
                    {
                     nextState +="2";   
                     reward+=10;
                    }
                    else
                    {
                      nextState +="1";  
                    }
                    if(randomNum1-1!=-1)
                    {if(Grid[randomNum1-1][randomNum2]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else{
                        nextState +="3";
                    }
                    if(randomNum1+1!=10)
                    {
                        if(Grid[randomNum1+1][randomNum2]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else
                    {
                        nextState +="3";
                    }
                    if(randomNum2-1!=-1)
                    {
                        if(Grid[randomNum1][randomNum2-1]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else
                    {
                        nextState +="3";
                    }
                    if(randomNum2+1!=10)
                    {
                        if(Grid[randomNum1][randomNum2+1]=='C')
                    {
                        nextState +="2";
                    }else
                    {
                      nextState +="1";  
                    }
                    }
                    else
                    {
                        nextState +="3";
                    }
                    
                    }
                }
                else if(action.equals("PC"))
                {
                 nextState=curState;
                 randomNum1=randomNum1;
                 randomNum2=randomNum2;        
                  if(randomNum1==-1 || randomNum2==-1 || randomNum2==10 || randomNum1==10)
                    {
                        reward+=-5;
                    }
                  if(Grid[randomNum1][randomNum2]!='C')
                      reward+=-1;
                }
                //System.out.println("Nxt:"+nextState);
                rewardstep=rewardstep+reward;
                //System.out.println("rew:"+reward);
               // UpdateQVal(curState,nextState,index);
                curState=nextState;
                nextState="";
            }
            R[N]=rewardstep;
            if(y==49 && E>.1)
            {
                y=0;
                E=E-.01;
            }
            
            if(s==99)
            {
                s=0;
                //System.out.println("Rewards-:"+R[N]);
            }
            
    }
        System.out.println("Mean:"+rewardstep/5000);
        double dv = 0D;
        for (double d : R) {
            double dm = d - rewardstep/5000;
            dv += dm * dm;
        }
        double sd= Math.sqrt(dv / (R.length - 1));
        System.out.println("sd:"+sd);
    }
    
    public static void UpdateQVal(String curState,String nextState,int index)
    {
        int curindex=0,nextindex=0;
        
        //System.out.println("C"+curState+"N   "+nextState+" I"+index);
        for (int i = 0 ; i < 243; i++)
        {
         if (pools[i].equals(curState))
         {
             curindex=i;
         }
         if(pools[i].equals(nextState))
         {
             nextindex=i;
         }
        }
        //System.out.println(curindex+" "+nextindex);
        
        Q[curindex][index]=Q[curindex][index]+ lr*(reward + gamma*(max(nextindex))-Q[curindex][index]);;
    }
    
    public static double max(int index)
    {
        double maxValue = Double.MIN_VALUE;
        for (int i=0;i<6;i++) {
            if(i>=1)
            {double value = Q[index][i];

            if (value > maxValue)
                maxValue = value;
        }}
        return maxValue;
        //return max;
    }
        
}
