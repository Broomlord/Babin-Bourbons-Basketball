import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.util.Scanner;
import java.lang.reflect.Method;
import java.io.Serializable;

public class Player implements Serializable{
  //All player stats (more to implemented later)
  String name;
  int offense,defense,points,age, gp, position, pointsAllowed;
  Random rand = new Random();
  Team team;
  Boolean rookie;

  private static final long serialVersionUID = 2L;

  public Player(String name, int oLB, int oHB, int dLB, int dHB, int aLB, int aHB) {
    if(name.equals("random")) {
      try {
        this.name = this.getName();
      }
      catch(Exception e) {
        this.name = "ERROR";
      }
    } else {
      this.name = name;
    }
    this.offense = rand.nextInt(oHB-oLB) + oLB;
    this.defense = rand.nextInt(dHB-dLB) + dLB;
    this.age = rand.nextInt(aHB-aLB) + aLB;
    this.points = 0;
    this.gp = 0;
    this.rookie = false;
  }

  public Player() {
    try {
      this.name = this.getName();
    } catch(Exception e) {
      this.name = "ERROR";
    }
    this.offense = rand.nextInt(45) + 30;
    this.defense = rand.nextInt(45) + 30;
    this.age = rand.nextInt(10) + 22;
    this.points = 0;
    this.gp = 0;
    this.rookie = false;
  }

  public void displayStats() {
    if(this.gp == 0) {
      System.out.println(this.name + ": " + this.offense + " Offense, " + this.defense + " Defense, Age " + this.age);
    } else {
        double average = Math.round(((float)this.points / this.gp) * 10) / 10.0;
        System.out.println(this.name + ": " + average + " ppg (" + this.offense + " Offense, " + this.defense + " Defense, Age " + this.age + ")"); 
    }
    /*(Math.round(((float)this.c.points / this.gp) * 100) / 100.00)*/
  }


  public String[] updateStat() {
    int change = 0;
    /*
    Young: (-5/6)(x-25)+1
    Prime: (-1/25)(x-25)^2
    Old: -x+30
    */
    //Checks what stat is being updated and applies the change
    Random rand = new Random();
    //Output for printing stat changes
    int changedStat = 0;

    String[] stats = this.getStats();
    if(this.rookie == true) {
        this.rookie = false;
        stats[4] += " (+0)";
        stats[5] += " (+0)"; 
        return (stats);
      }
    for(int i = 0; i < 2; i++) {
      String output = "";
      if(this.age <= 25) {
        change = (int)Math.round(2*Math.random()*((-5/6.00)*(this.age - 25) + 1));
      } else if(this.age < 30) {
        change = (int)Math.round((-1.00/25)*(this.age-25)+1);
      } else {
        change = (int)Math.round(-1.5 * (this.age - 30));
      }
      int oldStat = Integer.parseInt(stats[i + 4]);
      int newStat = oldStat + change;
      Class c = Player.class;
      try {
        Method m = c.getMethod("update" + this.intToStat(i), Integer.class);
        m.invoke(this, newStat);
      } catch (Exception e) {
        System.out.println(e);
      }
      stats[i + 4] = Integer.toString(newStat);
      if(i == 0) {
        oldStat = this.offense;
      } else {
        oldStat = this.defense;
      }

      output += " (";
      if(change >= 0) {output += "+";}
      output += Integer.toString(change) + ")";
      stats[i + 4] += output;
    }
    this.age++;
    return (stats);
  }

  public String getName() throws Exception {
    Scanner nameSc = new Scanner(new File("Baby Names.csv"));
    nameSc.useDelimiter("\n");
    List<String> firstNames = new ArrayList<String>();
    while(nameSc.hasNextLine()) {
      String name = nameSc.next();
      firstNames.add(name);
    }
    nameSc = new Scanner(new File("Surnames.csv"));
    nameSc.useDelimiter("\n");
    List<String> lastNames = new ArrayList<String>();
    while(nameSc.hasNextLine()) {
      String name = nameSc.next();
      name = name.charAt(0) + name.substring(1, name.length()).toLowerCase();
      lastNames.add(name);
    }
    nameSc.close();
    String firstName = firstNames.get(rand.nextInt(firstNames.size() - 1));
    
    name = firstName + " " + lastNames.get(rand.nextInt(lastNames.size() - 1));
    return name.substring(0,firstName.length() - 1) + name.substring(firstName.length(), name.length() - 1);
  }

  public String intToStat(int position) {
    if(position == 0) {
      return "Offense";
    } else {
      return "Defense";
    }
  }


  public void updateOffense(Integer newStat) {
    this.offense = newStat;
  }

  public void updateDefense(Integer newStat) {
    this.defense = newStat;
  }

  public String[] getStats() {
    String[] output = new String[7];
    output[0] = String.valueOf(this.team.getPos(this.position));
    output[1] = this.name;
    output[2] = String.valueOf((Math.round((float)this.points / this.gp * 10) / 10.00));
    output[3] = String.valueOf((Math.round((float)this.pointsAllowed / this.gp * 10) / 10.00));
    output[4] = String.valueOf(this.offense);
    output[5] = String.valueOf(this.defense);
    output[6] = String.valueOf(this.age);
    return output;
  }

}