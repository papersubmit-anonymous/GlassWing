package glasswing;

import soot.SootClass;
import soot.SootMethod;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.handlers.PreAnalysisHandler;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import soot.Body;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.Jimple;
import soot.jimple.StringConstant;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.entryPointCreators.DefaultEntryPointCreator;
import soot.jimple.infoflow.handlers.PreAnalysisHandler;
import java.util.Collection;
import java.util.Map;

public class testPre implements PreAnalysisHandler {
    SetupApplication app;
    String pkgName;
    public testPre(SetupApplication app, String pkgName) {
        this.app = app;
        this.pkgName = pkgName;
    }

    @Override
    public void onBeforeCallgraphConstruction() {
        int numberOfDartMethod = 0;
        SootMethod dartModulesEntryMethod;

        Map<SootClass, Collection<SootMethod>> dartMethods = FlutterUtil.getFlutterModules();

        Collection<String> dartModuleEntryPoints = new ArrayList<String>();
        for(SootClass dartModule: dartMethods.keySet()){
            SootMethod dartModuleEntryPoint;
            // get all the @ react methods
            Collection<SootMethod> dartMethodsInModule = dartMethods.get(dartModule);
            Collection<String> dartMethodsInModuleSignatures = new ArrayList<String>(){{
                for(SootMethod dartMethod: dartMethodsInModule){
                    add(dartMethod.getSignature());
                }
            }};
            // create a default entry point creator to these react methods
            DefaultEntryPointCreator reactModuleEntryPointCreator = new DefaultEntryPointCreator(dartMethodsInModuleSignatures);
            // change the name of that react method
            reactModuleEntryPointCreator.setDummyMethodName(dartModule.getName().replace("\\.","_"));
            // create the react entry point method
            dartModuleEntryPoint = reactModuleEntryPointCreator.createDummyMain();
            dartModuleEntryPoints.add(dartModuleEntryPoint.getSignature());
            numberOfDartMethod += dartMethodsInModule.size();
        }
        DefaultEntryPointCreator dartEntryPointCreator = new DefaultEntryPointCreator(dartModuleEntryPoints);
        // change the name of that react method
        dartEntryPointCreator.setDummyMethodName("dummyReactModulesMethod");
        // create the react entry point method
        dartModulesEntryMethod = dartEntryPointCreator.createDummyMain();
        FlutterUtil.dartModulesEntryMethod = dartModulesEntryMethod;

        SootMethod dummyMainMethod = app.getDummyMainMethod();
        Body dummyMainBody = dummyMainMethod.retrieveActiveBody();
        Unit lastUnit = dummyMainBody.getUnits().getLast();

        Unit dartModulesMethodInvocationUnit = Jimple.v().newInvokeStmt(
                Jimple.v().newStaticInvokeExpr(
                        dartModulesEntryMethod.makeRef(),
                        Arrays.asList(new Value[] {StringConstant.v("")})
                )
        );

        // insert invocation stmt before last unit of dummy main body
        dummyMainBody.getUnits().insertBefore(
                dartModulesMethodInvocationUnit,
                lastUnit
        );

        // validate the syntax of dummyMainBody
        dummyMainBody.validate();

        System.out.println("=======[" + this.pkgName + "]=======");
        System.out.println("Number of dart methods : " + numberOfDartMethod);
        System.out.println("[+] Number of dart channel : " + FlutterUtil.AllchannelSet.size());
        System.out.println("[+] Number of Method channel : " + FlutterUtil.AllmethodSet.size());
        System.out.println("[+] Number of Event channel : " + FlutterUtil.AlleventSet.size());
        System.out.println("[+] Number of Basic channel : " + FlutterUtil.AllbasicSet.size());
        System.out.println("[+] Number of Activity channel : " + FlutterUtil.ActchannelSet.size());
        System.out.println("[+] Number of Activity Method channel : " + FlutterUtil.ActmethodSet.size());
        System.out.println("[+] Number of Activity Event channel : " + FlutterUtil.ActeventSet.size());
        System.out.println("[+] Number of Activity Basic channel : " + FlutterUtil.ActbasicSet.size());
        File file = new File("/home/syc/logfile.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();  // 创建新文件
            } catch (IOException e) {
                System.out.println("Error creating new file: " + e.getMessage());
                return;
            }
        }

        // 使用 FileWriter 以追加模式写入文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write("=======[" + this.pkgName + "]=======");
            writer.newLine();
            writer.write("Number of dart methods : " + numberOfDartMethod);
            writer.newLine();
            writer.write("[+] Number of dart channel : " + FlutterUtil.ChannelNumber);
            writer.newLine();
            writer.write("[+] Number of Method channel : " + FlutterUtil.MethodChannelNumber);
            writer.newLine();
            writer.write("[+] Number of Event channel : " + FlutterUtil.EventChannelNumber);
            writer.newLine();
            writer.write("[+] Number of Basic channel : " + FlutterUtil.BasicChannelNumber);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    @Override
    public void onAfterCallgraphConstruction() {

    }
}
