package org.ef3d0c3e.sheepwars.kits;

import java.util.ArrayList;

public class Kits
{
	static public ArrayList<Kit> list;

	static
	{
		list = new ArrayList<>();

		list.add(new MageKit(null));
		list.add(new EnchanterKit(null));
		list.add(new ArcherKit(null));
		list.add(new BarbarianKit(null));
		list.add(new BuilderKit(null));
		list.add(new TechnicianKit(null));
	}
}
