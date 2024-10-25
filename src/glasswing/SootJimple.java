package glasswing;

import java.util.Collections;

import soot.BodyTransformer;
import soot.G;
import soot.Pack;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.options.Options;

public class SootJimple {
    public final static String androidJar = "/Users/syc/Library/Android/sdk/platforms";
    public final static String APK = "/Users/syc/Person/Paper/app-arm64-v8a-release.apk";
    public final static String OUTPUT_DIR = "/Users/syc/Person/Paper/jimple"; // 指定 Jimple 文件的输出目录

    public static void initSoot() {
        G.reset(); // 重置 Soot 的全局变量，确保每次运行都是干净的
        Options.v().set_allow_phantom_refs(true); // 允许伪类
        Options.v().set_prepend_classpath(true); // 将 VM 的类路径追加到 Soot 的类路径
        Options.v().set_output_format(Options.output_format_jimple); // 设置输出格式为 Jimple
        Options.v().set_android_jars(androidJar); // 设置 Android JAR 包路径
        Options.v().set_src_prec(Options.src_prec_apk); // 设置输入为 APK 文件
        Options.v().set_process_dir(Collections.singletonList(APK)); // 设置要处理的 APK 文件路径
        Options.v().set_force_overwrite(true); // 强制覆盖输出目录
        Options.v().set_output_dir(OUTPUT_DIR); // 设置 Jimple 文件的输出目录
        Scene.v().loadNecessaryClasses(); // 加载必要的类
    }

    public static void main(String[] args) {
        initSoot();
        // 运行 Soot 的所有包，不进行任何自定义转换
        PackManager.v().runPacks();
        // 写出输出
        PackManager.v().writeOutput();
    }
}