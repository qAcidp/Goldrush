package net.qacidp.goldrush.block.bucket;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.qacidp.goldrush.block.ModBlocks;
import net.qacidp.goldrush.block.entity.PaydirtWaterBucketBlockEntity;
import net.qacidp.goldrush.item.ModItems;
import net.qacidp.goldrush.item.washplant.WashplantMatItem;
import org.jetbrains.annotations.Nullable;

public class PaydirtWaterBucketBlock extends BaseEntityBlock {

    private static final VoxelShape SHAPE = Shapes.or(
            box(3, 0, 3, 13, 1, 13),
            box(3, 1, 3, 4, 9, 13),
            box(12, 1, 3, 13, 9, 13),
            box(4, 1, 3, 12, 9, 4),
            box(4, 1, 12, 12, 9, 13),
            box(2, 9, 6, 3, 11, 10),
            box(13, 9, 6, 14, 11, 10)
    );

    public PaydirtWaterBucketBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PaydirtWaterBucketBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                           Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (!(entity instanceof PaydirtWaterBucketBlockEntity bucketEntity)) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            if (stack.getItem() instanceof WashplantMatItem) {
                int matPoints = WashplantMatItem.getMaterialPoints(stack);
                if (matPoints <= 0) {
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cThis mat is clean!"));
                    return ItemInteractionResult.FAIL;
                }

                int currentPoints = bucketEntity.getGoldPoints();

                // Eimer bereits voll
                if (currentPoints >= 1200) {
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cBucket is full!"));
                    return ItemInteractionResult.FAIL;
                }

                // Berechne wie viele Punkte tatsächlich aufgenommen werden
                int spaceLeft = 1200 - currentPoints;
                int pointsToAdd = Math.min(matPoints, spaceLeft);
                int pointsLeftOnMat = matPoints - pointsToAdd;

                bucketEntity.addGoldPoints(pointsToAdd);

                // Matte zurückgeben
                if (!player.isCreative()) {
                    stack.shrink(1);

                    ItemStack returnMat;
                    if (pointsLeftOnMat <= 0) {
                        // Matte komplett verbraucht — clean zurück
                        returnMat = new ItemStack(ModItems.WASHPLANT_MAT.get());
                    } else {
                        // Matte hat noch Punkte — gleiche Stufe aber reduzierte Punkte
                        returnMat = getMatItemForPoints(pointsLeftOnMat);
                        WashplantMatItem.setMaterialPoints(returnMat, pointsLeftOnMat);
                    }

                    if (!player.getInventory().add(returnMat)) {
                        player.drop(returnMat, false);
                    }
                }

                int newPoints = bucketEntity.getGoldPoints();
                Block newBlock;
                if (newPoints >= 600) {
                    newBlock = ModBlocks.PAYDIRT_WATER_BUCKET_BLOCK_FULL.get();
                } else if (newPoints > 0) {
                    newBlock = ModBlocks.PAYDIRT_WATER_BUCKET_BLOCK_HALF.get();
                } else {
                    newBlock = ModBlocks.PAYDIRT_WATER_BUCKET_BLOCK.get();
                }

                if (newBlock != state.getBlock()) {
                    int savedPoints = bucketEntity.getGoldPoints();
                    level.setBlock(pos, newBlock.defaultBlockState(), 3);
                    BlockEntity newEntity = level.getBlockEntity(pos);
                    if (newEntity instanceof PaydirtWaterBucketBlockEntity newBucketEntity) {
                        newBucketEntity.addGoldPoints(savedPoints);
                    }
                }

                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§aAdded " + pointsToAdd + " points! Total: " + newPoints + "/1200"));
                return ItemInteractionResult.SUCCESS;
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state,
                            @Nullable net.minecraft.world.entity.LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide) {
            int points = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                    .copyTag()
                    .getInt("goldPoints");

            Block newBlock;
            if (points >= 600) {
                newBlock = ModBlocks.PAYDIRT_WATER_BUCKET_BLOCK_FULL.get();
            } else if (points > 0) {
                newBlock = ModBlocks.PAYDIRT_WATER_BUCKET_BLOCK_HALF.get();
            } else {
                newBlock = ModBlocks.PAYDIRT_WATER_BUCKET_BLOCK.get();
            }

            if (newBlock != state.getBlock()) {
                level.setBlock(pos, newBlock.defaultBlockState(), 3);
            }

            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof PaydirtWaterBucketBlockEntity bucketEntity) {
                bucketEntity.addGoldPoints(points);
            }
        }
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state,
                              @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (!level.isClientSide && blockEntity instanceof PaydirtWaterBucketBlockEntity bucketEntity) {
            Block currentBlock = state.getBlock();
            ItemStack drop;
            if (currentBlock == ModBlocks.PAYDIRT_WATER_BUCKET_BLOCK_FULL.get()) {
                drop = new ItemStack(ModBlocks.PAYDIRT_WATER_BUCKET_BLOCK_FULL.get());
            } else if (currentBlock == ModBlocks.PAYDIRT_WATER_BUCKET_BLOCK_HALF.get()) {
                drop = new ItemStack(ModBlocks.PAYDIRT_WATER_BUCKET_BLOCK_HALF.get());
            } else {
                drop = new ItemStack(ModBlocks.PAYDIRT_WATER_BUCKET_BLOCK.get());
            }
            CustomData.update(DataComponents.CUSTOM_DATA, drop,
                    tag -> tag.putInt("goldPoints", bucketEntity.getGoldPoints()));
            popResource(level, pos, drop);
        }
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }

    @Override
    public MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(PaydirtWaterBucketBlock::new);
    }

    private ItemStack getMatItemForPoints(int points) {
        if (points >= 500) return new ItemStack(ModItems.WASHPLANT_MAT_HEAVY.get());
        else if (points >= 300) return new ItemStack(ModItems.WASHPLANT_MAT_MEDIUM.get());
        else if (points >= 100) return new ItemStack(ModItems.WASHPLANT_MAT_LIGHT.get());
        else return new ItemStack(ModItems.WASHPLANT_MAT.get());
    }
}