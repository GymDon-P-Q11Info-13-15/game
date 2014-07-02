package de.gymdon.inf1315.game;

public class Castle extends Building
{

	public Castle(Player owner, int x, int y)
	{
		this.owner = owner;
		this.x = x;
		this.y = y;
		this.hp = 10000;
		this.defense = 80;
	}

	@Override
	public void occupy(Player p)
	{
		// cannot be occupied
		// sure can it be occupied, if occupied the game is over ;)
	}

	public int getSizeX()
	{
		return owner == null ? 2 : 1;
	}

	public int getSizeY()
	{
		return owner == null ? 2 : 1;
	}

}
