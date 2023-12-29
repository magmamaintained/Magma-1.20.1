/*
 * Magma Server
 * Copyright (C) 2019-2023.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.magmafoundation.magma.patcher.impl;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.magmafoundation.magma.patcher.Patcher;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.function.Consumer;

@Patcher.PatcherInfo(name = "MultiverseCore", description = "Patches MultiverseCore")
public class MultiverseCorePatcher extends Patcher {
    @Override
    public byte[] transform(String className, byte[] basicClass) {
        Consumer<ClassNode> patcher = switch (className) {
            case "com.onarandombox.MultiverseCore.utils.WorldManager" -> MultiverseCorePatcher::fix;
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

    private static void fix(ClassNode node) {
        for (MethodNode method : node.methods) {
            if (method.name.equals("doLoad") && method.desc.equals("(Ljava/lang/String;ZLorg/bukkit/WorldType;)Z")) {
                InsnList toInject = new InsnList();
                toInject.add(new VarInsnNode(Opcodes.ALOAD, 1));
                toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(MultiverseCorePatcher.class), "isLoad", "(Ljava/lang/String;)Z"));
                toInject.add(new VarInsnNode(Opcodes.ISTORE, 2));
                method.instructions.insert(toInject);
            }
        }
    }

    public static boolean isLoad(String name) {
        return Bukkit.getServer().getWorlds().stream().map(World::getName).toList().contains(name);
    }
}
