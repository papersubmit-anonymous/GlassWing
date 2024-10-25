package glasswing;

import soot.*;
import soot.options.Options;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.SimpleLocalDefs;
import soot.util.Chain;
import soot.dart.text.DartTypeFactory;
import soot.hermeser.text.HbcTypeFactory;
import soot.jimple.NewExpr;
import soot.jimple.StringConstant;
import soot.jimple.internal.JAssignStmt;
import soot.options.Options;
import soot.util.Chain;

import java.util.*;
import java.util.concurrent.*;

public class FlutterUtil {
    public static Boolean isDartModulesDetected = false;
    protected static Map<SootClass,Collection<SootMethod>> dartModules;
    protected static Map<SootClass,Collection<SootMethod>> flutterModules;
    protected static boolean isDartMethodSourced = false;
    public static Boolean isFlutterModulesDetected = false;
    public static SootClass dartClass;
    public static SootMethod flutterModulesEntryMethod;
    public static SootMethod dartModulesEntryMethod;
    public static int ChannelNumber;
    public static int MethodChannelNumber;
    public static int EventChannelNumber;
    public static int BasicChannelNumber;
    public static int ActivityBasicChannelNumber;
    public static int ActivityMethodChannelNumber;
    public static int ActivityEventChannelNumber;
    static Set<String> AllchannelSet = new HashSet<>();
    static Set<String> AlleventSet = new HashSet<>();
    static Set<String> AllmethodSet = new HashSet<>();
    static Set<String> AllbasicSet = new HashSet<>();
    static Set<String> ActchannelSet = new HashSet<>();
    static Set<String> ActeventSet = new HashSet<>();
    static Set<String> ActmethodSet = new HashSet<>();
    static Set<String> ActbasicSet = new HashSet<>();

    public static Map<SootClass, Collection<SootMethod>>  extractDartModules(){
        Map<SootClass, Collection<SootMethod>> outputs = new HashMap<SootClass, Collection<SootMethod>>();
        List<SootClass> flutterModulePossilbleClassList = new ArrayList<SootClass>();
        Hierarchy hierarchy = Scene.v().getActiveHierarchy();
        ArrayList<SootClass> flutterActivityClasses = new ArrayList<>();
        ArrayList<SootClass> flutterActivitySubClasses = new ArrayList<>();
        // 查找所有继承 FlutterActivity 的类
        if (Scene.v().containsType("io.flutter.embedding.android.FlutterActivity")) {
            SootClass flutterActivityClass = Scene.v().getSootClass("io.flutter.embedding.android.FlutterActivity");
            List<SootClass> flutterActivitySubClassList = hierarchy.getSubclassesOf(flutterActivityClass);
            flutterModulePossilbleClassList.addAll(flutterActivitySubClassList);
        }
        for (SootClass sootClass : flutterModulePossilbleClassList) {
            Collection<SootMethod> moduleMethods = extractMethodsFromClass(sootClass);
            outputs.put(sootClass, moduleMethods);
        }
        return outputs;
    }

    private static Collection<SootMethod> extractMethodsFromClass(SootClass sootClass) {
        // 提取类中的方法
        return sootClass.getMethods();
    }

    public static SootClass getDartClass(){
        if(isDartMethodSourced){
            return dartClass;
        }
        else{
            dartClass = extractDartClass();
            isDartMethodSourced = true;
            return dartClass;
        }
    }

    public static SootClass extractDartClass(){
        SootClass cl = Scene.v().getSootClass(DartTypeFactory.DART_HERMES);
        int threadNum = Options.v().coffi() ? 1 : Runtime.getRuntime().availableProcessors();
        System.out.println("threadNum: " + threadNum);
        ExecutorService executor = new ThreadPoolExecutor(threadNum, threadNum, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (SootMethod m : new ArrayList<SootMethod>(cl.getMethods())) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println(m.getName());
                m.retrieveActiveBody();
                // Do some work asynchronously
            }, executor).thenRunAsync(() -> {
                m.getActiveBody().validate();
                // Do some more work asynchronously
            }, executor);

            futures.add(future);
        }
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            executor.shutdown();
        }
        return cl;
    }

    public static Map<SootClass, Collection<SootMethod>> getFlutterModules() {
        if(isFlutterModulesDetected){
            return flutterModules;
        }
        else{
            flutterModules = extractFlutterModules();
            isFlutterModulesDetected = true;
            return flutterModules;
        }
    }

    // 打印类中的所有方法
    private static void printMethods(SootClass sootClass) {
        for (SootMethod method : sootClass.getMethods()) {
            System.out.println("        Method: " + method.getSignature());
        }
    }

    public static Map<SootClass, Collection<SootMethod>> extractFlutterModules(){
        Map<SootClass, Collection<SootMethod>> outputs = new HashMap<SootClass, Collection<SootMethod>>();
        List<SootClass> flutterModulePossilbleClassList = new ArrayList<SootClass>();
        // 打印 Scene 中的所有类
        for (SootClass sootClass : Scene.v().getClasses()) {
            System.out.println(sootClass.getName());
        }
        // 存储符合条件的类
        List<SootClass> flutterClasses = new ArrayList<>();
        List<SootClass> channelClasses = new ArrayList<>();
        List<SootClass> EventchannelClasses = new ArrayList<>();
        List<SootClass> MethodchannelClasses = new ArrayList<>();
        List<SootClass> BasicchannelClasses = new ArrayList<>();
        List<SootClass> AllTopClasses = new ArrayList<>();

        Hierarchy hierarchy = Scene.v().getActiveHierarchy();
        int tmpchannel = 0;
        int tmpevenchannel = 0;
        int tmpmethodchannel = 0 ;

        Set<String> mySet = new HashSet<>();
        Set<String> eventSet = new HashSet<>();
        Set<String> methodSet = new HashSet<>();
        Set<String> basicSet = new HashSet<>();

        // 遍历 Scene 中的所有类
        for (SootClass sootClass : Scene.v().getClasses()) {
            if (sootClass.getName().equals("flutter")){
                flutterClasses.add(sootClass);
            }

            // 遍历类中的所有方法
            for (SootMethod method : sootClass.getMethods()) {
                if (method.isConcrete()) { // 确保方法有一个具体的实现
                    // 获取方法的Jimple体
                    Chain<Unit> units = method.retrieveActiveBody().getUnits();
                    // 遍历所有指令单元
                    for (soot.Unit unit : units) {
                        // 检查每个单元是否包含StringConstant
                        for (soot.ValueBox valueBox : unit.getUseBoxes()) {
                            soot.Value value = valueBox.getValue();
                            if (value instanceof StringConstant) {
                                StringConstant stringConst = (StringConstant) value;
                                if (stringConst.value.contains("FlutterPlugin")) {

                                }
                                if (stringConst.value.contains("FlutterActivity")) {
                                    System.out.println("在Class " + sootClass.getName() + "在方法 " + method.getSignature() + " 中找到字符串常量: " + stringConst.value);
                                    flutterClasses.add(sootClass);
                                    SootClass currentClass =  sootClass;
                                    currentClass.setApplicationClass();
                                    // 逐步查找并打印所有外部类，直到没有外部类为止
                                    while (currentClass.hasOuterClass()) {
                                        SootClass outerClass = currentClass.getOuterClass();
                                        System.out.println("Current class: " + currentClass.getName() + ", Outer class: " + outerClass.getName());
                                        currentClass = outerClass; // 将外部类设置为当前类，继续查找
                                    }
                                    // 最后的 currentClass 应该是顶层类 a0
                                    System.out.println("MethodChannel Top level class: " + currentClass.getName());
                                    //MethodchannelClasses.add(currentClass);
                                    AllTopClasses.add(currentClass);

                                    //methodSet.add(currentClass.getName());
                                }
                                if (stringConst.value.contains("MethodChannel#")) {
                                    System.out.println("在Class " + sootClass.getName() + "在方法 " + method.getSignature() + " 中找到字符串常量: " + stringConst.value);
                                    flutterClasses.add(sootClass);
                                    //channelClasses.add(sootClass);
                                    SootClass currentClass =  sootClass;
                                    currentClass.setApplicationClass();
                                    // 逐步查找并打印所有外部类，直到没有外部类为止
                                    while (currentClass.hasOuterClass()) {
                                        SootClass outerClass = currentClass.getOuterClass();
                                        System.out.println("Current class: " + currentClass.getName() + ", Outer class: " + outerClass.getName());
                                        currentClass = outerClass; // 将外部类设置为当前类，继续查找
                                    }
                                    // 最后的 currentClass 应该是顶层类 a0
                                    System.out.println("MethodChannel Top level class: " + currentClass.getName());
                                    MethodchannelClasses.add(currentClass);
                                    AllTopClasses.add(currentClass);

                                    methodSet.add(currentClass.getName());
                                }

                                if (stringConst.value.contains("flutter")) {
                                    System.out.println("在Class " + sootClass.getName() + "在方法 " + method.getSignature() + " 中找到字符串常量: " + stringConst.value);
                                    flutterClasses.add(sootClass);
                                }
                                if (stringConst.value.contains("Flutter")) {
                                    System.out.println("在Class " + sootClass.getName() + "在方法 " + method.getSignature() + " 中找到字符串常量: " + stringConst.value);
                                    flutterClasses.add(sootClass);
                                }
                                if (stringConst.value.contains("EventChannel")) {
                                    System.out.println("在Class " + sootClass.getName() + "在方法 " + method.getSignature() + " 中找到字符串常量: " + stringConst.value);
                                    flutterClasses.add(sootClass);
                                    //channelClasses.add(sootClass);

                                    SootClass currentClass =  sootClass;
                                    currentClass.setApplicationClass();

                                    // 逐步查找并打印所有外部类，直到没有外部类为止
                                    while (currentClass.hasOuterClass()) {
                                        SootClass outerClass = currentClass.getOuterClass();
                                        System.out.println("Current class: " + currentClass.getName() + ", Outer class: " + outerClass.getName());
                                        currentClass = outerClass; // 将外部类设置为当前类，继续查找
                                    }
                                    // 最后的 currentClass 应该是顶层类 a0
                                    System.out.println("EventChannel Top level class: " + currentClass.getName());
                                    EventchannelClasses.add(currentClass);
                                    AllTopClasses.add(currentClass);
                                    eventSet.add(currentClass.getName());

                                }if (stringConst.value.contains("Channel")) {
                                    System.out.println("在Class " + sootClass.getName() + "在方法 " + method.getSignature() + " 中找到字符串常量: " + stringConst.value);
                                    flutterClasses.add(sootClass);
                                    //channelClasses.add(sootClass);
                                    SootClass currentClass =  sootClass;
                                    currentClass.setApplicationClass();

                                    // 逐步查找并打印所有外部类，直到没有外部类为止
                                    while (currentClass.hasOuterClass()) {
                                        SootClass outerClass = currentClass.getOuterClass();
                                        System.out.println("Current class: " + currentClass.getName() + ", Outer class: " + outerClass.getName());
                                        currentClass = outerClass; // 将外部类设置为当前类，继续查找
                                    }
                                    // 最后的 currentClass 应该是顶层类 a0
                                    System.out.println("Top level class: " + currentClass.getName());
                                    channelClasses.add(currentClass);
                                    mySet.add(currentClass.getName());
                                    tmpchannel = tmpchannel + 1;

                                }if (stringConst.value.contains("BasicMessageChannel")) {
                                    System.out.println("在Class " + sootClass.getName() + "在方法 " + method.getSignature() + " 中找到字符串常量: " + stringConst.value);
                                    SootClass currentClass =  sootClass;
                                    currentClass.setApplicationClass();

                                    // 逐步查找并打印所有外部类，直到没有外部类为止
                                    while (currentClass.hasOuterClass()) {
                                        SootClass outerClass = currentClass.getOuterClass();
                                        System.out.println("Current class: " + currentClass.getName() + ", Outer class: " + outerClass.getName());
                                        currentClass = outerClass; // 将外部类设置为当前类，继续查找
                                    }
                                    // 最后的 currentClass 应该是顶层类 a0
                                    System.out.println("BasicMessageChannel Top level class: " + currentClass.getName());
                                    BasicchannelClasses.add(currentClass);
                                    AllTopClasses.add(currentClass);
                                    basicSet.add(currentClass.getName());
                                }
                            }
                        }
                    }
                }
            }
        }
        int teal = 0;

        /*
        // 添加类及其子类的所有方法到outputs
        for (SootClass flutterClass : channelClasses) {
            Set<SootMethod> methodsSet = new HashSet<>(flutterClass.getMethods());
            // 获取所有子类
            List<SootClass> subClasses = hierarchy.getSubclassesOf(flutterClass);
            for (SootClass subClass : subClasses) {
                teal = teal + 1;
                methodsSet.addAll(subClass.getMethods());
            }
            outputs.put(flutterClass, methodsSet);
        }*/


        // 添加类及其子类的所有方法到outputs
        System.out.println("====== MethodchannelClasses ======");
        for (SootClass flutterClass : MethodchannelClasses) {
            int instantiated = checkClassInstantiation(flutterClass.getName(), "MethodChannel");
            MethodChannelNumber = MethodChannelNumber + instantiated;
            System.out.println(flutterClass.getName() + " Instantiation : " + instantiated);
        }

        // 添加类及其子类的所有方法到outputs
        System.out.println("====== EventchannelClasses ======");
        for (SootClass flutterClass : EventchannelClasses) {
            int instantiated = checkClassInstantiation(flutterClass.getName(), "EventChannel");
            EventChannelNumber = EventChannelNumber + instantiated;
            System.out.println(flutterClass.getName() + " Instantiation : " + instantiated);
        }

        System.out.println("====== BasicchannelClasses ======");
        for (SootClass flutterClass : BasicchannelClasses) {
            int instantiated = checkClassInstantiation(flutterClass.getName(), "BasicChannel");
            BasicChannelNumber = BasicChannelNumber + instantiated;
            System.out.println(flutterClass.getName() + " Instantiation : " + instantiated);
        }

        System.out.println("====== channelClasses ======");
        for (SootClass flutterClass : channelClasses) {
            int instantiated = checkClassInstantiation(flutterClass.getName());
            ChannelNumber = ChannelNumber + instantiated;
            System.out.println(flutterClass.getName() + " Instantiation : " + instantiated);
        }



        //ChannelNumber = mySet.size();
        //MethodChannelNumber = methodSet.size();
        //EventChannelNumber = eventSet.size();
        //BasicChannelNumber = basicSet.size();
        for (SootClass flutterClass : flutterClasses) {
            System.out.println(flutterClass.getName());
        }

        /*
        // 存储符合条件的类
        List<SootClass> flutterClasses = new ArrayList<>();
        List<SootClass> channelClasses = new ArrayList<>();
        List<SootClass> EventchannelClasses = new ArrayList<>();
        List<SootClass> MethodchannelClasses = new ArrayList<>();
        List<SootClass> BasicchannelClasses = new ArrayList<>();
        List<SootClass> AllTopClasses = new ArrayList<>();
        */


        // 添加类及其子类的所有方法到outputs
        for (SootClass flutterClass : flutterClasses) {
            Set<SootMethod> methodsSet = new HashSet<>(flutterClass.getMethods());
            // 获取所有子类
            List<SootClass> subClasses = hierarchy.getSubclassesOf(flutterClass);
            for (SootClass subClass : subClasses) {
                methodsSet.addAll(subClass.getMethods());
            }
            outputs.put(flutterClass, methodsSet);
        }

//        // 添加类及其子类的所有方法到outputs
//        for (SootClass flutterClass : channelClasses) {
//            Set<SootMethod> methodsSet = new HashSet<>(flutterClass.getMethods());
//            try{
//                // 获取所有子类
//                List<SootClass> subClasses = hierarchy.getSubclassesOf(flutterClass);
//                for (SootClass subClass : subClasses) {
//                    methodsSet.addAll(subClass.getMethods());
//                }
//                outputs.put(flutterClass, methodsSet);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//
//        }
//
//        // 添加类及其子类的所有方法到outputs
//        for (SootClass flutterClass : EventchannelClasses) {
//            Set<SootMethod> methodsSet = new HashSet<>(flutterClass.getMethods());
//            // 获取所有子类
//            List<SootClass> subClasses = hierarchy.getSubclassesOf(flutterClass);
//            for (SootClass subClass : subClasses) {
//                methodsSet.addAll(subClass.getMethods());
//            }
//            outputs.put(flutterClass, methodsSet);
//        }
//
//        // 添加类及其子类的所有方法到outputs
//        for (SootClass flutterClass : MethodchannelClasses) {
//            Set<SootMethod> methodsSet = new HashSet<>(flutterClass.getMethods());
//            // 获取所有子类
//            List<SootClass> subClasses = hierarchy.getSubclassesOf(flutterClass);
//            for (SootClass subClass : subClasses) {
//                methodsSet.addAll(subClass.getMethods());
//            }
//            outputs.put(flutterClass, methodsSet);
//        }
//
//        // 添加类及其子类的所有方法到outputs
//        for (SootClass flutterClass : BasicchannelClasses) {
//            Set<SootMethod> methodsSet = new HashSet<>(flutterClass.getMethods());
//            // 获取所有子类
//            List<SootClass> subClasses = hierarchy.getSubclassesOf(flutterClass);
//            for (SootClass subClass : subClasses) {
//                methodsSet.addAll(subClass.getMethods());
//            }
//            outputs.put(flutterClass, methodsSet);
//        }
//
        // 添加类及其子类的所有方法到outputs
        for (SootClass flutterClass : AllTopClasses) {
            Set<SootMethod> methodsSet = new HashSet<>(flutterClass.getMethods());
            // 获取所有子类
            List<SootClass> subClasses = hierarchy.getSubclassesOf(flutterClass);
            for (SootClass subClass : subClasses) {
                methodsSet.addAll(subClass.getMethods());
            }
            outputs.put(flutterClass, methodsSet);
        }

        return outputs;

        /*
        // 打印所有找到的类
        for (SootClass flutterClass : flutterClasses) {
            System.out.println(flutterClass.getName());
        }

        // 遍历 Scene 中的所有类
        for (SootClass sootClass : Scene.v().getClasses()) {
            // 打印类名
            System.out.println("Class: " + sootClass.getName());

            // 打印该类中的所有方法
            for (SootMethod method : sootClass.getMethods()) {
                System.out.println("    Method: " + method.getSignature());
            }
        }

        Hierarchy hierarchy = Scene.v().getActiveHierarchy();
        // 遍历并打印类及其子类的信息
        for (SootClass flutterClass : flutterClasses) {
            // 打印当前类
            System.out.println("Class: " + flutterClass.getName());

            // 打印当前类的方法
            printMethods(flutterClass);

            // 获取并打印子类
            List<SootClass> subClasses = hierarchy.getSubclassesOf(flutterClass);
            for (SootClass subClass : subClasses) {
                System.out.println("    Subclass: " + subClass.getName());
                printMethods(subClass);
            }

            System.out.println(); // 分隔每个类的输出
        }

        if(Scene.v().containsType("io.flutter.plugin.common.EventChannel")){
            SootClass flutterEventChannelJavaModuleClass = Scene.v().getSootClass("io.flutter.plugin.common.EventChannel");
            List<SootClass> flutterEventChannelJavaModuleSubClassList = hierarchy.getSubclassesOf(flutterEventChannelJavaModuleClass);
            flutterModulePossilbleClassList.addAll(flutterEventChannelJavaModuleSubClassList);
        }

        if(Scene.v().containsType("io.flutter.plugin.common.MethodChannel")){
            SootClass flutterMethodChannelJavaModuleClass = Scene.v().getSootClass("io.flutter.plugin.common.MethodChannel");
            List<SootClass> flutterMethodChanneJavaModuleSubClassList = hierarchy.getSubclassesOf(flutterMethodChannelJavaModuleClass);
            flutterModulePossilbleClassList.addAll(flutterMethodChanneJavaModuleSubClassList);
        }

        if(Scene.v().containsType("io.flutter.plugin.common.MethodCall")){
            SootClass flutterMethodCallJavaModuleClass = Scene.v().getSootClass("io.flutter.plugin.common.MethodCall");
            List<SootClass> flutterMethodCallJavaModuleSubClassList = hierarchy.getSubclassesOf(flutterMethodCallJavaModuleClass);
            flutterModulePossilbleClassList.addAll(flutterMethodCallJavaModuleSubClassList);
        }

        if(Scene.v().containsType("io.flutter.embedding.android.FlutterActivity")){
            SootClass flutterFlutterActivityJavaModuleClass = Scene.v().getSootClass("io.flutter.embedding.android.FlutterActivity");
            List<SootClass> flutterFlutterActivityJavaModuleSubClassList = hierarchy.getSubclassesOf(flutterFlutterActivityJavaModuleClass);
            flutterModulePossilbleClassList.addAll(flutterFlutterActivityJavaModuleSubClassList);
        }

        // 准备存储不同类型 Flutter 模块的集合
        ArrayList<SootClass> flutterActivityClasses = new ArrayList<>();
        ArrayList<SootClass> flutterMethodChannelClasses = new ArrayList<>();
        ArrayList<SootClass> flutterEventChannelClasses = new ArrayList<>();
        ArrayList<SootClass> flutterMethodCallClasses = new ArrayList<>();

        for (SootClass sootClass : flutterModulePossilbleClassList)
        {
            // 检查是否为 FlutterActivity 的子类
            if (sootClass.getSuperclassUnsafe().getName().equals("io.flutter.embedding.android.FlutterActivity")) {
                flutterActivityClasses.add(sootClass);
            }
            // 检查是否为 MethodChannel 的子类
            else if (sootClass.getSuperclassUnsafe().getName().equals("io.flutter.plugin.common.MethodChannel")) {
                flutterMethodChannelClasses.add(sootClass);
            }
            // 检查是否为 EventChannel 的子类
            else if (sootClass.getSuperclassUnsafe().getName().equals("io.flutter.plugin.common.EventChannel")) {
                flutterEventChannelClasses.add(sootClass);
            }
            // 检查是否为 MethodCall 的子类
            else if (sootClass.getSuperclassUnsafe().getName().equals("io.flutter.plugin.common.MethodCall")) {
                flutterMethodCallClasses.add(sootClass);
            }
        }

        for (SootClass flutterActivityClass : flutterActivityClasses) {
            Collection<SootMethod> activityMethods = extractFlutterMethodsFromClass(flutterActivityClass);
            outputs.put(flutterActivityClass, activityMethods);
        }

        for (SootClass methodChannelClass : flutterMethodChannelClasses) {
            Collection<SootMethod> methodChannelMethods = extractFlutterMethodsFromClass(methodChannelClass);
            outputs.put(methodChannelClass, methodChannelMethods);
        }

        for (SootClass eventChannelClass : flutterEventChannelClasses) {
            Collection<SootMethod> eventChannelMethods = extractFlutterMethodsFromClass(eventChannelClass);
            outputs.put(eventChannelClass, eventChannelMethods);
        }

        for (SootClass methodCallClass : flutterMethodCallClasses) {
            Collection<SootMethod> methodCallMethods = extractFlutterMethodsFromClass(methodCallClass);
            outputs.put(methodCallClass, methodCallMethods);
        }*/

        //return  outputs;
    }



    public static int checkClassInstantiation(String className) {
        int ExprNumber = 0;
        Set<String> mySet = new HashSet<>();
        Chain<SootClass> classes = Scene.v().getApplicationClasses();
        for (SootClass sc : classes) {
            for (SootMethod method : sc.getMethods()) {
                if (method.isConcrete()) {
                    Body body = method.retrieveActiveBody();
                    for (Unit unit : body.getUnits()) {
                        if (unit instanceof JAssignStmt) {
                            JAssignStmt stmt = (JAssignStmt) unit;
                            Value rightOp = stmt.getRightOp(); // 使用 getRightOp() 方法获取右操作数
                            if (rightOp instanceof NewExpr) {
                                NewExpr newExpr = (NewExpr) rightOp;
                                if (newExpr.getBaseType().toString().equals(className)) {
                                    ExprNumber = ExprNumber + 1;
                                    String tmpstr = sc.getName()+method.getName()+stmt;
                                    System.out.println(tmpstr);
                                    mySet.add(tmpstr);
                                    System.out.println("stmt: " + stmt + " class: " + sc.getName() + " method: " + method.getName());
                                    //return true; // 找到实例化
                                    if (sc.getName().contains("Activity")){
                                        ActchannelSet.add(tmpstr);
                                    }
                                    AllchannelSet.add(tmpstr);
                                        //ActivityMethodChannelNumber = ActivityMethodChannelNumber + 1;
                                }
                            }
                        }
                    }
                }
            }
        }
        return mySet.size(); // 没有找到实例化
    }

    public static int checkClassInstantiation(String className, String ActivityType) {
        int ExprNumber = 0;
        Set<String> mySet = new HashSet<>();
        Chain<SootClass> classes = Scene.v().getApplicationClasses();
        for (SootClass sc : classes) {
            for (SootMethod method : sc.getMethods()) {
                if (method.isConcrete()) {
                    Body body = method.retrieveActiveBody();
                    for (Unit unit : body.getUnits()) {
                        if (unit instanceof JAssignStmt) {
                            JAssignStmt stmt = (JAssignStmt) unit;
                            Value rightOp = stmt.getRightOp(); // 使用 getRightOp() 方法获取右操作数
                            if (rightOp instanceof NewExpr) {
                                NewExpr newExpr = (NewExpr) rightOp;
                                if (newExpr.getBaseType().toString().equals(className)) {
                                    ExprNumber = ExprNumber + 1;
                                    String tmpstr = sc.getName()+method.getName()+stmt;
                                    System.out.println(tmpstr);
                                    mySet.add(tmpstr);
                                    System.out.println("stmt: " + stmt + " class: " + sc.getName() + " method: " + method.getName());
                                    if (ActivityType=="MethodChannel"){
                                        if (sc.getName().contains("Activity")){
                                            ActmethodSet.add(tmpstr);
                                        }
                                        AllmethodSet.add(tmpstr);
                                        //ActivityMethodChannelNumber = ActivityMethodChannelNumber + 1;
                                    } else if (ActivityType=="EventChannel") {
                                        if (sc.getName().contains("Activity")){
                                            ActeventSet.add(tmpstr);
                                        }
                                        AlleventSet.add(tmpstr);
                                        //ActivityEventChannelNumber = ActivityEventChannelNumber + 1;
                                    } else if (ActivityType=="BasicChannel") {
                                        if (sc.getName().contains("Activity")){
                                            ActbasicSet.add(tmpstr);
                                        }
                                        AllbasicSet.add(tmpstr);
                                        //ActivityBasicChannelNumber = ActivityBasicChannelNumber + 1;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return mySet.size(); // 没有找到实例化
    }


    public static Collection<SootMethod> extractFlutterMethodsFromClass(SootClass sootClass) {
        Collection<SootMethod> flutterMethods = new ArrayList<>();

        for (SootMethod method : sootClass.getMethods()) {
            // 你可以在这里添加一些条件来过滤或选择特定的方法
            flutterMethods.add(method);
        }

        return flutterMethods;
    }

    public static int getnonDartMethodCount(){
        SootClass dartClass = getDartClass();
        // Retrieve all classes from the Soot scene
        Chain<SootClass> allClasses = Scene.v().getClasses();
        // Initialize counters
        int nonDartMethodCount = 0;

// Iterate over each class
        for (SootClass sootClass : allClasses) {
            // Skip the Dart class
            if (!sootClass.equals(dartClass)) {
                // Iterate over each method in the class
                for (SootMethod sootMethod : sootClass.getMethods()) {
                    // Increment the method count
                    nonDartMethodCount++;
                    // Add the size of the method's active body units to the size counter
                    //nonDartMethodSize += sootMethod.getActiveBody().getUnits().size();
                }
            }
        }
        return nonDartMethodCount;
    }

    public static int getnonDartMethodSize(){
        SootClass dartClass = getDartClass();
        // Retrieve all classes from the Soot scene
        Chain<SootClass> allClasses = Scene.v().getClasses();
        // Initialize counters
        int nonDartMethodCount = 0;
        int nonDartMethodSize = 0;
        // Iterate over each class
        for (SootClass sootClass : allClasses) {
            // Skip the Dart class
            if (!sootClass.equals(dartClass)) {
                // Iterate over each method in the class
                for (SootMethod sootMethod : sootClass.getMethods()) {
                    // Increment the method count
                    nonDartMethodCount++;
                    try{
                        nonDartMethodSize += sootMethod.getActiveBody().getUnits().size();
                    } catch (Exception e) {
                        //throw new RuntimeException(e);
                    }
                    // Add the size of the method's active body units to the size counter
                }
            }
        }
        return nonDartMethodSize;
    }
}
