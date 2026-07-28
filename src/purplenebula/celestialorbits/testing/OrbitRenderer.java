package purplenebula.celestialorbits.testing;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;

import java.awt.*;

public class OrbitRenderer implements EveryFrameScript {

    private boolean done = false;
    private boolean safeToDraw = false;

    private CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
    private SectorEntityToken currentCourseTarget;
    private SectorEntityToken currentCourseTargetOrbitLine;
    private SectorEntityToken currentCourseTargetSelectorLine;


    public OrbitRenderer() {
//        DrawUtils.drawCircle();

    }

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


            if (Global.getSector().getCampaignUI().getCurrentCourseTarget() == null) {

                if (currentCourseTargetOrbitLine != null) { //  && currentCourseTargetSelectorLine != null
                    currentCourseTarget.getStarSystem().removeEntity(currentCourseTargetOrbitLine);
//                    currentCourseTarget.getStarSystem().removeEntity(currentCourseTargetSelectorLine);
                }

            }
            else {
                if (Global.getSector().getCampaignUI().getCurrentCourseTarget() != currentCourseTarget) {
                    done = false;
                    if (currentCourseTargetOrbitLine != null) { //  && currentCourseTargetSelectorLine != null
                        currentCourseTarget.getStarSystem().removeEntity(currentCourseTargetOrbitLine);
//                        currentCourseTarget.getStarSystem().removeEntity(currentCourseTargetSelectorLine);
                    }
                    currentCourseTarget = Global.getSector().getCampaignUI().getCurrentCourseTarget();
                }
            }

            if (!done && playerFleet != null && currentCourseTarget != null && !currentCourseTarget.isStar()) {

                SectorEntityToken orbitMidPoint = currentCourseTarget.getOrbitFocus();
                float circularOrbitRadius = currentCourseTarget.getCircularOrbitRadius();
//                currentCourseTargetOrbitLine = currentCourseTarget.getStarSystem().addRingBand(
//                        orbitMidPoint,
//                        "misc",
//                        "rings_special0",
//                        256f,
//                        0, Color.white,
//                        256f,
//                        circularOrbitRadius,
//                        0f
//                );
                currentCourseTargetOrbitLine = currentCourseTarget.getStarSystem().addRingBand(orbitMidPoint, "misc", "rings_planetradius", 256f, 2, Color.white, 256f, circularOrbitRadius, 31f);
//                currentCourseTargetSelectorLine = currentCourseTarget.getStarSystem().addRingBand(currentCourseTarget, "misc", "rings_planetradius", 256f, 2, Color.white, 256f, currentCourseTarget.getRadius(), 31f);


                done = true;
            }

        }
    }
}
