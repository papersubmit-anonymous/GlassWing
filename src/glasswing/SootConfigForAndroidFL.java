package glasswing;

import soot.jimple.infoflow.android.config.SootConfigForAndroid;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.options.Options;


public class SootConfigForAndroidFL extends SootConfigForAndroid{
    private String dartTool;

    public SootConfigForAndroidFL(String dartTool) {
        super();
        this.dartTool = dartTool;
    }

    @Override
    public void setSootOptions(Options options, InfoflowConfiguration config) {
        super.setSootOptions(options, config);
        options.set_flutter(true);
        options.set_dart_nativehost_path(dartTool);
        Options.v().set_src_prec(Options.src_prec_apk);
        //options.set_react_native(true);
        //options.set_hbc_nativehost_path(hbcTool);
        // 允许 Phantom 引用
        options.set_allow_phantom_refs(true);
        // 启用调用图生成
        options.setPhaseOption("cg", "enabled:true");
        Options.v().set_output_dir("/home/syc/out");
    }

}