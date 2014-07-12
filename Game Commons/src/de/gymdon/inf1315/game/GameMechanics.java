package de.gymdon.inf1315.game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class GameMechanics implements ActionListener {
    Random r = new Random();
    // Tile[][] map;
    // Building[][] buildings;
    // Unit[][] units;
    boolean[][] tempRange;
    
    boolean won;
    public Game game;

    /**
     * Map, Buildings etc wird alle im MapGenerator generiert Da braucht ihr
     * hier in den GameMechanics nichts machen Ich denk mal ein Objekt von
     * GameMechanics wird im Client erzeugt, dann wird da die Karte etc gleich
     * übergeben. (?)
     */

    public GameMechanics() { // neue Welt mit Breite x und Höhe y
	// this.map = Client.instance.map;
	// buildings = Client.instance.buildings;
	// units = Client.instance.units;
	won = false;
    }

    /**
     * Set a Map as internal Tile Array
     * 
     * @param t
     *            Tile Array as new Map
     */
    public void setMap(Tile[][] t) {
	game.map = t;
    }
    

    public void run() {

	while (!won) { // Ablauf EINER Spielrunde (was ein Spieler machen darf)
		       // (Bauen -> Bewegen -> Kaempfen)

	    game.phase = 0;
	    
	    if(game.phase%3==0){
		game.options[0]=false;
		game.options[1]=false;
	    }
	    if(game.phase%3==1){
		
	    }
	    
	    // start round

	    // phase = "building etc";

	    // Client beendet phase -> next phase;

	    // phase % 3 == 0 -> Change active player

	    // Other player -> gleicher Ablauf wie oben

	    // if castle destroyed -> won = true;

	}

    }

    public String phaseButtonText() {
	if (game.phase == 3 || game.phase == 5) {
	    return "gui.game.endRound";
	} else {
	    return "gui.game.endPhase";
	}

    }

    public void nextPhase() {

	if (game.phase + 1 == 6) {
	    game.round++;
	    game.phase = 0;
	} else {
	    game.phase++;
	}

    }

    /**
     * Stacks two Units if their combined HP is lower than 120
     * 
     * @param a
     *            first Unit to stack into another
     * @param b
     *            second Unit that is stacked into
     */
    public void stack(Unit a, Unit b) {
	getAccessableFields(a);
	if (tempRange[b.x][b.y] == true) {
	    if ((a.getHP() + b.getHP()) >= 120) {
		b.setHP(a.getHP() + b.getHP());
		game.units[a.x][a.y] = null;
	    }
	}
    }

    /**
     * Build a new building if possible
     * 
     * @param b
     *            Building (attention to building type)
     * @param x
     *            x-coordinate of the field to build on
     * @param y
     *            y-coordinate of the field to build on
     * 
     */
    public void buildBuilding(Building b, int x, int y) {
	if (x >= 0 && y >= 0) {
	    // check player's gold!
	    if (game.buildings[x][y] == null) {
		game.buildings[x][y] = b;
	    }
	} else {
	    throw new IllegalArgumentException("Field position must be positive");
	}
    }

    /**
     * Moves a unit to a field if possible
     * 
     * @param u
     *            Unit
     * @param x
     *            x-coordinate of the field to move to
 Archer    * @param y
     *            y-coordinate of the field to move to
     * @return true if move was possible, false otherwise
     */
    public boolean move(Unit u, int x, int y) {
	getAccessableFields(u);
	if (tempRange[x][y] == true) {
	    u.x = x;
	    u.y = y;
	    return true;
	} else
	    return false;

	/*
	 * int xold = u.x; // Bisherige Koordinaten der Unit int yold = u.y; int
	 * spd = u.getSpeed(); // Speed der Unit int effspd = (int) Math.abs((x
	 * - xold) + (y - yold - 1)); // Effektiv // benoetigte // Speed, um //
	 * zum neuen // Feld zu // gelangen // (Feldmalus // einberechnet)
	 * 
	 * if (effspd < 1) { effspd = 1;
	 * 
	 * }
	 * 
	 * if (effspd <= spd) {
	 * 
	 * }
	 */

    }

    public void getAccessableFields(Unit a) {
	tempRange = new boolean[game.map.length][game.map[0].length];
	step(a.getSpeed(), a.x, a.y);

    }

    /**
     * returns array of all accessable fields
     * 
     * @param a
     *            Unit whose movement is calculated
     * @return boolean array with true for all accessable fields
     */
    public boolean[][] getAccessableField(Unit a) {
	tempRange = new boolean[game.map.length][game.map[0].length];
	step(a.getSpeed(), a.x, a.y);
	return tempRange;
    }

    public boolean isAccessable(Unit u, int x, int y) {
	getAccessableFields(u);
	if (tempRange[x][y] == true) {
	    return true;
	} else {
	    return false;
	}

    }

    private void step(int actualSpeed, int x, int y) {

	if (game.map[x][y].isWalkable() && game.buildings[x][y] == null) { // can
									   // only
									   // walk
									   // if
									   // no
									   // building
									   // or
									   // walkable
	    int newSpeed = (int) (actualSpeed - game.map[x][y].getGroundFactor()); // TODO:
										   // Hotfix
										   // by
										   // Simi,
										   // groundFactor
										   // is
										   // now
										   // double

	    if (newSpeed >= 1) {

		tempRange[x][y] = true;
		step(newSpeed, x - 1, y);
		step(newSpeed, x + 1, y);
		step(newSpeed, x, y + 1);
		step(newSpeed, x, y - 1);
	    } else if (newSpeed > 0) {
		tempRange[x][y] = true;
		step(1, x - 1, y);
		step(1, x + 1, y);
		step(1, x, y + 1);
		step(1, x, y - 1);
	    } else { // Movement-points used -> field not accessible

	    }

	} else { // Field not walkable

	}

    }

    /*
     * public void buildUnit(Player p,Unit u,int number,Building b){ if(p.gold <
     * u.cost*number) {
     * 
     * }
     * 
     * 
     * }
     */

    public int strikechance(Unit striker, Unit stroke) {
	int attchance = 80 - (striker.attack + striker.hp / 4 - stroke.defense / 2 - stroke.hp / 4);
	System.out.println(attchance);
	if (attchance < 0) {
	    return 5;
	} else if (attchance > 75) {
	    return 75;
	} else {
	    return attchance;
	}
    }

    public void combat(Unit attacker, Unit defender, int round) {

	if (round < 100) {
	    if (attacker.range > defender.range)
	    // Prüfen ob der Verteidiger sich wehren kann
	    {
		defender.setHP(defender.hp - r.nextInt(attacker.attack) * attacker.hp / 100);
		

		return;
	    } else {
		if (r.nextInt(81) >= strikechance(attacker, defender)) {
		    defender.setHP(defender.hp - 1);
		}
		
		if (r.nextInt(81) >= strikechance(defender, attacker)) {
		    attacker.setHP(attacker.hp - 1);
		}
		
	    }
	    if (defender.hp > 0 && attacker.hp > 0) {
		combat(attacker, defender, round + 1);
	    }
	}
    }

    public void pillage(Unit u, Building b) {
	if (r.nextInt(101) >= b.defense) {
	    b.hp = b.hp - u.attack * 125 * (int) ((75 + r.nextInt(51)) / 100);
	}

    }
    
public void create(Player p,Unit u,Building b)
    {
if(p.gold < u.cost){System.err.println("More gold requiered");}
else{
   p.gold = p.gold - u.cost; 
   if(u instanceof Archer && b instanceof Barracks)game.units[b.x-1][b.y]=new Archer(p,b.x-1,b.y); 
   if(u instanceof Knight && b instanceof Barracks)game.units[b.x-1][b.y]=new Knight(p,b.x-1,b.y);
   if(u instanceof Miner)game.units[b.x-1][b.y]=new Miner(p,b.x-1,b.y);
   if(u instanceof Spearman)game.units[b.x-1][b.y]=new Spearman(p,b.x-1,b.y);
   if(u instanceof Swordsman)game.units[b.x-1][b.y]=new Swordsman(p,b.x-1,b.y);
   //if(u instanceof Scout)game.units[b.x-1][b.y]=new Scout(p,b.x-1,b.y);
    }
    }
    @Override
    
public void actionPerformed(ActionEvent e) {

	if (e.getSource() instanceof Unit) {
	    Unit u = (Unit) e.getSource();
	    game.options=u.clicked(game.phase % 3);
	    System.out.println("Unit: (" + u.x + "|" + u.y + ")");
	}

	if (e.getSource() instanceof Building) {
	    Building b = (Building) e.getSource();
	    game.options=b.clicked(game.phase%3);
	    System.out.println("Building: (" + b.x + "|" + b.y + ")");
	}
    }
}
