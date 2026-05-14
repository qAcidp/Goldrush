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
import net.qacidp.goldrush.block.entity.WashplantBaseBlockEntity;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.qacidp.goldrush.item.ModItems;
import net.qacidp.goldrush.item.washplant.WashplantMatItem;
import net.qacidp.goldrush.network.SyncWashplantMatPacket;
import net.neoforged.neoforge.network.PacketDistributor;

public class WashplantBaseBlock extends BaseEntityBlock {

    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 12, 16);

    public WashplantBaseBlock(Properties properties) {
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
        return new WashplantBaseBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        BlockPos pos2 = pos.north();
        BlockPos pos3 = pos.north(2);

        if (!level.isClientSide) {
            if (level.getBlockState(pos2).canBeReplaced(context) &&
                    level.getBlockState(pos3).canBeReplaced(context)) {

                // Flag 3 = UPDATE_NEIGHBORS | UPDATE_CLIENTS
                // Flag 11 = UPDATE_ALL (sollte BlockEntity erstellen)
                level.setBlock(pos2, this.defaultBlockState(), 11);
                level.setBlock(pos3, this.defaultBlockState(), 11);


            }
        }

        return this.defaultBlockState();
    }

    @Override
    public MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(WashplantBaseBlock::new);
    }


    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                           Player player, InteractionHand hand, BlockHitResult hitResult) {

        if (!level.isClientSide) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (!(entity instanceof WashplantBaseBlockEntity baseEntity)) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            // Matte platzieren
            if (stack.getItem() instanceof WashplantMatItem matItem) {
                if (baseEntity.hasMat()) {
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cAlready has a mat!"));
                    return ItemInteractionResult.FAIL;
                }

                baseEntity.setHasMat(true);
                baseEntity.setMatWashCycles(matItem.getWashCycles());
                baseEntity.setMatMaterialPoints(WashplantMatItem.getMaterialPoints(stack)); // Punkte laden

                PacketDistributor.sendToPlayersTrackingChunk((net.minecraft.server.level.ServerLevel) level,
                        new net.minecraft.world.level.ChunkPos(pos),
                        new SyncWashplantMatPacket(pos, true, matItem.getWashCycles(),
                                baseEntity.getMatMaterialPoints(), false)); // getMatMaterialPoints() statt 0

                if (!player.isCreative()) {
                    stack.shrink(1);
                }

                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aMat placed!"));
                return ItemInteractionResult.SUCCESS;
            }

            // Matte entfernen (Rechtsklick mit leerer Hand)
            if (stack.isEmpty() && player.isCrouching()) {
                if (!baseEntity.hasMat()) {
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cNo mat here!"));
                    return ItemInteractionResult.FAIL;
                }

                // Gib das richtige Item basierend auf Waschgängen zurück
                ItemStack matItem = getMatItemForWashCycles(baseEntity.getMatWashCycles());
                WashplantMatItem.setMaterialPoints(matItem, baseEntity.getMatMaterialPoints()); // Punkte speichern


                if (!player.getInventory().add(matItem)) {
                    player.drop(matItem, false);
                }

                baseEntity.removeMat();
                PacketDistributor.sendToPlayersTrackingChunk((net.minecraft.server.level.ServerLevel) level,
                        new net.minecraft.world.level.ChunkPos(pos),
                        new SyncWashplantMatPacket(pos, false, 0, 0, false));

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