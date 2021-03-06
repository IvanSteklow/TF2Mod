package ru.ivansteklow.tf2mod.blocks;

import java.util.Random;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.ivansteklow.tf2mod.Core;
import ru.ivansteklow.tf2mod.init.BlockList;
import ru.ivansteklow.tf2mod.init.QualityList;
import ru.ivansteklow.tf2mod.tileentities.MetalRefineryTileEntity;

public class BlockMetalRefinery extends BlockBase implements ITileEntityProvider {

	public static final int GUI_ID = 1;
	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	public static final PropertyBool ACTIVATED = PropertyBool.create("activated");

	public BlockMetalRefinery() {
		super(Material.ANVIL, "metal_refinery", QualityList.QUALITY_NORMAL, 8F, true);
		setSoundType(SoundType.ANVIL);
		setHarvestLevel("pickaxe", 2);
		setDefaultState(
				this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(ACTIVATED, false));
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new MetalRefineryTileEntity();
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(BlockList.metal_refinery);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) {
			return true;
		}
		TileEntity te = worldIn.getTileEntity(pos);
		if (!(te instanceof MetalRefineryTileEntity)) {
			return false;
		}
		playerIn.openGui(Core.instance, GUI_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		worldIn.setBlockState(pos, state.withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer)));
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FACING, ACTIVATED });
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumFacing = EnumFacing.getFront(meta);
		if (enumFacing.getAxis() == EnumFacing.Axis.Y || enumFacing == EnumFacing.UP)
			enumFacing = EnumFacing.NORTH;
		if (enumFacing == EnumFacing.DOWN)
			enumFacing = EnumFacing.SOUTH;
		return this.getDefaultState().withProperty(FACING, enumFacing);
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		setDefaultState(state);
	}

	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tileentity = worldIn.getTileEntity(pos);

		if (tileentity instanceof MetalRefineryTileEntity) {
			tileentity = (MetalRefineryTileEntity) worldIn.getTileEntity(pos);
			worldIn.spawnEntity(new EntityItem(worldIn, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
					((MetalRefineryTileEntity) tileentity).itemStackHandler.getStackInSlot(0)));
			worldIn.spawnEntity(new EntityItem(worldIn, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
					((MetalRefineryTileEntity) tileentity).itemStackHandler.getStackInSlot(1)));
			worldIn.spawnEntity(new EntityItem(worldIn, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
					((MetalRefineryTileEntity) tileentity).itemStackHandler.getStackInSlot(2)));
		}
		super.breakBlock(worldIn, pos, state);
	}

	public void setBlockActivated(boolean value) {
		this.getDefaultState().withProperty(ACTIVATED, value);
	}
	
	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		if(worldIn.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock() == Blocks.ANVIL)
			return true;
		else
			return false;
	}

}
