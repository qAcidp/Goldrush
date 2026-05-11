package net.qacidp.goldrush.block.paydirt.nugget;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.qacidp.goldrush.block.entity.PaydirtBlockEntity;
import net.qacidp.goldrush.util.PaydirtConfig;
import org.jetbrains.annotations.Nullable;

public class PaydirtNuggetBlock extends BaseEntityBlock {

    public static final IntegerProperty LAYERS = IntegerProperty.create("layers", 1, 8);

    private static final VoxelShape[] SHAPES = new VoxelShape[]{
            Shapes.empty(),
            box(0, 0, 0, 16, 2, 16),
            box(0, 0, 0, 16, 4, 16),
            box(0, 0, 0, 16, 6, 16),
            box(0, 0, 0, 16, 8, 16),
            box(0, 0, 0, 16, 10, 16),
            box(0, 0, 0, 16, 12, 16),
            box(0, 0, 0, 16, 14, 16),
            box(0, 0, 0, 16, 16, 16)
    };

    private final String paydirtType;

    public PaydirtNuggetBlock(Properties properties, String paydirtType) {
        super(properties);
        this.paydirtType = paydirtType;
        this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, 8));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(LAYERS);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(LAYERS)];
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(LAYERS)];
    }

    @Override
    public MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(props -> new PaydirtNuggetBlock(props, paydirtType));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        PaydirtBlockEntity entity = new PaydirtBlockEntity(pos, state);

        // Initialisiere direkt hier
        float totalGold = PaydirtConfig.generateGoldAmount(paydirtType);
        float[] goldDistribution = PaydirtConfig.getGoldDistribution(paydirtType, totalGold);
        entity.setGoldDistribution(goldDistribution);

        float nuggetGold = PaydirtConfig.generateGoldAmount(paydirtType) * 0.5f;
        entity.setNuggetGold(nuggetGold);

        // Zufälliger Layer zwischen 1 und 7
        java.util.Random random = new java.util.Random();
        int randomLayer = 1 + random.nextInt(7);
        entity.setNuggetLayer(randomLayer);

        return entity;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);

        if (!level.isClientSide && !oldState.is(this)) {  // Nur bei erstem Platzieren
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof PaydirtBlockEntity paydirtEntity) {
                // Prüfe ob schon initialisiert
                if (paydirtEntity.getNuggetLayer() == -1) {
                    float totalGold = PaydirtConfig.generateGoldAmount(paydirtType);
                    float[] goldDistribution = PaydirtConfig.getGoldDistribution(paydirtType, totalGold);
                    paydirtEntity.setGoldDistribution(goldDistribution);

                    float nuggetGold = PaydirtConfig.generateGoldAmount(paydirtType) * 0.5f;
                    paydirtEntity.setNuggetGold(nuggetGold);

                    // Zufälliger Layer zwischen 1 und 7
                    int randomLayer = 1 + level.random.nextInt(7);
                    paydirtEntity.setNuggetLayer(randomLayer);

                    paydirtEntity.setChanged();

                    // BlockEntity manuell speichern
                    level.sendBlockUpdated(pos, state, state, 3);
                }
            }
        }
    }


}