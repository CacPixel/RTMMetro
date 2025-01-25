package net.cacpixel.rtmmetro.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class ClassTransformerRtm implements IClassTransformer
{
    private final Logger logger = LogManager.getLogger("ClassTransformerRtm");

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        switch (transformedName)
        {
        case "jp.ngt.ngtlib.network.PacketCustom":
            return this.fixNGTLibPacketCustom(name, transformedName, basicClass);
        case "jp.ngt.rtm.modelpack.init.MPLAdButton":
//            return fixRTMPackLoadAds(name, transformedName, basicClass);
        default:
            return basicClass;
        }
    }

    private byte[] fixRTMPackLoadAds(String name, String transformedName, byte[] basicClass)
    {
        ClassNode cn = new ClassNode();
        new ClassReader(basicClass).accept(cn, 0);
        for (MethodNode mn : cn.methods)
        {
            String mappedMethodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(cn.name, mn.name, mn.desc);
            logger.info("test: mappedMethodName " + mappedMethodName);
            if (mappedMethodName.equals("getAds"))
            {
                logger.info("test: mappedMethodName in equals " + mappedMethodName);
                for (AbstractInsnNode ain : mn.instructions.toArray())
                {
                    if (ain.getOpcode() == Opcodes.DUP)
                    {
                        /*
                        new #97
                        dup
                        aconst_null
                        areturn
                        ...
                        */
                        InsnList list = new InsnList();
                        list.add(new InsnNode(Opcodes.ACONST_NULL));
                        list.add(new InsnNode(Opcodes.ARETURN));
                        mn.instructions.insert(ain, list);
                        break;
                    }
                }
            }
        }
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        cn.accept(cw);
//            try {
//                FileUtils.writeByteArrayToFile(new File("./MPLAdButton.class"), cw.toByteArray());
//            } catch (IOException ignored) {
//                ;
//            }
        return cw.toByteArray();
    }

    private byte[] fixNGTLibPacketCustom(String name, String transformedName, byte[] basicClass)
    {
        ClassNode cn = new ClassNode();
        new ClassReader(basicClass).accept(cn, 0);
        for (MethodNode mn : cn.methods)
        {
            String mappedMethodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(cn.name, mn.name, mn.desc);
            logger.info("test: mappedMethodName " + mappedMethodName);
            if (mappedMethodName.equals("<init>"))
            {
                logger.info("test: mappedMethodName in equals " + mappedMethodName);
                for (AbstractInsnNode ain : mn.instructions.toArray())
                {
                    if (ain.getOpcode() == Opcodes.IFGT)
                    { //ifgt succeeds if and only if value > 0
                        LabelNode label = ((JumpInsnNode) ain).label;
                        mn.instructions.set(ain, new JumpInsnNode(Opcodes.IFGE, label));
                    }
                }
            }
        }
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        cn.accept(cw);
//            try {
//                FileUtils.writeByteArrayToFile(new File("./PacketCustom.class"), cw.toByteArray());
//            } catch (IOException ignored) {
//                ;
//            }
        return cw.toByteArray();
    }

}
