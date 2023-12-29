package org.magmafoundation.magma.patcher.impl;

import org.magmafoundation.magma.patcher.Patcher;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.function.Consumer;

@Patcher.PatcherInfo(name = "FastAsyncWorldEdit", description = "Patches FAWE")
public class FAWEPatcher extends Patcher {
    @Override
    public byte[] transform(String className, byte[] basicClass) {
        Consumer<ClassNode> patcher = switch (className) {
            case "com.sk89q.worldedit.bukkit.BukkitAdapter" -> WorldEditPatcher::handleBukkitAdapter;
            case "com.sk89q.worldedit.bukkit.adapter.Refraction" -> FAWEPatcher::handlePickName;
            default -> null;
        };
        return patcher == null ? basicClass : patch(basicClass, patcher);
    }

    private static byte[] patch(byte[] basicClass, Consumer<ClassNode> handler) {
        ClassNode node = new ClassNode();
        new ClassReader(basicClass).accept(node, 0);
        handler.accept(node);
        ClassWriter writer = new ClassWriter(0);
        node.accept(writer);
        return writer.toByteArray();
    }

    private static void handlePickName(ClassNode node) {
        for (MethodNode method : node.methods) {
            if (method.name.equals("pickName")) {
                method.instructions.clear();
                method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                method.instructions.add(new InsnNode(Opcodes.ARETURN));
                return;
            }
        }
    }
}
