package purplenebula.celestialorbits.testing;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.campaign.CampaignAsteroid;
import com.fs.starfarer.campaign.CampaignTerrain;
import com.fs.starfarer.campaign.RingBand;

import java.awt.*;
import java.util.*;
import java.util.List;

public class SystemOrbitRenderer implements EveryFrameScript {

    private boolean isFinished = false;

    private boolean done = false;
    private boolean safeToDraw = false;

    private CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
    private List<RingBandAPI> orbitLines = new ArrayList<>();
    private List<SectorEntityToken> selectedEntities = new ArrayList<>();
    private StarSystemAPI lastSystem;
    private Map<SectorEntityToken,Float> uniqueRadii = new HashMap<>();

    @Override
    public boolean isDone() {
        return isFinished;
    }

    public void setFinished() {
        isFinished = true;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }

    public void deleteLines() {
        for (RingBandAPI orbitLine : orbitLines) {
            lastSystem.removeEntity(orbitLine);
        }
        orbitLines.clear();
    }

    @Override
    public void advance(float amount) {

        if (playerFleet != null) {

            isSafeToDraw(playerFleet);

            if (!safeToDraw && !orbitLines.isEmpty()) {
                for (RingBandAPI orbitLine : orbitLines) {
                    lastSystem.removeEntity(orbitLine);
                }
                orbitLines.clear();
                done = false;
            }

            if (!done && safeToDraw && !Global.getSector().getCampaignUI().isShowingDialog() &&
                    Global.getCurrentState() == GameState.CAMPAIGN && playerFleet.getStarSystem() != null) {

                if (lastSystem != playerFleet.getStarSystem()) lastSystem = playerFleet.getStarSystem();

                for (SectorEntityToken systemEntity : playerFleet.getStarSystem().getAllEntities()) {

                    if (systemEntity.isPlayerFleet()) continue;
                    if (systemEntity.isStar()) continue;
                    if (systemEntity.isSystemCenter()) continue;
//                    if (!systemEntity.isVisibleToPlayerFleet()) continue;
                    if (systemEntity instanceof CampaignTerrain) continue;
                    if (systemEntity instanceof RingBand) continue;
                    if (systemEntity instanceof CampaignAsteroid) continue;
                    if (systemEntity.hasTag("orbital_junk")) continue;



//                    if (systemEntity.getCustomEntityType() != null && systemEntity.getCustomEntityType().contains("station"))
//                        selectedEntities.add(systemEntity);

//                    if (systemEntity instanceof PlanetAPI || systemEntity instanceof JumpPointAPI)
                    selectedEntities.add(systemEntity);

                }

                for (SectorEntityToken selectedEntity : selectedEntities) {
                    SectorEntityToken orbitMidPoint = selectedEntity.getOrbitFocus();
                    if (orbitMidPoint == null) continue;
                    float circularOrbitRadius = selectedEntity.getCircularOrbitRadius();

                    if (uniqueRadii.isEmpty()) {
                        RingBandAPI orbitLine = selectedEntity.getStarSystem().addRingBand(orbitMidPoint, "misc", "rings_planetradius", 256f, 2, Color.white, 256f, circularOrbitRadius, 31f);
                        orbitLines.add(orbitLine);
                    }

                    for (Map.Entry<SectorEntityToken, Float> uniqueLineData : uniqueRadii.entrySet()) {
                        if (uniqueLineData.getKey() == orbitMidPoint && circularOrbitRadius == uniqueLineData.getValue()) continue;
                        RingBandAPI orbitLine = selectedEntity.getStarSystem().addRingBand(orbitMidPoint, "misc", "rings_planetradius", 256f, 2, Color.white, 256f, circularOrbitRadius, 31f);
                        orbitLines.add(orbitLine);
                    }

                    done = true;
                }


            }

        }


    }

    private void isSafeToDraw(CampaignFleetAPI playerFleet) {
        if (playerFleet.isInHyperspace()) safeToDraw = false;
        else if (playerFleet.isInHyperspaceTransition()) safeToDraw = false;
        else safeToDraw = true;
    }
}
