/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package appeng.tile.powersink;


import net.minecraftforge.common.util.ForgeDirection;

import Reika.RotaryCraft.API.Power.ShaftPowerReceiver;

import appeng.api.config.PowerUnits;
import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import appeng.transformer.annotations.Integration.Interface;
import appeng.transformer.annotations.Integration.Method;
import appeng.util.Platform;


@Interface( iname = "RotaryCraft", iface = "Reika.RotaryCraft.API.Power.ShaftPowerReceiver" )
public abstract class RotaryCraft extends IC2 implements ShaftPowerReceiver
{

	private int omega = 0;
	private int torque = 0;
	private long power = 0;
	private int alpha = 0;

	@TileEvent( TileEventType.TICK )
	@Method( iname = "RotaryCraft" )
	public void Tick_RotaryCraft()
	{
		if( this.worldObj != null && !this.worldObj.isRemote && this.power > 0 )
			this.injectExternalPower( PowerUnits.WA, this.power );
	}

	@Override
	public final int getOmega()
	{
		return this.omega;
	}

	@Override
	public final int getTorque()
	{
		return this.torque;
	}

	@Override
	public final long getPower()
	{
		return this.power;
	}

	@Override
	public final String getName()
	{
		return "AE";
	}

	@Override
	public final int getIORenderAlpha()
	{
		return this.alpha;
	}

	@Override
	public final void setIORenderAlpha( int io )
	{
		this.alpha = io;
	}

	@Override
	public final void setPower( long p )
	{
		if( Platform.isClient() )
			return;

		this.power = p;
	}

	@Override
	public final void noInputMachine()
	{
		this.power = 0;
		this.torque = 0;
		this.omega = 0;
	}

	@Override
	public final void setTorque( int t )
	{
		this.torque = t;
	}

	@Override
	public final void setOmega( int o )
	{
		this.omega = o;
	}

	public final boolean canReadFromBlock( int x, int y, int z )
	{
		ForgeDirection side = ForgeDirection.UNKNOWN;

		if( x == this.xCoord - 1 )
			side = ForgeDirection.WEST;
		else if( x == this.xCoord + 1 )
			side = ForgeDirection.EAST;
		else if( z == this.zCoord - 1 )
			side = ForgeDirection.NORTH;
		else if( z == this.zCoord + 1 )
			side = ForgeDirection.SOUTH;
		else if( y == this.yCoord - 1 )
			side = ForgeDirection.DOWN;
		else if( y == this.yCoord + 1 )
			side = ForgeDirection.UP;

		return this.getPowerSides().contains( side );
	}

	@Override
	public final boolean canReadFrom( ForgeDirection side )
	{
		return this.getPowerSides().contains( side );
	}

	@Override
	public final boolean isReceiving()
	{
		return true;
	}

	@Override
	public final int getMinTorque( int available )
	{
		return 0;
	}
}
