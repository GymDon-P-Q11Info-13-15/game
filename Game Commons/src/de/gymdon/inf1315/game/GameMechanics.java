package de.gymdon.inf1315.game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class GameMechanics implements ActionListener {
    Random r = new Random();
    boolean[][] tempRange;

    public boolean won;
    public Game game;

    /**
     * Map, Buildings etc wird alle im MapGenerator generiert Da braucht ihr
     * hier in den GameMechanics nichts machen Ich denk mal ein Objekt von
     * GameMechanics wird im Client erzeugt, dann wird da die Karte etc gleich
     * übergeben. (?)
     */

    public GameMechanics() {
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

	if (!won) { // Ablauf EINER Spielrunde (was ein Spieler machen darf)
		    // (Bauen -> Bewegen -> Kaempfen)

	    for (int a = 0; a < game.units.length; a++)
		for (int b = 0; b < game.units[a].length; b++)
		    if (game.units[a][b] != null)
			if (game.units[a][b].getHP() <= 0)
			    game.units[a][b] = null;

	    for (int a = 0; a < game.buildings.length; a++)
		for (int b = 0; b < game.buildings[a].length; b++)
		    if (game.buildings[a][b] != null) {
			if (game.buildings[a][b].getHP() <= 0 && !(game.buildings[a][b] instanceof Mine))
			    game.buildings[a][b] = null;
			else if (game.buildings[a][b].getHP() <= 0 && game.buildings[a][b] instanceof Mine)
			    game.buildings[a][b].occupy(game.activePlayer == game.player1 ? game.player1 : game.player2);
		    }

	    if (game.buildings[1][game.mapgen.getMapHeight() / 2 - 1] == null)
		won = true;
	    if (game.buildings[game.mapgen.getMapWidth() - 3][game.mapgen.getMapHeight() / 2 - 1] == null)
		won = true;

	    if (game.round == 0)
		game.GoldDif = 0;

	    // start round

	    // phase = "building etc";

	    // Client beendet phase -> next phase;

	    // phase % 3 == 0 -> Change active player

	    // Other player -> gleicher Ablauf wie oben

	    // if castle destroyed -> won = true;

	}

	else
	    return;
    }

    public String phaseButtonText() {
	if (game.phase % 3 == 2) {
	    return "gui.game.endRound";
	} else {
	    return "gui.game.endPhase";
	}

    }

    public void nextPhase() {

	if (game.phase + 1 == 6) {
	    game.round++;
	    game.phase = 0;
	    game.activePlayer = game.activePlayer == game.player1 ? game.player2 : game.player1;

	    for (int x = 0; x < game.units.length; x++) {
		for (int y = 0; y < game.units[x].length; y++) {
		    Unit u = game.units[x][y];
		    if (u != null)
			u.reset();
		}
	    }

	    game.GoldDif = 0;
	    for (int x = 0; x < game.buildings.length; x++) {
		for (int y = 0; y < game.buildings[x].length; y++) {
		    Building b = game.buildings[x][y];
		    if (b != null && b.owner == (game.activePlayer == game.player1 ? game.player1 : game.player2)) {
			(game.activePlayer == game.player1 ? game.player1 : game.player2).gold += b.getIncome();
			game.GoldDif += b.getIncome();
		    }
		}
	    }
	} else {
	    game.phase++;
	    if (game.phase % 3 == 0) {
		game.activePlayer = game.activePlayer == game.player1 ? game.player2 : game.player1;

		for (int x = 0; x < game.units.length; x++) {
		    for (int y = 0; y < game.units[x].length; y++) {
			Unit u = game.units[x][y];
			if (u != null)
			    u.reset();
		    }
		}

		game.GoldDif = 0;
		for (int x = 0; x < game.buildings.length; x++) {
		    for (int y = 0; y < game.buildings[x].length; y++) {
			Building b = game.buildings[x][y];
			if (b != null && b.owner == (game.activePlayer == game.player1 ? game.player1 : game.player2) && game.round != 0) {
			    (game.activePlayer == game.player1 ? game.player1 : game.player2).gold += b.getIncome();
			    game.GoldDif += b.getIncome();
			}
		    }
		}
	    }
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
	    if ((a.getHP() + b.getHP()) <= 120) {
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
    public void buildBuilding(Player p, Building b) {
	if (p.gold < b.cost) {
	    System.err.println("More gold requiered");
	} else {
	    p.gold -= b.cost;
	    game.buildings[b.x][b.y] = b;
	}
    }

    /**
     * Moves a unit to a field if possible
     * 
     * @param u
     *            Unit
     * @param x
     *            x-coordinate of the field to move to Archer * @param y
     *            y-coordinate of the field to move to
     * @return true if move was possible, false otherwise
     */
    public boolean move(Unit u, int x, int y) {

	getAccessableFields(u);
	if (tempRange[x][y] == true) {
	    game.units[u.x][u.y] = null;
	    u.act_speed -= Math.abs(u.x - x);
	    u.act_speed -= Math.abs(u.y - y);
	    u.x = x;
	    u.y = y;
	    game.units[x][y] = u;
	    return true;
	} else {
	    return false;
	}

    }

    public void getAccessableFields(Unit a) {
	tempRange = new boolean[game.map.length][game.map[0].length];
	step(a.act_speed, a.x, a.y);
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
	step(a.act_speed, a.x, a.y);
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

	if (x < 0 || y < 0 || x >= tempRange.length || y >= tempRange[0].length)
	    return;

	Building b = null;
	boolean n = false;
	for (int x1 = x; x1 > x1 - 6 && x1 >= 0; x1--) {
	    if (n)
		break;
	    for (int y1 = y; y1 > y1 - 6 && y1 >= 0; y1--) {
		Building c = game.buildings[x1][y1];
		if (c != null && c.getSizeX() + x1 > x && c.getSizeY() + y1 > y) {
		    b = c;
		    n = true;
		    break;
		}
	    }
	}

	if (game.map[x][y].isWalkable() && b == null) { // can
	    // only
	    // walk
	    // if
	    // no
	    // building
	    // and
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
	if (attchance < 0) {
	    return 5;
	} else if (attchance > 75) {
	    return 75;
	} else {
	    return attchance;
	}
    }

    public void combat(Unit attacker, Unit defender, int round) {

	if (round < 200) {
	    if (defender.range < Math.abs(attacker.x - defender.x) || defender.range < Math.abs(attacker.y - defender.y))
	    // Pr�fen ob der Verteidiger sich wehren kann
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
	if (b instanceof Mine && b.owner == null) {
	    if (1 >= Math.abs(u.x - b.x) && 1 >= Math.abs(u.y - b.y))
		b.setHP(0);
	    return;
	}
	else {
	    b.setHP(b.hp - u.attack * u.hp / 100 * 125 * (int) ((75 + r.nextInt(51)) / 100));
	    return;
	}
    }

    public void create(Player p, Unit u, Building b) {
	if (p.gold < u.cost) {
	    System.err.println("More gold requiered");
	} else {
	    p.gold -= u.cost;
	    if (u instanceof Archer && b instanceof Barracks)
		game.units[u.x][u.y] = u;
	    if (u instanceof Knight && b instanceof Barracks)
		game.units[u.x][u.y] = u;
	    if (u instanceof Miner)
		game.units[u.x][u.y] = u;
	    if (u instanceof Spearman)
		game.units[u.x][u.y] = u;
	    if (u instanceof Swordsman)
		game.units[u.x][u.y] = u;
	}
    }

    @Override
    public void actionPerformed(ActionEvent e) {

	if (e.getSource() instanceof GameObject) {
	    GameObject g = (GameObject) e.getSource();
	    game.options = g.clicked(game.phase % 3);
	}
    }
}
