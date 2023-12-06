package org.magmafoundation.magma.util;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;

import java.util.Set;
import java.util.concurrent.Executor;

public class MagmaDataFixerBuilder extends DataFixerBuilder {
    private static final Executor NO_OP_EXECUTOR = command -> {};

    public MagmaDataFixerBuilder(int dataVersion) {
        super(dataVersion);
    }

    @Override
    public DataFixer buildOptimized(Set<DSL.TypeReference> requiredTypes, Executor executor) {
        return super.buildOptimized(requiredTypes, NO_OP_EXECUTOR);
    }
}
