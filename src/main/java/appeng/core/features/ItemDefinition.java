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

package appeng.core.features;


import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;

import com.google.common.base.Optional;

import appeng.api.definitions.IItemDefinition;
import appeng.util.Platform;


public class ItemDefinition implements IItemDefinition
{
	private final Item item;
	private final boolean enabled;

	public ItemDefinition( Item item, ActivityState state )
	{
		assert item != null;

		this.item = item;
		this.enabled = state == ActivityState.Enabled;
	}

	@Override
	public final Optional<Item> maybeItem()
	{
		return Optional.of( this.item );
	}

	@Override
	public Optional<ItemStack> maybeStack( int stackSize )
	{
		if( this.enabled )
		{
			return Optional.of( new ItemStack( this.item ) );
		}
		else
		{
			return Optional.absent();
		}
	}

	@Override
	public final boolean isSameAs( ItemStack comparableStack )
	{
		return this.enabled && Platform.isSameItemType( comparableStack, this.maybeStack( 1 ).get() );
	}

	@Override
	public boolean isSameAs( IBlockAccess world, int x, int y, int z )
	{
		return false;
	}
}
