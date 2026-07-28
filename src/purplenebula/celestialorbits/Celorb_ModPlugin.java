package purplenebula.celestialorbits;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorAPI;
import purplenebula.celestialorbits.trackers.InSystemTracker;
import purplenebula.celestialorbits.trackers.SystemTimeTracker;

public class Celorb_ModPlugin extends BaseModPlugin {


    private Celorb_ModPlugin covModPlugin;

    public Celorb_ModPlugin() {
        covModPlugin = this;
    }

    public Celorb_ModPlugin getCovModPlugin() {
        return covModPlugin;
    }

//    @Override
//    public void afterGameSave() {
//        SectorAPI sector = Global.getSector();
//        if (!sector.hasScript(SystemOrbitRenderer.class))
//            sector.addScript(new SystemOrbitRenderer());
//    }
//
//    @Override
//    public void beforeGameSave() {
//        SectorAPI sector = Global.getSector();
//        SystemOrbitRenderer sor = null;
//        for (EveryFrameScript script : sector.getScripts()) {
//            if (script instanceof SystemOrbitRenderer) {
//                sor = (SystemOrbitRenderer) script;
//                sor.deleteLines();
////                sor.setFinished();
//                break;
//            }
//        }
//        if (sor != null) sector.removeScript(sor);
//    }

    @Override
    public void onGameLoad(boolean newGame) {
//        Global.getSector().addTransientScript(new SystemOrbitRenderer());
        SectorAPI sector = Global.getSector();
//        if (!sector.hasScript(SystemOrbitRenderer.class))
//            sector.addScript(new SystemOrbitRenderer());

//        if (!sector.hasScript(OrbitRendererOpenGL.class))
//            sector.addScript(new OrbitRendererOpenGL());

//        if (!sector.hasScript(HasSetCourseListener.class))
//            sector.addScript(new HasSetCourseListener());

//        if (!sector.hasScript(InSystemTracker.class))
//            sector.addScript(new InSystemTracker());
        Global.getSector().addTransientScript(new InSystemTracker());
        Global.getSector().addTransientScript(new SystemTimeTracker());

    }

}
