package purplenebula.celestialorbits.testing;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.ids.Factions;

public class HasSetCourseListener implements EveryFrameScript {

    SectorEntityToken orbitLineEntity;
    private SectorEntityToken currentCourseTarget;


    boolean safeToDraw = false;
    boolean done = false;

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }

    @Override
    public void advance(float amount) {

        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        if (playerFleet != null) {
            if (playerFleet.isInHyperspace()) safeToDraw = false;
            if (playerFleet.isInHyperspaceTransition()) safeToDraw = false;
            else safeToDraw = true;
        }

        if (safeToDraw && !Global.getSector().getCampaignUI().isShowingDialog() && Global.getCurrentState() == GameState.CAMPAIGN) {

//            SectorEntityToken currentCourseTarget = Global.getSector().getCampaignUI().getCurrentCourseTarget();


            if (Global.getSector().getCampaignUI().getCurrentCourseTarget() == null) {

                if (orbitLineEntity != null) { //  && currentCourseTargetSelectorLine != null
                    currentCourseTarget.getStarSystem().removeEntity(orbitLineEntity);
                    orbitLineEntity = null;
                    currentCourseTarget = null;
                    //                    currentCourseTarget.getStarSystem().removeEntity(currentCourseTargetSelectorLine);
                }

            }
            else {
                if (Global.getSector().getCampaignUI().getCurrentCourseTarget() != currentCourseTarget) {
                    done = false;
                    if (orbitLineEntity != null) { //  && currentCourseTargetSelectorLine != null
                        currentCourseTarget.getStarSystem().removeEntity(orbitLineEntity);
//                        currentCourseTarget.getStarSystem().removeEntity(currentCourseTargetSelectorLine);
                    }
                    currentCourseTarget = Global.getSector().getCampaignUI().getCurrentCourseTarget();
                }
            }

            if (!done && playerFleet != null && currentCourseTarget != null && !currentCourseTarget.isStar()) {

                orbitLineEntity = currentCourseTarget.getStarSystem().addCustomEntity("CourseEntityOrbit","Entity Orbit","entity_orbit_vector", Factions.PLAYER);
                done = true;

            }

        }

    }
}
