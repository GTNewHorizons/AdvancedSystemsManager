package advancedsystemsmanager.client.render;

import advancedsystemsmanager.blocks.BlockTileElement;
import advancedsystemsmanager.registry.ClusterRegistry;
import advancedsystemsmanager.tileentities.TileEntityCamouflage;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderCamouflage implements ISimpleBlockRenderingHandler
{
    private int id;

    public RenderCamouflage()
    {
        id = RenderingRegistry.getNextAvailableRenderId();
        BlockTileElement.RENDER_ID = id;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        block.setBlockBoundsForItemRender();
        renderer.setRenderBoundsFromBlock(block);

        GL11.glPushMatrix();
        GL11.glRotatef(90, 0, 1, 0);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        Tessellator tessellator = Tessellator.instance;

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, metadata));

        tessellator.setNormal(0F, 1F, 0F);
        renderer.renderFaceYPos(block, 0, 0, 0, block.getIcon(1, metadata));

        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, metadata));

        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, metadata));

        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, metadata));

        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, metadata));

        tessellator.draw();

        GL11.glPopMatrix();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        Tessellator.instance.setColorOpaque_F(1F, 1F, 1F);

        if (block instanceof BlockTileElement)
        {
            TileEntityCamouflage camouflage = ClusterRegistry.CAMO.getTileEntity(world, x, y, z);
            if (camouflage != null)
            {
                block.setBlockBoundsBasedOnState(world, x, y, z);
                double maxX = block.getBlockBoundsMaxX();
                double maxY = block.getBlockBoundsMaxY();
                double maxZ = block.getBlockBoundsMaxZ();
                double minX = block.getBlockBoundsMinX();
                double minY = block.getBlockBoundsMinY();
                double minZ = block.getBlockBoundsMinZ();

                IBlockAccess renderWorld = renderer.blockAccess;
//                for (int i = 0; i< 6; i++)
                int i = 0;
                {
                    setBlockBounds(renderer, minX, minY, minZ, maxX, maxY, maxZ, i);
                    if (camouflage.hasSideBlock(i))
                    {
                        renderer.blockAccess = new CamouflageBlockAccess(i, camouflage, renderWorld);
                        renderer.renderBlockByRenderType(camouflage.getSideBlock(i), x, y, z);
                    } else
                    {
                        renderer.renderStandardBlock(block, x, y, z);
                    }
                }


                if (camouflage.getCamouflageType().useDoubleRendering())
                {
                    setBlockBounds(renderer, minX + 0.0015D, minY + 0.0015D, minZ + 0.0015D, maxX - 0.0015D, maxY - 0.0015D, maxZ - 0.0015D, i);
                    if (camouflage.hasSideBlock(1))
                    {
                        if (renderer.blockAccess == renderWorld)
                        {
                            renderer.blockAccess = new CamouflageBlockAccess(1, camouflage, renderWorld);
                        }
                        renderer.renderBlockByRenderType(camouflage.getSideBlock(1), x, y, z);
                    }
                }
                renderer.unlockBlockBounds();
                renderer.blockAccess = renderWorld;
            } else
            {
                block.setBlockBoundsForItemRender();
                renderer.setRenderBoundsFromBlock(block);
                renderer.renderStandardBlock(block, x, y, z);
            }
            return true;
        }

        return false;
    }

    private static void setBlockBounds(RenderBlocks renderer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, int side)
    {
        renderer.overrideBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId)
    {
        return true;
    }

    @Override
    public int getRenderId()
    {
        return id;
    }


}
