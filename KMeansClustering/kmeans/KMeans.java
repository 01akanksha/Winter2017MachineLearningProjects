/* To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package kmeans;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Math.log;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Random;
/**
 *
 * @author A
 */
class KMeans {

    static int k = 30;
    static int numFeat = 64;
    static Cluster[] clusters = new Cluster[k];
    static HashMap<Integer,double[]> featureTrain = new HashMap();    // to store the training features
    static HashMap<Integer, Integer> labelsTrain = new HashMap();     // store labels corresponding to training data
    static HashMap <Integer, double[]> featureTest = new HashMap();   // to store test features
    static HashMap <Integer, Integer> labelsTest = new HashMap();     // to store test labels
    static int totalRun =5;
    static String index ="k_"+k;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException{
        // TODO code application logic here
        BufferedReader trainData = new BufferedReader(new FileReader("optdigits.train"));
        double[] mse;
        Cluster[][] clusters1;
        try (BufferedWriter outFile = new BufferedWriter (new FileWriter("/u/akanksha/kmeans/"+index+"/out.txt"))) {
            String line;
            int lineNum =0;
            // parse and store the training features and labels
            while ((line = trainData.readLine())!= null){
                String features[] = line.split(",");
                double feat[]= new double[features.length-1];
                for (int i=0; i<features.length-1;i++){
                    feat[i] =Double.parseDouble(features[i]);
                }
                int label = Integer.parseInt(features[features.length-1]);
                featureTrain.put(lineNum,feat);
                labelsTrain.put(lineNum, label);
                lineNum++;                
            }  
            mse = new double[totalRun];                 // to compare the different runs and find the one with the minimum sum squared error
            clusters1 = new Cluster[totalRun][k];       // stores all the clusters of the all the runs 
            Random ran = new Random();
            int seed;
            for (int run =0; run<totalRun; run++){
                seed = ran.nextInt(16);                              // get a random seed within 0 to 16
                String str = "run = " +run +"seed = "+ seed+"\n";
                outFile.append(str);
                clusters1[run] = KMeanClustering(seed);      // call the function to perform clustering and return the clusters
            }
        }
        for (int run =0; run<totalRun; run++){
            mse[run]= getmse1(clusters1[run]);                      // calculate the sum squared earror for clustering for each of the runs 
        }
        int bestRun = findMaxmse1(mse);
        System.out.println("best run = "+ bestRun);       
        clusters = copyClusters(clusters1[bestRun]);            // copy into the global variable clusters, the set of clusters that gave the smallest mse
        try (BufferedWriter clustFile = new BufferedWriter(new FileWriter("/u/akanksha/kmeans/"+index+"/clustFile.txt"))) {
            for (int i=0; i<k; i++){
                clusters[i].lab = getClasmset(i);
                clusters[i].label = getMax(clusters[i].lab);        // set labels of each cluster
                clusters[i].mse = getmseClust(i);                   // set the mse of each cluster
            }
            printClusters(clustFile);
        }
        testData();
        getConfMat();
        for(int i=0; i<k;i++)
        printClustImage(i);
    }
     // return the index of maximum value of array
    private static int findMaxmse1(double [] mse) {
        int maxmse =0;
        for (int i=0; i<mse.length; i++){
            if (mse[maxmse]<mse[i])
                maxmse =i;
        }
        return maxmse;
    }
    
    // calculate the total mse of the clustering
    private static double getmse1(Cluster[] clusters) {
        double sum =0;
        for (int i =0; i<k; i++){
            sum += clusters[i].mse;
        }
        return sum;        
    }
    
     static Cluster[] KMeanClustering(int seed) throws IOException{
        for (int i=0; i<k; i++){
            clusters[i] = new Cluster(seed);
        }
        int itr =0;
        Cluster[] prevClusters, prev2Clusters, prev3Clusters ;        // to keep track of oscillations
        prevClusters = copyClusters(clusters);
        prev2Clusters = copyClusters(clusters);
        do{
        prev3Clusters = copyClusters(prev2Clusters);
        prev2Clusters = copyClusters(prevClusters);
        prevClusters = copyClusters(clusters);
        clusters = getNewcluster();  // call the method to run one iteration of k-means clustering algorithm and find teh resulting clusters
        itr++;
        if (itr>30)
            break;
        }while(getCenterChange(prevClusters)&& getCenterChange(prev2Clusters)&& getCenterChange(prev3Clusters)); 
        
        Cluster[] clusters1 = new Cluster[k];
        for (int i=0; i<k; i++){
            clusters1[i]=Cluster.copyCluster(clusters[i]);
        }
        return clusters1;
    }

    private static Cluster[] getNewcluster() {
        getNewClustering();                          // method to assign datapoints to the clusters
        for (int i=0; i<k; i++){
            while(clusters[i].datapoints.isEmpty()){
                randomize(i);                                   // to avoid clusters having no data
                getNewClustering();                             
            }
        }
        //calculate the centroid of datapoints in a cluster and change the cluster center
        for (int i=0; i<k; i++){
            int points = clusters[i].datapoints.size();
            int sum[]  = new int[numFeat];
            if (points!=0){
                for (int j=0; j<points; j++){
                    for (int l=0; l<numFeat; l++){
                        sum[l] += featureTrain.get(clusters[i].datapoints.get(j))[l];
                    }
                }
                for (int l =0; l<numFeat; l++){
                    clusters[i].center[l]= (double)sum[l]/points;
                }
            }
        }
        return clusters;
    }
    // This method calculates teh distance of a datapoint from each of the k clusters and assigns the datapoint to its nearest cluster
    private static void AssignCluster(int data) {
        double distance[] = new double[k];
        for (int i=0; i<k; i++){
            for (int j=0; j<numFeat; j++){
                distance[i] += Math.pow((clusters[i].center[j] -featureTrain.get(data)[j]),2);
            }
        }
        int clust =findMin(distance);
        clusters[clust].datapoints.add(data);
    }

    private static int findMin(double[] distance) {
        int min = 0;
        for (int i=0; i<distance.length; i++){
            if (distance[i]<distance[min])
                min =i;
        }
        return min;
    }
    // returns teh mean-squared separation
    private static double getMSS() {
        double sum=0;
        for (int i=0; i<k-1; i++){
            for(int j=i+1; j<k; j++)
            sum += getDistanceSquare(clusters[i].center, clusters[j].center);
        }
        sum=sum/(((k-1)*k)/2);
        return sum;
    }
    // returns the square of Euclidean distance between two points    
    private static double getDistanceSquare(double[] feat, double[] center){
        double distancesqr=0;
        for (int i=0; i<center.length; i++){
            distancesqr += Math.pow(((double)feat[i]-center[i]),2);
        }
        return distancesqr;
    }    
    // return an array, whose each element is the number of datapoints in the cluster that belongs to the class corresponding to the index
    private static int[] getClasmset(int clust) {
       int[] labl = new int[10];
        for (int j=0; j<clusters[clust].datapoints.size(); j++){
            int point = labelsTrain.get(clusters[clust].datapoints.get(j));
            labl[point]++;
        }
        return labl;
    }
    
    private static void printClusters(BufferedWriter out) {
        Formatter fmt = new Formatter(out);
        int s=0;
        for (int i=0; i<k; i++){
            for (int j=0; j<numFeat; j++){
                s++;
                fmt.format("%20s ",clusters[i].center[j]);
                if(s==8)
                {
                    fmt.format("\n");
                    s=0;                    
                }
            }
            fmt.format("\n");
        }
        double mse = getmse1(clusters);
        double mss = getMSS();
        //double entropy = getEntropy();
        fmt.format ("amse = %f MSS = %f\n",mse,mss);
    }
    // assign a random datapoint to a cluster's center.
    private static void randomize(int i) {
        Random ran = new Random();
        int data = ran.nextInt(featureTrain.size());
        for (int j=0; j<numFeat; j++){
            clusters[i].center[j]= featureTrain.get(data)[j];
        }
    }

    // get the Mean Squared Error of the ith cluster
    private static double getmseClust(int i) {
        double sum =0;
        for (int j=0; j<clusters[i].datapoints.size();j++){
                sum += getDistanceSquare(featureTrain.get(clusters[i].datapoints.get(j)),clusters[i].center);
            }
        sum=sum/clusters[i].datapoints.size();
        return sum/k;
    }
    // return a copy of the clusters;
    private static Cluster[] copyClusters(Cluster[] clusters1) {
        Cluster prevClusters[] = new Cluster[k];
        for (int i=0; i<k; i++)
            prevClusters[i] = Cluster.copyCluster(clusters1[i]);
        return prevClusters;
    }


    private static void getNewClustering() {
        for (int i=0; i<k; i++){
            clusters[i].datapoints = new ArrayList<>();
        }
        for (int i=0; i<featureTrain.size(); i++){
            AssignCluster(i);
        }
        for (int i=0; i<k; i++){
            clusters[i].mse = getmseClust(i);
        }
    }
    // return true if the value of center of any cluster has changed.
    private static boolean getCenterChange(Cluster[] prevClusters) {
        for (int i=0; i<k; i++){
            for (int j=0; j<numFeat; j++){
                if (clusters[i].center[j] != prevClusters[i].center[j])
                    return true;
            }
        }
        return false;
    }

    // This method reads and stores test data (features and labels) in the featureTest and labelsTest HashMaps
    private static void readTestData() throws FileNotFoundException, IOException {
         BufferedReader testFile = new BufferedReader (new FileReader("optdigits.test"));
        String line;
        int lineNum =0;
        while ((line = testFile.readLine())!= null){
            String features[] = line.split(",");
            double feat[]= new double[features.length-1];
            for (int i=0; i<features.length-1;i++){
                feat[i] =Double.parseDouble(features[i]);
            }
            int label = Integer.parseInt(features[features.length-1]);
            featureTest.put(lineNum,feat);
            labelsTest.put(lineNum, label);
            lineNum++;
            
        }
    }
    // This method assigns a label to each of the test data, based on the label of its nearest cluster and prints the actual and predicted label to the output file
    private static void testData() throws FileNotFoundException, IOException {
        readTestData();
        BufferedWriter outFile = new BufferedWriter (new FileWriter ("/u/akanksha/kmeans/"+index+"/testOut.txt"));
        Formatter fmt = new Formatter(outFile);
        outFile.write("Actual Predicted\n");
        for (int i=0; i<featureTest.size(); i++){
            int lab =testFeat(i);
            outFile.append(labelsTest.get(i)+"        "+lab+"\n");
        }
        outFile.close();
    }

    // find the label of the cluster nearest to the datapoint
    private static int testFeat(int data) {
        double distance[] = new double[k];
        double[] feat = featureTest.get(data);
        for (int i=0; i<k; i++){
            for (int j=0; j<numFeat; j++){
                  distance[i] += Math.pow((clusters[i].center[j] -feat[j]),2);
            }
        }
        int clust = findMin(distance);
        return clusters[clust].label;
    }

    private static int getMax(int[] lab) {
        int max =0;
        for (int i=0; i<lab.length; i++){
            if (lab[i]>lab[max])
                max = i;
        }
        return max;
    }

    private static void getConfMat() throws FileNotFoundException, IOException {
        BufferedReader resultFile = new BufferedReader (new FileReader("/u/akanksha/kmeans/"+index+"/testOut.txt"));
        BufferedWriter confMatFile = new BufferedWriter(new FileWriter("/u/akanksha/kmeans/"+index+"/confMat.txt"));
        int confMat[][] = new int[10][10];
        int i=0,j=0;
        String line;
        int lineNum =0;
            resultFile.readLine();                               // ignore the title line
            while((line = resultFile.readLine())!=null){         // while there is data to read in out.txt
                String[] str = line.split("\\s+");               // split the characters based on whitespaces
                i = Integer.parseInt(str[0]) ;       // get the index of actual digit
                j = Integer.parseInt(str[1]);       // get the index of predicted digit
                confMat[i][j]= confMat[i][j]+1;     // increment the value of the cell corresponding to actual digit with index i and predicted digit with index j
                lineNum++;
            }
            Formatter fmt = new Formatter(confMatFile);                                      // to print a formatted array                        
            fmt.format("%s", " ");                                    
            // print out the entire confusion matrix
            for (i=0; i<10; i++){
                fmt.format("%5d", i);
            }
            for (i=0; i<10; i++){
                fmt.format("\n" +(i));
                for (j=0; j<10; j++){
                    fmt.format("%5d",confMat[i][j]);
                }
            }
            int accuracySum =0;
            for (i=0; i<10; i++){
                accuracySum += confMat[i][i];             
            }
            double accuracy = (double)accuracySum/lineNum;
            confMatFile.append("\naccuracy =" +accuracy);
                confMatFile.close();
    }
    // prints the pgm image of each cluster
    private static void printClustImage(int clust) throws IOException {
        BufferedWriter imageOut = new BufferedWriter(new FileWriter("/u/akanksha/kmeans/"+index+"/outImage_clust_"+clust+".pgm"));
        //Formatter fmt = new Formatter(imageOut);                                      // to print a formatted array                        
        imageOut.write("P2\n8 8\n16\n");
        imageOut.write("# label = "+clusters[clust].label+"\n");
        int ind =0,s=0;
        for (ind =0; ind<numFeat; ind++)
        {
        //fmt.format("%20f",clusters[clust].center[ind]);
        imageOut.write(clusters[clust].center[ind]+"\t");
              }
       
        imageOut.close();
    }
}

