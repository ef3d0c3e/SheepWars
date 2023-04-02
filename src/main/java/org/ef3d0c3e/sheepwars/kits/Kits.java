package org.ef3d0c3e.sheepwars.kits;

import java.util.ArrayList;

public class Kits
{
	static public ArrayList<Kit> list;

	static
	{
		list = new ArrayList<>();

		list.add(new MageKit());
		list.add(new EnchanterKit());
		list.add(new ArcherKit());
		list.add(new BarbarianKit());
		list.add(new BuilderKit());
		list.add(new TechnicianKit());
	}
}
