import java.util.Random;
import java.util.*;
import java.io.Serializable;

public class Team implements Comparable<Team>, Serializable{
  //Each position info. Teams are initialized with 5 players
 Player[] players = new Player[5];
 String name;
 int pointsAgainst, pointsFor, gp, teamD, teamO, teamTotal, wins;
  
  private static final long serialVersionUID = 1L;

  public Team(String name, int oLB, int oHB, int dLB, int dHB, int aLB, int aHB) {
    for(int i = 0; i < this.players.length; i++) {
      players[i] = new Player(name, oLB, oHB, dLB, dHB, aLB, aHB);
      players[i].team = this;
    }
    this.name = name;
    this.pointsAgainst = this.gp = this.pointsFor = this.wins = 0;
    this.dSum();
  }



  public Player getPlayer(int position) {
    //Converts a position int to a player
    if(position == 1) {
      return players[0];
    } else if (position == 2) {
      return players[1];
    } else if (position == 3) {
      return players[2];
    } else if (position == 4) {
      return players[3];
    }
    return players[4];
  }



  public Team(String name) {
    //Auto fill a team of Jims
    this.name = name;
    for(int i = 0; i < this.players.length; i++) {
      players[i] = new Player();
      players[i].team = this;
    }
    this.dSum();
  }

   public void printStats() {
     //Prints the players' names, ratings, and points per game
    System.out.println(this.name + "' stats:\n");

    String[] header = {"Pos", "Name", "PPG", "PA", "OFF", "DEF", "Age"};
    String[][] table = new String[6][header.length];
    int[] maxLengths = new int[header.length];
    table[0] = header;
    for(int j = 0; j < table[0].length; j++) {
          int size = table[0][j].length();
          maxLengths[j] = size;
        }
    for(int i = 0; i < 5; i++) {
      table[i + 1] = this.players[i].getStats();
      for(int j = 0; j < table[0].length; j++) {
        int size = table[i + 1][j].length();
        if(size > maxLengths[j]) {
          maxLengths[j] = size;
        }
      }
    }
    String format = "";
    for(int k = 0; k < table[0].length; k++) {
      format += "%-" + maxLengths[k] + "s|";
    }
    for(int i = 0; i < table.length; i++) {
      System.out.format(format, table[i]);
      System.out.println();
    }
    System.out.println("Offense: " + this.teamO / 5 + " overall, Defense: " + this.teamD / 5 + " overall");
  }


  public void addPoints(int[] scorecard) {
    //Adds the points from an array to the season total after every game (also iterates 1 game)
    for(int i = 0; i < this.players.length; i++) {
      players[i].points += scorecard[i];
      players[i].pointsAllowed += scorecard[i + 7];
      players[i].gp++;
    }
    this.pointsFor += scorecard[this.players.length];
    this.pointsAgainst += scorecard[this.players.length + 1];
    if(scorecard[5] > scorecard[6]) {
      this.wins++;
    }
    this.gp++;
  }


  
  public void clearPoints() {
    //Reset the point totals and games played for a new season
    for(int i = 0; i < players.length; i++) {
      players[i].points = 0;
      players[i].gp = 0;
      players[i].pointsAllowed = 0;
    }
    this.pointsFor = 0;
    this.pointsAgainst = 0;
    this.gp = 0;
    this.wins = 0;
  }



  public void updateTeam() {
    if(this.name.charAt(this.name.length() - 1) == 's'){
      System.out.println(this.name + "' changes:\n");
    } else {
      System.out.println(this.name + "'s changes:\n");
    }
    if(this.gp != 0) {
      System.out.println("Points For: " + Math.round(this.pointsFor / this.gp * 10) / 10.0 + " ppg, Points against: " + Math.round(this.pointsAgainst/this.gp * 10) / 10.0 + " ppg");
    }
    String[] header = {"Pos", "Name", "PPG", "PA", "OFF", "DEF", "Age"};
    String[][] table = new String[6][header.length];
    int[] maxLengths = new int[header.length];
    table[0] = header;
    for(int j = 0; j < table[0].length; j++) {
          int size = table[0][j].length();
          maxLengths[j] = size;
        }
    for(int i = 0; i < 5; i++) {
      table[i + 1] = this.players[i].updateStat();
      for(int j = 0; j < table[0].length; j++) {
        int size = table[i + 1][j].length();
        if(size > maxLengths[j]) {
          maxLengths[j] = size;
        }
      }
    }
    String format = "";
    for(int k = 0; k < table[0].length; k++) {
      format += "%-" + maxLengths[k] + "s|";
    }
    for(int i = 0; i < table.length; i++) {
      System.out.format(format, table[i]);
      System.out.println();
    }
    this.dSum();
    System.out.println("Offense: " + this.teamO / 5 + " overall, Defense: " + this.teamD / 5 + " overall");
  }

  public void dSum() {
    this.teamD = players[0].defense + players[1].defense + players[2].defense + players[3].defense + players[4].defense;
    this.teamO = players[0].offense + players[1].offense + players[2].offense + players[3].offense + players[4].offense;
    this.teamTotal = this.teamO + this.teamD;
  }

  public void addPlayer(Player player, int position) {
    player.position = position;
    players[position - 1] = player;
    players[position - 1].team = this;
    this.dSum();
  }

  public String getPos(int pos) {
    if(pos == 1) {
      return "PG";
    } else if (pos == 2) {
      return "SG";
    } else if (pos == 3) {
      return "SF";
    } else if (pos == 4) {
      return "PF";
    } else {
      return "C";
    }
  }


  public int compareTo(Team t) {
    return this.teamTotal - t.teamTotal;
  }
}