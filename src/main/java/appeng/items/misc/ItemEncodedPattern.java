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

package appeng.items.misc;


import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

import appeng.api.AEApi;
import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import appeng.client.render.items.ItemEncodedPatternRenderer;
import appeng.core.CommonHelper;
import appeng.core.features.AEFeature;
import appeng.core.localization.GuiText;
import appeng.helpers.PatternHelper;
import appeng.items.AEBaseItem;
import appeng.util.Platform;


public class ItemEncodedPattern extends AEBaseItem implements ICraftingPatternItem
{
	// rather simple client side caching.
	private static final Map<ItemStack, ItemStack> SIMPLE_CACHE = new WeakHashMap<ItemStack, ItemStack>();

	public ItemEncodedPattern()
	{
		this.setFeature( EnumSet.of( AEFeature.Patterns ) );
		this.setMaxStackSize( 1 );
		if( Platform.isClient() )
			MinecraftForgeClient.registerItemRenderer( this, new ItemEncodedPatternRenderer() );
	}

	@Override
	public ItemStack onItemRightClick( ItemStack stack, World w, EntityPlayer player )
	{
		this.clearPattern( stack, player );

		return stack;
	}

	@Override
	public boolean onItemUseFirst( ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ )
	{
		return this.clearPattern( stack, player );
	}

	private boolean clearPattern( ItemStack stack, EntityPlayer player )
	{
		if( player.isSneaking() )
		{
			if( Platform.isClient() )
				return false;

			InventoryPlayer inv = player.inventory;

			for( int s = 0; s < player.inventory.getSizeInventory(); s++ )
			{
				if( inv.getStackInSlot( s ) == stack )
				{
					for( ItemStack blankPattern : AEApi.instance().definitions().materials().blankPattern().maybeStack( stack.stackSize ).asSet() )
					{
						inv.setInventorySlotContents( s, blankPattern );
					}

					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void addCheckedInformation( ItemStack stack, EntityPlayer player, List<String> lines, boolean displayMoreInfo )
	{
		ICraftingPatternDetails details = this.getPatternForItem( stack, player.worldObj );

		if( details == null )
		{
			lines.add( EnumChatFormatting.RED + GuiText.InvalidPattern.getLocal() );
			return;
		}

		boolean isCrafting = details.isCraftable();

		IAEItemStack[] in = details.getCondensedInputs();
		IAEItemStack[] out = details.getCondensedOutputs();

		String label = ( isCrafting ? GuiText.Crafts.getLocal() : GuiText.Creates.getLocal() ) + ": ";
		String and = ' ' + GuiText.And.getLocal() + ' ';
		String with = GuiText.With.getLocal() + ": ";

		boolean first = true;
		for( IAEItemStack anOut : out )
		{
			if( anOut == null )
			{
				continue;
			}

			lines.add( ( first ? label : and ) + anOut.getStackSize() + ' ' + Platform.getItemDisplayName( anOut ) );
			first = false;
		}

		first = true;
		for( IAEItemStack anIn : in )
		{
			if( anIn == null )
			{
				continue;
			}

			lines.add( ( first ? with : and ) + anIn.getStackSize() + ' ' + Platform.getItemDisplayName( anIn ) );
			first = false;
		}
	}

	@Override
	public ICraftingPatternDetails getPatternForItem( ItemStack is, World w )
	{
		try
		{
			return new PatternHelper( is, w );
		}
		catch( Throwable t )
		{
			return null;
		}
	}

	public ItemStack getOutput( ItemStack item )
	{
		ItemStack out = SIMPLE_CACHE.get( item );
		if( out != null )
			return out;

		World w = CommonHelper.proxy.getWorld();
		if( w == null )
			return null;

		ICraftingPatternDetails details = this.getPatternForItem( item, w );

		if( details == null )
			return null;

		SIMPLE_CACHE.put( item, out = details.getCondensedOutputs()[0].getItemStack() );
		return out;
	}
}
