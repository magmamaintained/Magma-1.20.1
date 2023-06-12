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

package org.magmafoundation.magma.asm;

import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import java.lang.reflect.Modifier;
import java.util.EnumSet;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

/**
 * SwitchTableFixer
 *
 * @author Mainly by IzzelAliz and modified by Sm0keySa1m0n
 * @classFrom <a href=
 *            "https://github.com/IzzelAliz/Arclight/blob/1.18/arclight-forge/src/main/java/io/izzel/arclight/boot/asm/SwitchTableFixer.java">Click
 *            here to get to github</a>
 *
 *            This classes is modified by Magma to support the Magma software.
 */
public class SwitchTableFixer implements ILaunchPluginService {

    private static final EnumSet<Phase> YAY = EnumSet.of(Phase.AFTER);
    private static final EnumSet<Phase> NAY = EnumSet.noneOf(Phase.class);

    private static final Logger logger = LogManager.getLogger("SwitchTableFixer");

    private static final Set<String> ENUMS = Set.of(
            "org/bukkit/Material",
            "org/bukkit/potion/PotionType",
            "org/bukkit/entity/EntityType",
            "org/bukkit/entity/Villager$Profession",
            "org/bukkit/block/Biome",
            "org/bukkit/Art",
            "org/bukkit/Statistic",
            "org/bukkit/inventory/CreativeCategory",
            "org/bukkit/entity/SpawnCategory",
            "org/bukkit/entity/EnderDragon$Phase");

    @Override
    public String name() {
        return "switch_table_fixer";
    }

    @Override
    public EnumSet<Phase> handlesClass(Type classType, boolean isEmpty) {
        return isEmpty ? NAY : YAY;
    }

    @Override
    public boolean processClass(Phase phase, ClassNode classNode, Type classType, String reason) {
        return handleClass(classNode);
    }

    public static boolean handleClass(ClassNode node) {
        var success = false;
        for (var method : node.methods) {
            // There are two variants of switch map
            if (inject1(node, method) || inject2(node, method)) {
                success = true;
            }
        }
        return success;
    }

    private static boolean inject1(ClassNode node, MethodNode method) {
        if (Modifier.isStatic(method.access) && (method.access & Opcodes.ACC_SYNTHETIC) != 0
                && method.desc.equals("()[I")) {
            boolean foundTryCatch = false;
            for (TryCatchBlockNode tryCatchBlock : method.tryCatchBlocks) {
                if ("java/lang/NoSuchFieldError".equals(tryCatchBlock.type)) {
                    foundTryCatch = true;
                } else
                    return false;
            }
            if (!foundTryCatch)
                return false;
            logger.debug("Candidate switch enum method {} class {}", method.name + method.desc, node.name);
            FieldInsnNode fieldInsnNode = null;
            String enumType = null;
            for (AbstractInsnNode insnNode : method.instructions) {
                if (enumType != null) {
                    break;
                } else {
                    if (insnNode.getOpcode() == Opcodes.GETSTATIC && ((FieldInsnNode) insnNode).desc.equals("[I")) {
                        fieldInsnNode = ((FieldInsnNode) insnNode);
                    }
                    if (insnNode.getOpcode() == Opcodes.INVOKESTATIC
                            && ((MethodInsnNode) insnNode).name.equals("values")) {
                        Type methodType = Type.getMethodType(((MethodInsnNode) insnNode).desc);
                        Type returnType = methodType.getReturnType();
                        if (returnType.getSort() == Type.ARRAY && returnType.getDimensions() == 1) {
                            String retType = returnType.getElementType().getInternalName();
                            if (ENUMS.contains(retType)) {
                                AbstractInsnNode next = insnNode.getNext();
                                if (next.getOpcode() == Opcodes.ARRAYLENGTH) {
                                    AbstractInsnNode newArray = next.getNext();
                                    if (newArray.getOpcode() == Opcodes.NEWARRAY
                                            && ((IntInsnNode) newArray).operand == Opcodes.T_INT) {
                                        enumType = retType;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (fieldInsnNode != null && enumType != null) {
                logger.debug("Find switch(enum {}) table method {} in class {}", enumType, method.name + method.desc,
                        node.name);
                AbstractInsnNode last = method.instructions.getLast();
                while (last != null && last.getOpcode() != Opcodes.ARETURN) {
                    last = last.getPrevious();
                }
                if (last == null)
                    return false;
                InsnList list = new InsnList();
                list.add(new LdcInsnNode(Type.getObjectType(enumType)));
                list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(SwitchTableFixer.class),
                        "fillSwitchTable1", "([ILjava/lang/Class;)[I", false));
                list.add(new InsnNode(Opcodes.DUP));
                list.add(new FieldInsnNode(Opcodes.PUTSTATIC, fieldInsnNode.owner, fieldInsnNode.name,
                        fieldInsnNode.desc));
                method.instructions.insertBefore(last, list);
                logger.debug("Inject method in method {}:{}, switch table field is {}", node.name,
                        method.name + method.desc, fieldInsnNode.name + fieldInsnNode.desc);
                return true;
            }
        }
        return false;
    }

    public static int[] fillSwitchTable1(int[] arr, Class<? extends Enum<?>> cl) {
        logger.debug("Filling switch table for {}", cl);
        Enum<?>[] enums = cl.getEnumConstants();
        if (arr.length < enums.length) {
            int[] ints = new int[enums.length];
            System.arraycopy(arr, 0, ints, 0, arr.length);
            arr = ints;
        }
        int i = -1;
        for (int j : arr) {
            if (j > i)
                i = j;
        }
        if (i != -1) {
            for (int k = i; k < enums.length; k++) {
                arr[k] = enums[k].ordinal();
            }
        }
        return arr;
    }

    private static boolean inject2(ClassNode node, MethodNode method) {
        if ((node.access & Opcodes.ACC_SYNTHETIC) != 0) {
            if (node.methods.size() == 1 && Modifier.isStatic(method.access) && method.name.equals("<clinit>")) {
                boolean foundTryCatch = false;
                for (TryCatchBlockNode tryCatchBlock : method.tryCatchBlocks) {
                    if ("java/lang/NoSuchFieldError".equals(tryCatchBlock.type)) {
                        foundTryCatch = true;
                    } else
                        return false;
                }
                if (!foundTryCatch)
                    return false;
                logger.debug("Candidate switch enum method {} class {}", method.name + method.desc, node.name);
                FieldInsnNode fieldInsnNode = null;
                String enumType = null;
                for (AbstractInsnNode insnNode : method.instructions) {
                    if (insnNode.getOpcode() == Opcodes.INVOKESTATIC
                            && ((MethodInsnNode) insnNode).name.equals("values")) {
                        Type methodType = Type.getMethodType(((MethodInsnNode) insnNode).desc);
                        Type returnType = methodType.getReturnType();
                        if (returnType.getSort() == Type.ARRAY && returnType.getDimensions() == 1) {
                            String retType = returnType.getElementType().getInternalName();
                            if (ENUMS.contains(retType)) {
                                AbstractInsnNode next = insnNode.getNext();
                                if (next.getOpcode() == Opcodes.ARRAYLENGTH) {
                                    AbstractInsnNode newArray = next.getNext();
                                    if (newArray.getOpcode() == Opcodes.NEWARRAY
                                            && ((IntInsnNode) newArray).operand == Opcodes.T_INT) {
                                        AbstractInsnNode putStatic = newArray.getNext();
                                        if (putStatic.getOpcode() == Opcodes.PUTSTATIC
                                                && ((FieldInsnNode) putStatic).desc.equals("[I")) {
                                            enumType = retType;
                                            fieldInsnNode = ((FieldInsnNode) putStatic);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (fieldInsnNode != null) {
                    logger.debug("Find switch(enum {}) table method {} in class {}", enumType,
                            method.name + method.desc, node.name);
                    AbstractInsnNode last = method.instructions.getLast();
                    while (last != null && last.getOpcode() != Opcodes.RETURN) {
                        last = last.getPrevious();
                    }
                    if (last == null)
                        return false;
                    InsnList list = new InsnList();
                    list.add(new FieldInsnNode(Opcodes.GETSTATIC, fieldInsnNode.owner, fieldInsnNode.name,
                            fieldInsnNode.desc));
                    list.add(new LdcInsnNode(Type.getObjectType(enumType)));
                    list.add(
                            new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(SwitchTableFixer.class),
                                    "fillSwitchTable2", "([ILjava/lang/Class;)[I", false));
                    list.add(new FieldInsnNode(Opcodes.PUTSTATIC, fieldInsnNode.owner, fieldInsnNode.name,
                            fieldInsnNode.desc));
                    method.instructions.insertBefore(last, list);
                    logger.debug("Inject method in method {}:{}, switch table field is {}", node.name,
                            method.name + method.desc, fieldInsnNode.name + fieldInsnNode.desc);
                    return true;
                }
            }
        }
        return false;
    }

    public static int[] fillSwitchTable2(int[] arr, Class<? extends Enum<?>> cl) {
        logger.debug("Filling switch table for {}", cl);
        Enum<?>[] enums = cl.getEnumConstants();
        if (arr.length < enums.length) {
            int[] ints = new int[enums.length];
            System.arraycopy(arr, 0, ints, 0, arr.length);
            arr = ints;
        }
        return arr;
    }
}
