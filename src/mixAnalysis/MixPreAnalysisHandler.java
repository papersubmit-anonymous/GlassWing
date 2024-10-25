package mixAnalysis;

import glasswing.FlutterUtil;
import reunify.ReactUtil;
import soot.*;
import soot.jimple.Jimple;
import soot.jimple.StringConstant;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.entryPointCreators.DefaultEntryPointCreator;
import soot.jimple.infoflow.handlers.PreAnalysisHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class MixPreAnalysisHandler implements PreAnalysisHandler  {
    SetupApplication app;

    public MixPreAnalysisHandler(SetupApplication app) {
        this.app = app;
    }

    @Override
    public void onBeforeCallgraphConstruction() {
        // TODO Auto-generated method stub
        int numberOfReactMethod = 0;
        SootMethod reactModulesEntryMethod;
        if (!ReactUtil.isReactModulesDetected) {
            System.out.println("React entry point is created");
            // get all the react modules
            Map<SootClass, Collection<SootMethod>> reactMethods = ReactUtil.getReactModules();
            Collection<String> reactModuleEntryPoints = new ArrayList<String>();
            for(SootClass reactModule: reactMethods.keySet()){
                SootMethod reactModuleEntryPoint;
                // get all the @ react methods
                Collection<SootMethod> reactMethodsInModule = reactMethods.get(reactModule);
                Collection<String> reactMethodsInModuleSignatures = new ArrayList<String>(){{
                    for(SootMethod reactMethod: reactMethodsInModule){
                        add(reactMethod.getSignature());
                    }
                }};
                // create a default entry point creator to these react methods
                DefaultEntryPointCreator reactModuleEntryPointCreator = new DefaultEntryPointCreator(reactMethodsInModuleSignatures);
                // change the name of that react method
                reactModuleEntryPointCreator.setDummyMethodName(reactModule.getName().replace("\\.","_"));
                // create the react entry point method
                reactModuleEntryPoint = reactModuleEntryPointCreator.createDummyMain();
                reactModuleEntryPoints.add(reactModuleEntryPoint.getSignature());
                numberOfReactMethod += reactMethodsInModule.size();
            }
            DefaultEntryPointCreator reactEntryPointCreator = new DefaultEntryPointCreator(reactModuleEntryPoints);
            // change the name of that react method
            reactEntryPointCreator.setDummyMethodName("dummyReactModulesMethod");
            // create the react entry point method
            reactModulesEntryMethod = reactEntryPointCreator.createDummyMain();
            ReactUtil.reactModulesEntryMethod = reactModulesEntryMethod;
        }else{
            reactModulesEntryMethod = ReactUtil.reactModulesEntryMethod;
        }

        // TODO Auto-generated method stub
        SootMethod reactViewsEntryPoint;
        if (!ReactUtil.isReactViewsDetected) {
            System.out.println("React entry point is created");
            // get all the react modules
            Map<SootClass, Collection<SootMethod>> reactViews = ReactUtil.getReactViews();
            Collection<String> reactViewsEntryPoints = new ArrayList<String>();
            for(SootClass reactModule: reactViews.keySet()){
                SootMethod reactViewEntryPoint;
                // get all the @ react methods
                Collection<SootMethod> reactMethodsInModule = reactViews.get(reactModule);
                Collection<String> reactMethodsInModuleSignatures = new ArrayList<String>(){{
                    for(SootMethod reactMethod: reactMethodsInModule){
                        add(reactMethod.getSignature());
                    }
                }};
                // create a default entry point creator to these react methods
                DefaultEntryPointCreator reactViewEntryPointCreator = new DefaultEntryPointCreator(reactMethodsInModuleSignatures);
                // change the name of that react method
                reactViewEntryPointCreator.setDummyMethodName(reactModule.getName().replace("\\.","_"));
                // create the react entry point method
                reactViewEntryPoint = reactViewEntryPointCreator.createDummyMain();
                reactViewsEntryPoints.add(reactViewEntryPoint.getSignature());

                numberOfReactMethod += reactMethodsInModule.size();
            }
            DefaultEntryPointCreator reactViewsEntryPointCreator = new DefaultEntryPointCreator(reactViewsEntryPoints);
            // change the name of that react method
            reactViewsEntryPointCreator.setDummyMethodName("dummyReactViewsMethod");
            // create the react entry point method
            reactViewsEntryPoint = reactViewsEntryPointCreator.createDummyMain();
            ReactUtil.reactViewsEntryPoint = reactViewsEntryPoint;
        }else{
            reactViewsEntryPoint = ReactUtil.reactViewsEntryPoint;
        }
        // invoke react entry point in dummy main method
        // get dummy main method and body
        SootMethod dummyMainMethod = app.getDummyMainMethod();
        Body dummyMainBody = dummyMainMethod.retrieveActiveBody();
        Unit lastUnit = dummyMainBody.getUnits().getLast();
        Unit reactViewsMethodInvocationUnit = Jimple.v().newInvokeStmt(
                Jimple.v().newStaticInvokeExpr(
                        reactViewsEntryPoint.makeRef(),
                        Arrays.asList(new Value[] {StringConstant.v("")})
                )
        );
        Unit reactModulesMethodInvocationUnit = Jimple.v().newInvokeStmt(
                Jimple.v().newStaticInvokeExpr(
                        reactModulesEntryMethod.makeRef(),
                        Arrays.asList(new Value[] {StringConstant.v("")})
                )
        );
        // insert invocation stmt before last unit of dummy main body
        dummyMainBody.getUnits().insertBefore(
                reactViewsMethodInvocationUnit,
                lastUnit
        );
        // insert invocation stmt before last unit of dummy main body
        dummyMainBody.getUnits().insertBefore(
                reactModulesMethodInvocationUnit,
                lastUnit
        );
        // validate the syntax of dummyMainBody
        dummyMainBody.validate();
        System.out.println("Number of react methods: " + numberOfReactMethod);
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
        dummyMainMethod = app.getDummyMainMethod();
        dummyMainBody = dummyMainMethod.retrieveActiveBody();
        lastUnit = dummyMainBody.getUnits().getLast();


        reactModulesMethodInvocationUnit = Jimple.v().newInvokeStmt(
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
