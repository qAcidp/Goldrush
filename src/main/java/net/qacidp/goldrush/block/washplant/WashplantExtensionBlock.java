package net.qacidp.goldrush.block.washplant;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.qacidp.goldrush.block.entity.WashplantExtensionBlockEntity;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.qacidp.goldrush.item.ModItems;
import net.qacidp.goldrush.item.washplant.WashplantMatItem;
import net.qacidp.goldrush.block.entity.WashplantBaseBlockEntity;
import net.qacidp.goldrush.network.SyncWashplantMatPacket;
import net.neoforged.neoforge.network.PacketDistributor;

public class WashplantExtensionBlock extends BaseEntityBlock {

    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 11, 16);

    public WashplantExtensionBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WashplantExtensionBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (!isNextToBase(level, pos)) {
            return null;
        }

        BlockPos pos2 = pos.north();
        BlockPos pos3 = pos.north(2);

        if (!level.isClientSide) {
            if (level.getBlockState(pos2).canBeReplaced(context) &&
                    level.getBlockState(pos3).canBeReplaced(context)) {

                level.setBlock(pos2, this.defaultBlockState(), 11);
                level.setBlock(pos3, this.defaultBlockState(), 11);

// BlockEntities manuell erstellen
                level.setBlockEntity(new WashplantExtensionBlockEntity(pos2, this.defaultBlockState()));
                level.setBlockEntity(new WashplantExtensionBlockEntity(pos3, this.defaultBlockState()));
            }
        }

        return this.defaultBlockState();
    }

    private boolean isNextToBase(Level level, BlockPos pos) {
        return level.getBlockState(pos.north()).getBlock() instanceof WashplantBaseBlock ||
                level.getBlockState(pos.south()).getBlock() instanceof WashplantBaseBlock ||
                level.getBlockState(pos.east()).getBlock() instanceof WashplantBaseBlock ||
                level.getBlockState(pos.west()).getBlock() instanceof WashplantBaseBlock;
    }

    @Override
    public MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(WashplantExtensionBlock::new);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                           Player player, InteractionHand hand, BlockHitResult hitResult) {

        if (!level.isClientSide) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (!(entity instanceof WashplantExtensionBlockEntity extEntity)) { // GEÄNDERT!
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            // Matte platzieren
            if (stack.getItem() instanceof WashplantMatItem matItem) {
                if (extEntity.hasMat()) { // GEÄNDERT!
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cAlready has a mat!"));
                    return ItemInteractionResult.FAIL;
                }

                extEntity.setHasMat(true); // GEÄNDERT!
                extEntity.setMatWashCycles(matItem.getWashCycles()); // GEÄNDERT!
                extEntity.setMatMaterialPoints(WashplantMatItem.getMaterialPoints(stack)); // Punkte laden

                PacketDistributor.sendToPlayersTrackingChunk((net.minecraft.server.level.ServerLevel) level,
                        new net.minecraft.world.level.ChunkPos(pos),
                        new SyncWashplantMatPacket(pos, true, matItem.getWashCycles(),
                                extEntity.getMatMaterialPoints(), true)); // getMatMaterialPoints() statt 0


                if (!player.isCreative()) {
                    stack.shrink(1);
                }

                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aMat placed!"));
                return ItemInteractionResult.SUCCESS;
            }

            // Matte entfernen
            if (stack.isEmpty() && player.isCrouching()) {
                if (!extEntity.hasMat()) { // GEÄNDERT!
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cNo mat here!"));
                    return ItemInteractionResult.FAIL;
                }

                ItemStack matItem = getMatItemForWashCycles(extEntity.getMatWashCycles()); // GEÄNDERT!
                WashplantMatItem.setMaterialPoints(matItem, extEntity.getMatMaterialPoints()); // Punkte speichern

                if (!player.getInventory().add(matItem)) {
                    player.drop(matItem, false);
                }

                extEntity.removeMat(); // GEÄNDERT!

                PacketDistributor.sendToPlayersTrackingChunk((net.minecraft.server.level.ServerLevel) level,
                        new net.minecraft.world.level.ChunkPos(pos),
                        new SyncWashplantMatPacket(pos, false, 0, 0, true));

                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aMat removed!"));
                return ItemInteractionResult.SUCCESS;
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private ItemStack getMatItemForWashCycles(int cycles) {
        if (cycles == 0) {
            return new ItemStack(ModItems.WASHPLANT_MAT.get());
        } else if (cycles < 3) {
            return new ItemStack(ModItems.WASHPLANT_MAT_LIGHT.get());
        } else if (cycles < 5) {
            return new ItemStack(ModItems.WASHPLANT_MAT_MEDIUM.get());
        } else {
            return new ItemStack(ModItems.WASHPLANT_MAT_HEAVY.get());
        }
    }


}