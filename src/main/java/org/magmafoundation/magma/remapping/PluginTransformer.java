package org.magmafoundation.magma.remapping;

import org.objectweb.asm.tree.ClassNode;

public interface PluginTransformer {

    void handleClass(ClassNode node, ClassLoaderRemapper remapper);

    default int priority() {
        return 0;
    }
}
