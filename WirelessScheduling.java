import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

//10^4 time slots
//4 users

//measure fraction of time-slots for each user
// (time-slots for user) / (total time-slots)
//measure  throughput for each user
// (sum of data rates in each time-slot for user) / (total time-slots)
//measure total throughput
// (sum of all throughputs)

//symmetric ( 1:(D/3,0), 2:(0,D/3), 3:(-D/3,0), 4:(0,-D/3) )
//asymmetric ( 1:(D/5,0), 2:(0,D/4), 3:(-D/3,0), 4:(0,-D/2) )
public class wirelessScheduling {
  public static int users = 4;
  public static int W = 1;
  public static int PowerT = 10;
  public static int alpha = 4;
  public static int stddev = 8;
  public static int mean = 0;
  public static int D = 1000;
  public static double timeslots = 10000.0;
  public static double[] symmetricUserDistances = {D/3,D/3,D/3,D/3};
  public static double[] asymmetricUserDistances = {D/5,D/4,D/3,D/2};
  public static double[] userDistances = symmetricUserDistances;
  public static int[] timeSlotsScheduledPerUser = new int[users];
  public static double[] throughputPerUser = new double[users];

  public static void problemOne() {
    Random rng = new Random();
    for (double i=0;i<timeslots;i++) {
      int currUser = ((int) i)%users;
      timeSlotsScheduledPerUser[currUser]++;
      
      //Snij
      double stdNormal = rng.nextGaussian();
      double normalValue = stddev*stdNormal + mean;
      double Sn = Math.exp(normalValue);

      //Path gain
      double Gn = Sn / Math.pow(userDistances[currUser], alpha);

      //SINR
      double Yn = (Gn * PowerT);

      //Packet Transfer rate
      double Rn = W * (Math.log(1 + Yn)/Math.log(2));
      
      throughputPerUser[currUser]+=Rn;
    }
  }

  public static void problemTwo() {
    Random rng = new Random();
    for (double i=0;i<timeslots;i++) {
      double maxRn = 0;
      int chosenUser = 0;

      for (int j=0;j<users;j++) {
        //Snij
        double stdNormal = rng.nextGaussian();
        double normalValue = stddev*stdNormal + mean;
        double Sn = Math.exp(normalValue);

        //Path gain
        double Gn = Sn / Math.pow(userDistances[j], alpha);

        //SINR
        double Yn = (Gn * PowerT);

        //Packet Transfer rate
        double Rn = W * (Math.log(1 + Yn)/Math.log(2));

        if (Rn > maxRn) {
          maxRn = Rn;
          chosenUser = j;
        }
      }
      int currUser = chosenUser;
      throughputPerUser[currUser]+=maxRn;
      timeSlotsScheduledPerUser[currUser]++;
    }
  }

  public static void problemThree() {
    double minTimeslotsPerUser = .25;
    int maxTimeslotsPerUser = (int) ((1 - (users-1)*minTimeslotsPerUser) * timeslots);
    System.out.println(maxTimeslotsPerUser);
    Random rng = new Random();
    for (double i=0;i<timeslots;i++) {
      double maxRn = 0;
      int chosenUser = 0;

      for (int j=0;j<users;j++) {
        //Snij
        double stdNormal = rng.nextGaussian();
        double normalValue = stddev*stdNormal + mean;
        double Sn = Math.exp(normalValue);

        //Path gain
        double Gn = Sn / Math.pow(userDistances[j], alpha);

        //SINR
        double Yn = (Gn * PowerT);

        //Packet Transfer rate
        double Rn = W * (Math.log(1 + Yn)/Math.log(2));

        if (Rn > maxRn && timeSlotsScheduledPerUser[j]<maxTimeslotsPerUser) {
          maxRn = Rn;
          chosenUser = j;
        }
      }
      int currUser = chosenUser;
      throughputPerUser[currUser]+=maxRn;
      timeSlotsScheduledPerUser[currUser]++;
    }
  }

  public static void main(String[] args) {
    for (String arg : args) {
      if (arg.equals("-a")){
        userDistances = asymmetricUserDistances;
      } else if (arg.equals("2")){
        problemTwo();
      } else if (arg.equals("3")) {
        problemThree();
    } else {
        problemOne();
      }
    }

    System.out.println("\nTimeslots per User");
    for (int i=0;i<users;i++) {
      System.out.println("User " + (i+1) + ": " + timeSlotsScheduledPerUser[i]/timeslots + " fraction of timeslots");
    }

    System.out.println("\nThroughput per User");
    double totalThroughput = 0;
    for (int i=0;i<users;i++) {
      System.out.println("User " + (i+1) + ": " + throughputPerUser[i]/timeslots + " Throughput per timeslot");
      totalThroughput+=throughputPerUser[i];
    }

    System.out.println("Total Throughput: " + totalThroughput + "\n");
  }
}
