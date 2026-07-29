package purplenebula.celestialorbits.trackers;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.campaign.CampaignAsteroid;
import com.fs.starfarer.campaign.CampaignTerrain;
import com.fs.starfarer.campaign.RingBand;
import lunalib.lunaSettings.LunaSettings;

import java.util.ArrayList;
import java.util.List;

public class InSystemTracker implements EveryFrameScript {

    private SectorEntityToken orbitDrawEntity;
    private StarSystemAPI lastSystem;

//    private List<SectorEntityToken> selectedEntities = new ArrayList<>();

    private boolean done = false;
//    private boolean safeToDraw = false;

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

        if (orbitDrawEntity == null && playerFleet.getStarSystem() != null &&
                playerFleet.getStarSystem().getEntityById("OrbitDrawEntity") != null) {
            orbitDrawEntity = playerFleet.getStarSystem().getEntityById("OrbitDrawEntity");
        }

        if (playerFleet != null) {

            if (Boolean.TRUE.equals(LunaSettings.getBoolean("PN_CelestialOrbits", "celorb_disableModBoolean"))) {

                if (orbitDrawEntity != null) {

                    if (lastSystem != null && lastSystem.getEntityById("OrbitDrawEntity") != null) {
                        lastSystem.removeEntity(orbitDrawEntity);
                    }
                    if (playerFleet.getStarSystem() != null &&
                            playerFleet.getStarSystem().getEntityById("OrbitDrawEntity") != null) {
                        playerFleet.getStarSystem().removeEntity(orbitDrawEntity);
                    }

                }
                done = false;
                return;
            }

//            if (lastSystem == null) {
//                lastSystem = playerFleet.getStarSystem();
//            }
//
//            if (playerFleet.getStarSystem() != lastSystem) {
//                lastSystem = playerFleet.getStarSystem();
//                filteredDuplicateRadii = false;
//            }

            boolean safeToDraw = isSafeToDraw(playerFleet);

            if (!safeToDraw || playerFleet.getStarSystem() != lastSystem) {
                if (lastSystem != null) {
                    if (lastSystem.getEntityById("OrbitDrawEntity") != null) {
                        lastSystem.removeEntity(orbitDrawEntity);
                    }
                }
                done = false;
            }

            if (!done && safeToDraw && !Global.getSector().getCampaignUI().isShowingDialog() &&
                    Global.getCurrentState() == GameState.CAMPAIGN && playerFleet.getStarSystem() != null) {

                if (lastSystem != playerFleet.getStarSystem()) lastSystem = playerFleet.getStarSystem();

                if (playerFleet.getStarSystem().getEntityById("OrbitDrawEntity") == null)
                    orbitDrawEntity = playerFleet.getStarSystem().addCustomEntity("OrbitDrawEntity","Orbit Draw Entity","entity_orbit_vector", Factions.NEUTRAL);
                done = true;

            }

        }

    }

    private boolean isSafeToDraw(CampaignFleetAPI playerFleet) {
        if (playerFleet.isInHyperspace()) return false;
        if (Boolean.TRUE.equals(LunaSettings.getBoolean("PN_CelestialOrbits", "celorb_disableModBoolean"))) return false;
//        else if (playerFleet.isInHyperspaceTransition()) safeToDraw = false;
        return true;
    }
}
