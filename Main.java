import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import java.io.File;
import java.io.FileWriter;
import java.io.ObjectOutputStream;


/*
TO DO:
    Create names (DONE)
      Import names of top 200 first and last names, assign each player a random name based on those two names
      Method that returns array of names

    Draft (DONE)
      10 players (2 per position) with "potential" ratings that are in a range of their actual overall
      Quality of players dependent on wins
    Saving (ABANDONED, this is stupid i dont need this for this game)
    Settings
    Actual team customization
    Balance the game
*/
class Main {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    //sc.nextLine();
    //Starts game whenever user is ready (when they type "play")
    
    String newGame = "";
    System.out.println("Welcome to Babin Bourbon's Basketball! Type \"play\" to play.");
    while (!(newGame.equals("play"))) {
      newGame = sc.next();
    }
    //Creates initial roster with 5 players
  Team team1 = new Team("Old McDonald's Farmers");
  int min = 25;
  int max = 60;
  for(int i = 0; i < 5; i++) {
    team1.addPlayer(new Player("random", min, max, min, max, 19, 30), i + 1);
  }
    //Display the starting roster
    //team1.printStats();

    //Adds 3 to team1's pg's offense rating
    //updateStats(team1.pg,"offense", 3);
    //Runs the initial season; core gameplay loop
    runSeason(team1);
    sc.close();
  }



  public static String simGame(Team team1, Team team2) {
    // Simulates a basketball game, determining a winner, scores, and performances
    int time = 200;
    int turn = 0;
    int t1Score = 0;
    int t2Score = 0;
    int[] points = new int[12]; //Array to add to points total later
    int[] pointsAgainst = new int[5];
    int actionPos;
    Player scorer;
    Player defender;
    Random rand = new Random();
    //Loop until there are no more turns
    while(time > 0) {
      //Choose a random position to attempt to score
      actionPos = rand.nextInt(5);
      //Checks which side is on offense and defense and assigns those sides
      if(turn == 0) {
        scorer = team1.getPlayer(actionPos + 1);
        defender = team2.getPlayer(actionPos + 1);
      } else {
        scorer = team2.getPlayer(actionPos + 1);
        defender = team1.getPlayer(actionPos + 1);
      }
      //Calculates the chance of scoring and rounds up/down if the values are too extreme
      // (float)(Math.sqrt(2 * scorer.offense - defender.defense) / 100) + .46;
      // -1 * (float)(Math.sqrt(2 * defender.defense - scorer.offense) / 100) + .46;
      // (float)Math.sqrt(2 * scorer.offense - (((2 * defender.defense)+(3 * (defender.team.teamD / 5))) / 5.00)) / 100 + .46;
      //
      double scoreChance = 0;
      double oRating = (.6 * scorer.offense) + (0.08 * scorer.team.teamO);
      double dRating = (.6 * defender.defense) + (0.08 * defender.team.teamD);
      if(scorer.offense > defender.defense) {
        scoreChance = 1.5 * (float)Math.sqrt(2 * oRating - dRating) / 100 + .46;
        //scoreChance = (float)(Math.sqrt(2 * scorer.offense - defender.defense) / 100) + .46;
      } else {
        scoreChance = -1.5 * ((float)Math.sqrt(2 * dRating  - oRating) / 100) + .46;
      }
      if (scoreChance <= .01) { scoreChance = .01;}
      if (scoreChance >= 1) {scoreChance = 0.9;}
      //Generates a random number between 0 and 1. If below the odds, the offense scores and points are applied to proper team
      if (Math.random() <= scoreChance) {
        if (turn == 0){
          t1Score += 2;
          points[actionPos] += 2;
        } else {
          t2Score += 2;
          points[actionPos + 7] += 2;
        }
      }
      //Updates who has possession next and how much time is left
      //Possible feature: player pace that drains time differently depending on how fast the scoring player plays
      turn = Math.abs(turn-1);
      time--;
      if(time == 0 && t1Score == t2Score ) {
        time = 10;
      }
    }
    //Game is now over, points are added to the players' totals and outcome is recorded
    points[5] = t1Score;
    points[6] = t2Score;
    team1.addPoints(points);
    if(t1Score > t2Score) {
      return "W, " + t1Score + "-" + t2Score;
    }
    return "L, " + t1Score + "-" + t2Score;
  }
  


  public static void runSeason(Team team1)  {
    int[] difficulty = {35,75};
    int teams = 29;
    Random rand = new Random();
    //Simulate a season of x games (41 as of writing this)
    Scanner sc = new Scanner(System.in);
    //Resets values for the season
    //Player chooses to play a season or stop

    Player[] freeAgents = new Player[5];
    int length = 82;
    int wins = 0;
    int gp = 0;
    char gInput = 0;
    boolean hireable = true;
    Team[] league = new Team[teams];
    Team[] playoffTeams = new Team[teams / 2];
    int pTeams = 0;

    String ssnInput = "";
    while(!(ssnInput.equals("y")) && !(ssnInput.equals("n"))) {
      System.out.println("Play a season? y/n");
      ssnInput = sc.next();
      if(ssnInput.equals("n")) {
        ssnInput = "";
        while(!(ssnInput.equals("y")) && !(ssnInput.equals("n"))) {
          System.out.println("Are you sure you want to quit? y/n");
          ssnInput = sc.next();
          if(ssnInput.equals("y")) {
            System.out.println("Exiting...");
            sc.close();
            return;
            }
          }
        }
      }
    
    team1.printStats();
    leagueDraft(team1, difficulty);
    team1.updateTeam();
    team1.clearPoints();
    for(int i = 0; i < freeAgents.length; i++) {
      freeAgents[i] = new Player("random", difficulty[0], (int)(difficulty[1]*0.8), difficulty[0], (int)(difficulty[1]*0.8), 20, 33);
    }

    for(int i = 0; i < league.length; i++) {
      league[i] = new Team(Integer.toString(i), difficulty[0], difficulty[1], difficulty[0], difficulty[1], 1, 2);
    }
    Collections.sort(Arrays.asList(league), Collections.reverseOrder());
    for(int i = 0; i < playoffTeams.length; i++) {
      playoffTeams[i] = league[i];
    }
    //Simulates x amount of games and records everytime the player wins
    while(gp < length) {
      if(gp > length / 2) {
        hireable = false;
      }
      String helpText = "What would you like to do? \n 1. Simulate games\n 2. View Stats";
      if(hireable == true) {
        helpText += " \n 3. Recruit a free agent";
      }
      System.out.println(helpText);
      try {
        gInput = sc.next().charAt(0);
      } finally {}
      if (gInput == '1') {
        gInput = 0;
        int j = 0;
        while(j < 1 || j > (length - gp)) {
          System.out.println("How many games would you like to simulate? (1-" + (length - gp) + " games)");
          try {
            j = sc.nextInt();
          } catch (Exception e) {j = 1;
          }
        }
        System.out.println("Simulating " + j + " games...\n");
        for(int i = 0; i < j; i++) {
          Team team2 = league[rand.nextInt(teams)];
          String game = simGame(team1, team2);
          gp++;
          //System.out.println(game);
          if (game.charAt(0) == 'W') {
            wins++;
          }
        } 
       System.out.println("Your Record: " + wins + "-" + (gp-wins) + "\n"); 
      } else if (gInput == '2') {
        team1.printStats();
      } else if (gInput == '3' && hireable == true) {
        System.out.println("Select a player to be added to the corresponding position (1=PG, 2=SG, 3=SF, 4=PF, 5=C)");
        for(int i = 0; i < freeAgents.length; i++) {
          System.out.print((i + 1) + ".");
          freeAgents[i].displayStats();
        }
        System.out.println("6. Cancel\n");
        gInput = 0;
        int j = 0;
        while(j > 6 || j < 1) {
          gInput = sc.next().charAt(0);
          j = gInput - 48;
        }
        if(j != 6) {
          hireable = false;
          team1.addPlayer(freeAgents[j - 1], j);
          System.out.println(freeAgents[j - 1].name + " has been added to your team!");
        }
      }
    }

    //Season is over, shows the team's record
    System.out.println("Season over. " + team1.name + ": " + wins + "-" + (length - wins));

    //Checks if player reached win threshhold for playoffs, then simulates playoff series 
    if (wins >= (length / 2)){
      int win = 0;
      int round = 4;
      boolean lost = false;
      System.out.println("Playoffs!\nFirst round: ");
      /*while(round < 4 && lost == false) {
        pDiff[0] = (int)(difficulty[0] * (1 + (i / 10.00)));
        pDiff[1] = (int)(difficulty[1] * 1.1);
      }*/
      String result = playoffRound(team1, playoffTeams, 1);
      if (result.charAt(0) == '4') {
        System.out.println("Victory! Second round: ");
        result = playoffRound(team1, playoffTeams, 2);
        if (result.charAt(0) == '4') {
          System.out.println("Victory! Conference Finals: ");
          result = playoffRound(team1, playoffTeams, 3);
          if (result.charAt(0) == '4') {
            System.out.println("Victory! Grand Finals: ");
            result = playoffRound(team1, playoffTeams, 4);
            if (result.charAt(0) == '4') {
              System.out.println("You win! Way to go!\n");
              win = 1;
            }
          }
        }
      }
      if (win == 0) {
        System.out.println("You Lost. Better luck next time!\n");
      }
    } else{
        System.out.println("You didn't make playoffs. Unlucky, really.\n");
      }

    //Shows end of season stats, resets them, and attempts to run a new season
    runSeason(team1);
    sc.close();
  }



  public static String playoffRound(Team team1, Team[] playoffTeams, int round) {
    //Simulates a 7-game playoff series
    int pwins = 0;
    int ploss = 0;
    int teamPool = playoffTeams.length / round;
    if(teamPool < 1) {
      teamPool = 1;
    }
    Random rand = new Random();
    //Generates 1 team for the entire series
    
    Team team2 = playoffTeams[rand.nextInt(teamPool)];
    System.out.println("Your opponent: " + (team2.teamTotal / 10.0) + " Overall");
    //Simulates games until one side reaches 4 wins
    while(pwins < 4 && ploss < 4) {
      String game = simGame(team1, team2);
      System.out.println(game);
      if (game.charAt(0) == 'W') {
         pwins++;
        }
      else {
        ploss++;
      }
    }
    //Returns the record of the series
    return (pwins + "-" + ploss);
  }
    
  public static void leagueDraft(Team team1, int[]difficulty) {
    Random rand = new Random();
    int quality = (team1.gp - team1.wins) / 10;
    if(quality == 0) {
      quality = 4;
    }
    int input = 0;
    int[] playerRange = {
      difficulty[0] - (2 * (9 - quality)),
      difficulty[1] - (5 * (9 - quality))
    };
    Player[] rookies = new Player[5];
    System.out.println("Welcome to the draft. Select a player to be added to your team. Be careful, you can only draft one player!");
    
    for(int i = 0; i < 5; i++) {
      rookies[i] = new Player("random", playerRange[0], playerRange[1], playerRange[0], playerRange[1], 19, 23);
      int potential = ((rookies[i].offense + rookies[i].defense) / 2) + rand.nextInt(10) - 5;
      System.out.println((i+1) + ". " + team1.getPos(i + 1) + ": " + rookies[i].name + ", Age " + rookies[i].age + ", " + potential + " potential");
    }
    System.out.println("6. ");
    Scanner sc = new Scanner(System.in);
    while(input > 6 || input < 1) {
          char gInput = sc.next().charAt(0);
          input = gInput - 48;
    }
    if(input != 6) {
      rookies[input - 1].rookie = true;
      team1.addPlayer(rookies[input - 1], input);
    } else {
      System.out.println("You did not draft anybody.");
    }
  }
}

