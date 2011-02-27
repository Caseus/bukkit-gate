package caseus.Gate;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockDamageLevel;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockListener;

import caseus.Gate.Gate;

public class GateBlockListener extends BlockListener {
	public static Gate plugin;
	
	public GateBlockListener(Gate instance){
		plugin = instance;
	}
	
	public void onBlockPhysics(BlockPhysicsEvent event){
		Block block = event.getBlock();
		
		if(block.getType() == Material.WOODEN_DOOR){
			if(block.getFace(BlockFace.UP, 1).getType() == Material.AIR){
				event.setCancelled(true);
			}
		}
	}
	
	public void onBlockDamage(BlockDamageEvent event){
		//should probably be BlockDamageLevel.BROKEN, but that wasn't working
		if(event.getDamageLevel() != BlockDamageLevel.STOPPED){
			return;
		}
		
		Block fence = event.getBlock();
		if(fence.getType() != Material.FENCE){
			return;
		}
		
		BlockFace[] dirs = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
		
		for(int i = 0; i < dirs.length; i++){
			Block door = fence.getFace(dirs[i], 1);
			
			//check for door
			if(door.getType() == Material.WOODEN_DOOR){
				//check that its a bottom
				if((door.getData() & 0x8) == 0){
					Block top = door.getFace(BlockFace.UP, 1);
					if(top.getType() == Material.AIR){
						top.setType(Material.WOODEN_DOOR);
						top.setData((byte)(door.getData() | 0x8));
					}
				}
			}
		}
	}
	
	public void onBlockPlace(BlockPlaceEvent event){
		Block block = event.getBlock();
		
		if(block.getType() != Material.FENCE){
			return;
		}
		
		ArrayList<Block> doors = new ArrayList<Block>();
		
		BlockFace[] dirs = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
		
		for(int i = 0; i < dirs.length; i++){
			Block door = block.getFace(dirs[i], 1);
			if(door.getType() == Material.WOODEN_DOOR){
				if((door.getData() & 0x8) == 0){
					doors.add(door);
				}
			}
		}
		
		for(int i = 0; i < doors.size(); i++){
			Block bottom = doors.get(i);
			Block top = bottom.getFace(BlockFace.UP, 1);
			
			byte data = bottom.getData();
			
			//remove top
			Material topType = top.getType();
			if(topType == Material.WOODEN_DOOR){
				if((top.getData() & 0x8) != 0){
					top.setType(Material.AIR);
				}
			}
			
			//replace bottom
			bottom.setType(Material.WOODEN_DOOR);
			bottom.setData(data);
		}
	}
}