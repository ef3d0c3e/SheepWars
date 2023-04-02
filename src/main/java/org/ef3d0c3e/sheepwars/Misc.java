package org.ef3d0c3e.sheepwars;


import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Sheep;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Misc
{
	static void init()
	{
		// Prevents players from using the "mute" feature
		SheepWars.protocolManager.addPacketListener(new PacketAdapter(SheepWars.plugin, ListenerPriority.NORMAL, PacketType.Play.Server.CHAT)
		{
			@Override
			public void onPacketSending(PacketEvent ev)
			{
				PacketContainer packet = ev.getPacket();
				packet.getUUIDs().write(0, new UUID(0L, 0L));
			}
		});
	}
}
