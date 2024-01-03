package org.magmafoundation.magma.asm;

import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.function.Function;

/**
 * SwitchTableFixer
 *
 * @author Mainly by IzzelAliz and primary modified by JustRed23
 * @classFrom <a href=
 *            "https://github.com/IzzelAliz/Arclight/blob/1.18/arclight-forge/src/main/java/io/izzel/arclight/boot/asm/SwitchTableFixer.java">Click
 *            here to get to github</a>
 *
 *            This classes is modified by Magma to support the Magma software.
 */

public class SwitchTableFixer implements Implementer, Function<byte[], byte[]> {

    public static final Logger LOGGER = LoggerFactory.getLogger(SwitchTableFixer.class);

    public static final SwitchTableFixer INSTANCE = new SwitchTableFixer();
    private static final Marker MARKER = MarkerFactory.getMarker("SWITCH_TABLE");
    private static final Set<String> ENUMS = EnumDefinalizer.ENUM;

    @Override
    public byte[] apply(byte[] bytes) {
        ClassNode node = new ClassNode();
        new ClassReader(bytes).accept(node, 0);
        processClass(node, null);
        ClassWriter writer = new ClassWriter(0);
        node.accept(writer);
        return writer.toByteArray();
    }

    @Override
    public boolean processClass(ClassNode node, ILaunchPluginService.ITransformerLoader transformerLoader) {
        boolean success = false;
        for (MethodNode method : node.methods) {
            // There are two variants of switch map
            if (inject1(node, method)) {
                success = true;
            } else if (inject2(node, method)) {
                success = true;
            }
        }
        return success;
    }

    private boolean inject1(ClassNode node, MethodNode method) {
        if (Modifier.isStatic(method.access) && (method.access & Opcodes.ACC_SYNTHETIC) != 0 && method.desc.equals("()[I")) {
            boolean foundTryCatch = false;
            for (TryCatchBlockNode tryCatchBlock : method.tryCatchBlocks) {
                if ("java/lang/NoSuchFieldError".equals(tryCatchBlock.type)) {
                    foundTryCatch = true;
                } else return false;
            }
            if (!foundTryCatch) return false;
            LOGGER.debug(MARKER, "Candidate switch enum method {} class {}", method.name + method.desc, node.name);
            FieldInsnNode fieldInsnNode = null;
            String enumType = null;
            for (AbstractInsnNode insnNode : method.instructions) {
                if (enumType != null) {
                    break;
                } else {
                    if (insnNode.getOpcode() == Opcodes.GETSTATIC && ((FieldInsnNode) insnNode).desc.equals("[I")) {
                        fieldInsnNode = ((FieldInsnNode) insnNode);
                    }
                    if (insnNode.getOpcode() == Opcodes.INVOKESTATIC && ((MethodInsnNode) insnNode).name.equals("values")) {
                        Type methodType = Type.getMethodType(((MethodInsnNode) insnNode).desc);
                        Type returnType = methodType.getReturnType();
                        if (returnType.getSort() == Type.ARRAY && returnType.getDimensions() == 1) {
                            String retType = returnType.getElementType().getInternalName();
                            if (ENUMS.contains(retType)) {
                                AbstractInsnNode next = insnNode.getNext();
                                if (next.getOpcode() == Opcodes.ARRAYLENGTH) {
                                    AbstractInsnNode newArray = next.getNext();
                                    if (newArray.getOpcode() == Opcodes.NEWARRAY && ((IntInsnNode) newArray).operand == Opcodes.T_INT) {
                                        enumType = retType;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (fieldInsnNode != null && enumType != null) {
                LOGGER.debug(MARKER, "Find switch(enum {}) table method {} in class {}", enumType, method.name + method.desc, node.name);
                AbstractInsnNode last = method.instructions.getLast();
                while (last != null && last.getOpcode() != Opcodes.ARETURN) {
                    last = last.getPrevious();
                }
                if (last == null) return false;
                InsnList list = new InsnList();
                list.add(new LdcInsnNode(Type.getObjectType(enumType)));
                list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(SwitchTableFixer.class), "fillSwitchTable1", "([ILjava/lang/Class;)[I", false));
                list.add(new InsnNode(Opcodes.DUP));
                list.add(new FieldInsnNode(Opcodes.PUTSTATIC, fieldInsnNode.owner, fieldInsnNode.name, fieldInsnNode.desc));
                method.instructions.insertBefore(last, list);
                LOGGER.debug(MARKER, "Inject method in method {}:{}, switch table field is {}", node.name, method.name + method.desc, fieldInsnNode.name + fieldInsnNode.desc);
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unused")
    public static int[] fillSwitchTable1(int[] arr, Class<? extends Enum<?>> cl) {
        LOGGER.debug(MARKER, "Filling switch table for {}", cl);
        Enum<?>[] enums = cl.getEnumConstants();
        if (arr.length < enums.length) {
            int[] ints = new int[enums.length];
            System.arraycopy(arr, 0, ints, 0, arr.length);
            arr = ints;
        }
        int i = -1;
        for (int j : arr) {
            if (j > i) i = j;
        }
        if (i != -1) {
            for (int k = i; k < enums.length; k++) {
                arr[k] = enums[k].ordinal();
            }
        }
        return arr;
    }

    private boolean inject2(ClassNode node, MethodNode method) {
        if ((node.access & Opcodes.ACC_SYNTHETIC) != 0) {
            if (node.methods.size() == 1 && Modifier.isStatic(method.access) && method.name.equals("<clinit>")) {
                boolean foundTryCatch = false;
                for (TryCatchBlockNode tryCatchBlock : method.tryCatchBlocks) {
                    if ("java/lang/NoSuchFieldError".equals(tryCatchBlock.type)) {
                        foundTryCatch = true;
                    } else return false;
                }
                if (!foundTryCatch) return false;
                LOGGER.debug(MARKER, "Candidate switch enum method {} class {}", method.name + method.desc, node.name);
                FieldInsnNode fieldInsnNode = null;
                String enumType = null;
                for (AbstractInsnNode insnNode : method.instructions) {
                    if (insnNode.getOpcode() == Opcodes.INVOKESTATIC && ((MethodInsnNode) insnNode).name.equals("values")) {
                        Type methodType = Type.getMethodType(((MethodInsnNode) insnNode).desc);
                        Type returnType = methodType.getReturnType();
                        if (returnType.getSort() == Type.ARRAY && returnType.getDimensions() == 1) {
                            String retType = returnType.getElementType().getInternalName();
                            if (ENUMS.contains(retType)) {
                                AbstractInsnNode next = insnNode.getNext();
                                if (next.getOpcode() == Opcodes.ARRAYLENGTH) {
                                    AbstractInsnNode newArray = next.getNext();
                                    if (newArray.getOpcode() == Opcodes.NEWARRAY && ((IntInsnNode) newArray).operand == Opcodes.T_INT) {
                                        AbstractInsnNode putStatic = newArray.getNext();
                                        if (putStatic.getOpcode() == Opcodes.PUTSTATIC && ((FieldInsnNode) putStatic).desc.equals("[I")) {
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
                    LOGGER.debug(MARKER, "Find switch(enum {}) table method {} in class {}", enumType, method.name + method.desc, node.name);
                    AbstractInsnNode last = method.instructions.getLast();
                    while (last != null && last.getOpcode() != Opcodes.RETURN) {
                        last = last.getPrevious();
                    }
                    if (last == null) return false;
                    InsnList list = new InsnList();
                    list.add(new FieldInsnNode(Opcodes.GETSTATIC, fieldInsnNode.owner, fieldInsnNode.name, fieldInsnNode.desc));
                    list.add(new LdcInsnNode(Type.getObjectType(enumType)));
                    list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(SwitchTableFixer.class), "fillSwitchTable2", "([ILjava/lang/Class;)[I", false));
                    list.add(new FieldInsnNode(Opcodes.PUTSTATIC, fieldInsnNode.owner, fieldInsnNode.name, fieldInsnNode.desc));
                    method.instructions.insertBefore(last, list);
                    LOGGER.debug(MARKER, "Inject method in method {}:{}, switch table field is {}", node.name, method.name + method.desc, fieldInsnNode.name + fieldInsnNode.desc);
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("unused")
    public static int[] fillSwitchTable2(int[] arr, Class<? extends Enum<?>> cl) {
        LOGGER.debug(MARKER, "Filling switch table for {}", cl);
        Enum<?>[] enums = cl.getEnumConstants();
        if (arr.length < enums.length) {
            int[] ints = new int[enums.length];
            System.arraycopy(arr, 0, ints, 0, arr.length);
            arr = ints;
        }
        return arr;
    }
}
