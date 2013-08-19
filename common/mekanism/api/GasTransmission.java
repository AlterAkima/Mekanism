package mekanism.api;

import java.util.HashSet;
import java.util.Set;

import mekanism.common.MekanismUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

/**
 * A handy class containing several utilities for efficient gas transfer.
 * @author AidanBrady
 *
 */
public final class GasTransmission 
{
    /**
     * Gets all the tubes around a tile entity.
     * @param tileEntity - center tile entity
     * @return array of TileEntities
     */
    public static TileEntity[] getConnectedTubes(TileEntity tileEntity)
    {
    	TileEntity[] tubes = new TileEntity[] {null, null, null, null, null, null};
    	
    	for(ForgeDirection orientation : ForgeDirection.VALID_DIRECTIONS)
    	{
			TileEntity tube = Object3D.get(tileEntity).getFromSide(orientation).getTileEntity(tileEntity.worldObj);
			
			if(MekanismUtils.checkNetwork(tube, GasNetwork.class))
			{
                tubes[orientation.ordinal()] = tube;
			}
    	}
    	
    	return tubes;
    }
    
    /**
     * Gets all the acceptors around a tile entity.
     * @param tileEntity - center tile entity
     * @return array of IGasAcceptors
     */
    public static IGasAcceptor[] getConnectedAcceptors(TileEntity tileEntity)
    {
    	IGasAcceptor[] acceptors = new IGasAcceptor[] {null, null, null, null, null, null};
   
    	for(ForgeDirection orientation : ForgeDirection.VALID_DIRECTIONS)
    	{
			TileEntity acceptor = Object3D.get(tileEntity).getFromSide(orientation).getTileEntity(tileEntity.worldObj);
			
			if(acceptor instanceof IGasAcceptor)
			{
				acceptors[orientation.ordinal()] = (IGasAcceptor)acceptor;
			}
    	}
    	
    	return acceptors;
    }
    
    /**
     * Gets all the tube connections around a tile entity.
     * @param tileEntity - center tile entity
     * @return array of ITubeConnections
     */
    public static ITubeConnection[] getConnections(TileEntity tileEntity)
    {
    	ITubeConnection[] connections = new ITubeConnection[] {null, null, null, null, null, null};
   
		for(ForgeDirection orientation : ForgeDirection.VALID_DIRECTIONS)
    	{
			TileEntity connection = Object3D.get(tileEntity).getFromSide(orientation).getTileEntity(tileEntity.worldObj);
			
			if(connection instanceof ITubeConnection)
			{
				connections[orientation.ordinal()] = (ITubeConnection)connection;
			}
    	}
    	
    	return connections;
    }
    
    /**
     * Emits a defined gas to the network.
     * @param type - gas type to send
     * @param amount - amount of gas to send
     * @param sender - the sender of the gas
     * @param facing - side the sender is outputting from
     * @return rejected gas
     */
    public static int emitGasToNetwork(EnumGas type, int amount, TileEntity sender, ForgeDirection facing)
    {
    	TileEntity pointer = Object3D.get(sender).getFromSide(facing).getTileEntity(sender.worldObj);
    	
    	if(MekanismUtils.checkNetwork(pointer, GasNetwork.class))
    	{
	    	return ((ITransmitter<GasNetwork>)pointer).getNetwork().emit(amount, type, sender);
    	}
    	
    	return amount;
    }
    
    /**
     * Emits gas from all sides of a TileEntity.
     * @param type - gas type to send
     * @param amount - amount of gas to send
     * @param pointer - sending TileEntity
     * @return rejected gas
     */
    public static int emitGasFromAllSides(EnumGas type, int amount, TileEntity pointer)
    {
    	if(pointer != null)
    	{
       		Set<GasNetwork> networks = new HashSet<GasNetwork>();
    		int totalRemaining = 0;
    		
    		for(ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
    		{
    			TileEntity sideTile = Object3D.get(pointer).getFromSide(side).getTileEntity(pointer.worldObj);
    			
    			if(MekanismUtils.checkNetwork(sideTile, GasNetwork.class))
    			{
    				networks.add(((ITransmitter<GasNetwork>)sideTile).getNetwork());
    			}
    		}
    		
    		int remaining = amount%networks.size();
    		int splitGas = (amount-remaining)/networks.size();
    		
    		for(GasNetwork network : networks)
    		{
    			totalRemaining += network.emit(splitGas+remaining, type, pointer);
    			remaining = 0;
    		}
    		
    		return totalRemaining;
    	}
    	
    	return amount;
    }
}
