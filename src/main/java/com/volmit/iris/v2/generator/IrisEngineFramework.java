package com.volmit.iris.v2.generator;

import com.volmit.iris.Iris;
import com.volmit.iris.manager.IrisDataManager;
import com.volmit.iris.object.IrisBiome;
import com.volmit.iris.object.IrisObjectPlacement;
import com.volmit.iris.util.B;
import com.volmit.iris.util.IObjectPlacer;
import com.volmit.iris.util.RNG;
import com.volmit.iris.v2.generator.actuator.IrisBiomeActuator;
import com.volmit.iris.v2.generator.actuator.IrisDecorantActuator;
import com.volmit.iris.v2.generator.actuator.IrisTerrainActuator;
import com.volmit.iris.v2.generator.modifier.IrisCaveModifier;
import com.volmit.iris.v2.scaffold.engine.Engine;
import com.volmit.iris.v2.scaffold.engine.EngineActuator;
import com.volmit.iris.v2.scaffold.engine.EngineFramework;
import com.volmit.iris.v2.scaffold.engine.EngineParallax;
import lombok.Getter;
import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;

public class IrisEngineFramework implements EngineFramework {

    @Getter
    private final Engine engine;

    @Getter
    private final IrisCaveModifier caveModifier;

    @Getter
    private final IrisComplex complex;

    @Getter
    final EngineParallax engineParallax;

    @Getter
    private final EngineActuator<BlockData> terrainActuator;

    @Getter
    private final EngineActuator<BlockData> decorantActuator;

    @Getter
    private final EngineActuator<Biome> biomeActuator;

    public IrisEngineFramework(Engine engine)
    {
        this.engine = engine;
        this.caveModifier = new IrisCaveModifier(engine);
        this.complex = new IrisComplex(getEngine());
        this.engineParallax = new IrisEngineParallax(getEngine());
        this.terrainActuator = new IrisTerrainActuator(getEngine());
        this.decorantActuator = new IrisDecorantActuator(getEngine());
        this.biomeActuator = new IrisBiomeActuator(getEngine());
    }
}
