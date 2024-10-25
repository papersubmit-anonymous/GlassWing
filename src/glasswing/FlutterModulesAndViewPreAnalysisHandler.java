package glasswing;
import java.util.*;

import reunify.ReactUtil;
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

public class FlutterModulesAndViewPreAnalysisHandler implements PreAnalysisHandler {

    SetupApplication app;

    public FlutterModulesAndViewPreAnalysisHandler(SetupApplication app) {
        this.app = app;
    }

    @Override
    public void onBeforeCallgraphConstruction() {
        int numberOfFlutterMethod = 0;

        SootMethod flutterModulesEntryMethod;


        if (!FlutterUtil.isFlutterModulesDetected) {
            System.out.println("React entry point is created");
            // get all the react modules
            Map<SootClass, Collection<SootMethod>> flutterMethods = FlutterUtil.getFlutterModules();

            Collection<String> flutterModuleEntryPoints = new ArrayList<String>();

            for(SootClass flutterModule: flutterMethods.keySet()){
                SootMethod flutterModuleEntryPoint;
                // get all the @ react methods
                Collection<SootMethod> flutterMethodsInModule = flutterMethods.get(flutterModule);
                Collection<String> flutterMethodsInModuleSignatures = new ArrayList<String>(){{
                    for(SootMethod flutterMethod: flutterMethodsInModule){
                        add(flutterMethod.getSignature());
                    }
                }};
                // create a default entry point creator to these react methods
                DefaultEntryPointCreator flutterModuleEntryPointCreator = new DefaultEntryPointCreator(flutterMethodsInModuleSignatures);
                // change the name of that react method
                flutterModuleEntryPointCreator.setDummyMethodName(flutterModule.getName().replace("\\.","_"));
                // create the react entry point method
                flutterModuleEntryPoint = flutterModuleEntryPointCreator.createDummyMain();
                flutterModuleEntryPoints.add(flutterModuleEntryPoint.getSignature());

                numberOfFlutterMethod += flutterMethodsInModule.size();
            }

            DefaultEntryPointCreator flutterEntryPointCreator = new DefaultEntryPointCreator(flutterModuleEntryPoints);
            // change the name of that react method
            flutterEntryPointCreator.setDummyMethodName("dummyReactModulesMethod");
            // create the react entry point method
            flutterModulesEntryMethod = flutterEntryPointCreator.createDummyMain();

            FlutterUtil.flutterModulesEntryMethod = flutterModulesEntryMethod;

        }else{
            flutterModulesEntryMethod = ReactUtil.reactModulesEntryMethod;
        }
        // invoke react entry point in dummy main method
        // get dummy main method and body
        SootMethod dummyMainMethod = app.getDummyMainMethod();
        Body dummyMainBody = dummyMainMethod.retrieveActiveBody();
        Unit lastUnit = dummyMainBody.getUnits().getLast();


        Unit reactModulesMethodInvocationUnit = Jimple.v().newInvokeStmt(
                Jimple.v().newStaticInvokeExpr(
                        flutterModulesEntryMethod.makeRef(),
                        Arrays.asList(new Value[] {StringConstant.v("")})
                )
        );

        // insert invocation stmt before last unit of dummy main body
        dummyMainBody.getUnits().insertBefore(
                reactModulesMethodInvocationUnit,
                lastUnit
        );

        // validate the syntax of dummyMainBody
        dummyMainBody.validate();

        System.out.println("Number of react methods: " + numberOfFlutterMethod);

    }

    @Override
    public void onAfterCallgraphConstruction() {

    }
}
