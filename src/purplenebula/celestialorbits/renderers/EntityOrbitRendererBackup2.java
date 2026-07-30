package purplenebula.celestialorbits.renderers;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignEngineLayers;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.impl.campaign.BaseCustomEntityPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.campaign.CampaignAsteroid;
import com.fs.starfarer.campaign.CampaignTerrain;
import com.fs.starfarer.campaign.RingBand;
import lunalib.lunaSettings.LunaSettings;
import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EntityOrbitRendererBackup2 extends BaseCustomEntityPlugin {
    private static final Logger log = Logger.getLogger(EntityOrbitRendererBackup2.class);

    private Set<String> entityBlacklist = new HashSet<>();
    private Map<SectorEntityToken,Float> uniqueOrbits = new HashMap<>();
    boolean filteredDuplicateRadii = false;
    String visualsSetting = "";
    String researchSetting = "";
    String courseFocusSetting = "";

    float dRed = 0.7f;
    float dGreen = 0.8f;
    float dBlue = 1.0f;

//    private PlanetAPI planet;
//
//    public EntityOrbitRenderer(PlanetAPI planet) {
//        this.planet = planet;
//    }

    private SectorEntityToken entity;

    @Override
    public void advance(float amount) {
        super.advance(amount);
    }

    @Override
    public void init(SectorEntityToken entity, Object pluginParams) {
        super.init(entity, pluginParams);
        this.entity = entity;
    }

    @Override
    public float getRenderRange() {
        return  10000000f;
    }

    @Override
    public void render(CampaignEngineLayers layer, ViewportAPI viewport) {

        if (layer != CampaignEngineLayers.TERRAIN_2) return;

        if (Boolean.TRUE.equals(LunaSettings.getBoolean("PN_CelestialOrbits", "celorb_disableModBoolean"))) {

        }

        if (Boolean.TRUE.equals(LunaSettings.getBoolean("PN_CelestialOrbits", "celorb_orbitColorBoolean"))) {
            Color color = LunaSettings.getColor("PN_CelestialOrbits", "celorb_orbitColorPicker");
            if (color != null) {
                dRed = (float) color.getRed() / 255;
                dGreen = (float) color.getGreen() / 255;
                dBlue = (float) color.getBlue() / 255;
            }
            else {
                log.error("Failed to grab custom color value.");
                dRed = 0.7f;
                dGreen = 0.8f;
                dBlue = 1.0f;
            }
        }
        else {
            dRed = 0.7f;
            dGreen = 0.8f;
            dBlue = 1.0f;
        }

        if (!visualsSetting.equals(LunaSettings.getString("PN_CelestialOrbits", "celorb_visualsRadio")) ||
        !researchSetting.equals(LunaSettings.getString("PN_CelestialOrbits", "celorb_researchRadio"))) {
            uniqueOrbits.clear();
            filteredDuplicateRadii = false;
        }

        if (!filteredDuplicateRadii) {
            for (SectorEntityToken systemEntity : entity.getStarSystem().getAllEntities()) {


                // Populate visuals setting variable
                visualsSetting = LunaSettings.getString("PN_CelestialOrbits", "celorb_visualsRadio");
                // Populate research setting variable
                researchSetting = LunaSettings.getString("PN_CelestialOrbits", "celorb_researchRadio");

                // Check if visualsSetting is null, if so, throw an error
                if (visualsSetting == null)
                    throw new RuntimeException("Unable to find LunaSetting 'celorb_visualsRadio' for mod 'PN_CelestialOrbits'");
                // Check if researchSetting is null, if so, throw an error
                if (researchSetting == null)
                    throw new RuntimeException("Unable to find LunaSetting 'celorb_researchRadio' for mod 'PN_CelestialOrbits'");

                float radius = systemEntity.getCircularOrbitRadius();

                if (uniqueOrbits.isEmpty()) {
                    uniqueOrbits.put(systemEntity, radius);
                } else {
                    boolean addUniqueOrbit = true;
                    for (Map.Entry<SectorEntityToken, Float> uniqueOrbitEntity : uniqueOrbits.entrySet()) {
                        if (uniqueOrbitEntity.getKey().getOrbitFocus() == systemEntity.getOrbitFocus()) {
                            if (uniqueOrbitEntity.getValue() == radius) {
                                addUniqueOrbit = false;
                                break;
                            }
                        }
                    }
                    if (addUniqueOrbit) {
                        uniqueOrbits.put(systemEntity, radius);
                    }
                }

            }
            filteredDuplicateRadii = true;
        }
        else {

            for (Map.Entry<SectorEntityToken, Float> uniqueOrbit : uniqueOrbits.entrySet()) {

//                if (uniqueOrbit.getKey().getOrbitFocus() == null) continue;

                // Skip entities that shouldn't have an orbit drawn
                if (uniqueOrbit.getKey().getOrbitFocus() == null) continue;
                if (uniqueOrbit.getKey().isStar()) continue;
                if (uniqueOrbit.getKey().isSystemCenter()) continue;
                if (uniqueOrbit.getKey().isPlayerFleet()) continue;
                if (uniqueOrbit.getKey() instanceof CampaignTerrain) continue;
                if (uniqueOrbit.getKey() instanceof RingBand) continue;
                if (uniqueOrbit.getKey() instanceof CampaignAsteroid) continue;
                if (uniqueOrbit.getKey().hasTag(Tags.ORBITAL_JUNK)) continue;
                if (uniqueOrbit.getKey().hasTag(Tags.STELLAR_SHADE)) continue;
                if (uniqueOrbit.getKey().hasTag(Tags.STELLAR_MIRROR)) continue;
                if (uniqueOrbit.getKey().getCustomEntityType() != null) {
                    boolean skipEntity = false;
                    if (entityBlacklist == null || entityBlacklist.isEmpty()) entityBlacklist = populateEntityBlacklist();
                    for (String blacklistedEntityType : entityBlacklist) {
                        if (uniqueOrbit.getKey().getCustomEntityType().equals(blacklistedEntityType)) {
                            skipEntity = true;
                            break;
                        }
                    }
                    if (skipEntity) continue;
//                    if (uniqueOrbit.getKey().getCustomEntityType().contains("cache")) continue;
//                    if (uniqueOrbit.getKey().getCustomEntityType().contains("derelict")) continue;
//                    if (uniqueOrbit.getKey().getCustomEntityType().equals(Entities.WRECK)) continue;
//                    if (uniqueOrbit.getKey().getCustomEntityType().equals(Entities.CARGO_PODS)) continue;
//                    if (uniqueOrbit.getKey().getCustomEntityType().equals(Entities.STABLE_LOCATION)) continue;
                }


                if (visualsSetting.equals("Only Planetary Bodies")) {
                    if (!(uniqueOrbit.getKey() instanceof PlanetAPI)) continue;
                    PlanetAPI planet = (PlanetAPI) uniqueOrbit.getKey();
                    if (planet.isBlackHole()) continue;
                } else if (visualsSetting.equals("Stations Included")) {
                    if (uniqueOrbit.getKey() instanceof JumpPointAPI) continue;
                    if (uniqueOrbit.getKey().hasTag(Tags.COMM_RELAY)) continue;
                    if (uniqueOrbit.getKey().hasTag(Tags.GATE)) continue;
                    if (uniqueOrbit.getKey().hasTag(Tags.NAV_BUOY)) continue;
                    if (uniqueOrbit.getKey().hasTag(Tags.SENSOR_ARRAY)) continue;
                    if (uniqueOrbit.getKey().hasTag(Tags.WARNING_BEACON)) continue;
                    if (uniqueOrbit.getKey().hasTag(Tags.STATION) && uniqueOrbit.getKey().getMarket() != null) {
                         if (!uniqueOrbit.getKey().getMarket().getMemory().getBoolean("$isSurveyed")) continue;
                         if (!uniqueOrbit.getKey().getMarket().getMemory().getBoolean("$visitedBefore")) continue;
//                         if (!uniqueOrbit.getKey().isVisibleToPlayerFleet()) continue;
                    }
                }

                Vector2f center = uniqueOrbit.getKey().getOrbitFocus().getLocation();
                float radius = uniqueOrbit.getKey().getCircularOrbitRadius();



                float thickness = Math.max(1.5f, viewport.getViewMult() * 2f); // Zoom-scaled thickness

                int segments = 192; // 128–256

                if (uniqueOrbit.getKey() instanceof PlanetAPI) {
                    PlanetAPI planet = (PlanetAPI) uniqueOrbit.getKey();

                    if (researchSetting.equals("On Full Survey")) {
                        if (planet.getMarket().getSurveyLevel() != MarketAPI.SurveyLevel.FULL) continue;
                    }
                    else if (researchSetting.equals("Witnessed")) {
                        String researchTimeSetting = LunaSettings.getString("PN_CelestialOrbits", "celorb_researchTimeRadio");
                        // Check if researchTimeSetting is null, if so, throw an error
                        if (researchTimeSetting == null) throw new RuntimeException("Unable to find LunaSetting 'celorb_researchTimeRadio' for mod 'PN_CelestialOrbits'");

                        float comparingOrbitalPeriod = getComparingOrbitalPeriod(researchTimeSetting, planet);
                        // Not enough time spent in system means the planet does not draw its orbit yet
                        if (comparingOrbitalPeriod > planet.getStarSystem().getMemoryWithoutUpdate()
                                .getFloat("$celorb_daysSpentInSystem")) continue;
                    }
                }

                SectorEntityToken currentCourseTarget = Global.getSector().getCampaignUI().getCurrentCourseTarget();

                // Populate research setting variable
                courseFocusSetting = LunaSettings.getString("PN_CelestialOrbits", "celorb_courseFocusRadio");
                // Check if researchSetting is null, if so, throw an error
                if (courseFocusSetting == null)
                    throw new RuntimeException("Unable to find LunaSetting 'celorb_courseFocusRadio' for mod 'PN_CelestialOrbits'");

                if (currentCourseTarget != null && currentCourseTarget.getOrbitFocus() != null) {
                    if (courseFocusSetting.equals("Highlight") && currentCourseTarget == uniqueOrbit.getKey()) {
                        Color color = new Color(241, 185, 0, 50);
                        if (color != null) {
                            dRed = (float) color.getRed() / 255;
                            dGreen = (float) color.getGreen() / 255;
                            dBlue = (float) color.getBlue() / 255;
                        }
                    }
                    else if (courseFocusSetting.equals("Filter")) {
                        Color color = new Color(136, 112, 34, 20);
                        if (color != null) {
                            dRed = (float) color.getRed() / 255;
                            dGreen = (float) color.getGreen() / 255;
                            dBlue = (float) color.getBlue() / 255;
                        }
                        // Makes it so only the filtered orbit shows
                        center = currentCourseTarget.getOrbitFocus().getLocation();
                        radius = currentCourseTarget.getCircularOrbitRadius();
                    }
                    else {
                        if (Boolean.TRUE.equals(LunaSettings.getBoolean("PN_CelestialOrbits", "celorb_orbitColorBoolean"))) {
                            Color color = LunaSettings.getColor("PN_CelestialOrbits", "celorb_orbitColorPicker");
                            if (color != null) {
                                dRed = (float) color.getRed() / 255;
                                dGreen = (float) color.getGreen() / 255;
                                dBlue = (float) color.getBlue() / 255;
                            }
                        }
                        else {
                            dRed = 0.7f;
                            dGreen = 0.8f;
                            dBlue = 1.0f;
                        }
                    }
                }

                drawEntityOrbit(segments, center, radius, thickness);
            }
        }



        SectorEntityToken currentCourseTarget = Global.getSector().getCampaignUI().getCurrentCourseTarget();

        if (currentCourseTarget == null) return;
        if (!(currentCourseTarget instanceof PlanetAPI)) return;

//        PlanetAPI planet = (PlanetAPI) currentCourseTarget;


        // Red Square Test
//        GL11.glDisable(GL11.GL_TEXTURE_2D);
//
//        GL11.glColor3f(1f, 0f, 0f);
//
//        GL11.glPointSize(10);
//
//        GL11.glBegin(GL11.GL_POINTS);
//        GL11.glVertex2f(
//                planet.getLocation().x,
//                planet.getLocation().y
//        );
//        GL11.glEnd();


        // Planet orbit draw
//        for (PlanetAPI planet : entity.getStarSystem().getPlanets()) {
//
//            if (planet.isBlackHole() || planet.isStar()) continue;
//
//            Vector2f center = planet.getOrbitFocus().getLocation();
//            float radius = planet.getCircularOrbitRadius();
//
//            float thickness = Math.max(1.5f, viewport.getViewMult() * 2f); // Zoom-scaled thickness
//
//            int segments = 180; // 128–256
//
//            drawEntityOrbit(segments, center, radius, thickness);
//        }



    }

    private static float getComparingOrbitalPeriod(String researchTimeSetting, PlanetAPI planet) {
        float comparingOrbitalPeriod;
        if (researchTimeSetting.equals("Quarter Rotation")) {
            comparingOrbitalPeriod = planet.getOrbit().getOrbitalPeriod()/4;
        }
        else if (researchTimeSetting.equals("Half Rotation")) {
            comparingOrbitalPeriod = planet.getOrbit().getOrbitalPeriod()/2;
        }
        else {
            comparingOrbitalPeriod = planet.getOrbit().getOrbitalPeriod();
        }
        return comparingOrbitalPeriod;
    }

    private void drawEntityOrbit(int segments, Vector2f center, float radius, float thickness) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glDisable(GL11.GL_TEXTURE_2D);

        GL11.glColor4f(dRed, dGreen, dBlue, 0.18f);

        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

        for (int i = 0; i <= segments; i++) {
            double angle = Math.PI * 2 * i / segments;

            float cos = (float)Math.cos(angle);
            float sin = (float)Math.sin(angle);

            // Outer edge
            GL11.glVertex2f(
                    center.x + cos * (radius + thickness),
                    center.y + sin * (radius + thickness)
            );

            // Inner edge
            GL11.glVertex2f(
                    center.x + cos * (radius - thickness),
                    center.y + sin * (radius - thickness)
            );
        }

        GL11.glEnd();
    }

    public Set<String> populateEntityBlacklist() {
        Set<String> entityBlacklist = new HashSet<>();
        entityBlacklist.add(Entities.DERELICT_SURVEY_PROBE);
        entityBlacklist.add(Entities.DERELICT_SURVEY_SHIP);
        entityBlacklist.add(Entities.DERELICT_MOTHERSHIP);
        entityBlacklist.add(Entities.DERELICT_GATEHAULER);
        entityBlacklist.add(Entities.DERELICT_CRYOSLEEPER);
        entityBlacklist.add(Entities.SPEC_LIMBO_WORMHOLE_CACHE);
        entityBlacklist.add(Entities.LARGE_CACHE);
        entityBlacklist.add(Entities.DEBRIS_FIELD_SHARED);
        entityBlacklist.add(Entities.COMM_RELAY);
        entityBlacklist.add(Entities.NAV_BUOY);
        entityBlacklist.add(Entities.SENSOR_ARRAY);
        entityBlacklist.add(Entities.COMM_RELAY_MAKESHIFT);
        entityBlacklist.add(Entities.NAV_BUOY_MAKESHIFT);
        entityBlacklist.add(Entities.SENSOR_ARRAY_MAKESHIFT);
        entityBlacklist.add(Entities.STABLE_LOCATION);
        entityBlacklist.add(Entities.MISSION_LOCATION);
        entityBlacklist.add(Entities.FUSION_LAMP);
        entityBlacklist.add(Entities.GENERIC_PROBE);
        entityBlacklist.add(Entities.CARGO_POD_SPECIAL);
        entityBlacklist.add(Entities.STELLAR_MIRROR);
        entityBlacklist.add(Entities.STELLAR_SHADE);
        entityBlacklist.add(Entities.INACTIVE_GATE);
        entityBlacklist.add(Entities.CARGO_PODS);
        entityBlacklist.add(Entities.WARNING_BEACON);
        entityBlacklist.add(Entities.ORBITAL_DOCKYARD);
        entityBlacklist.add(Entities.MAKESHIFT_STATION);
        entityBlacklist.add(Entities.STATION_MINING_REMNANT);
        entityBlacklist.add(Entities.STATION_RESEARCH_REMNANT);
        entityBlacklist.add(Entities.ORBITAL_HABITAT_REMNANT);
        entityBlacklist.add(Entities.TECHNOLOGY_CACHE);
        entityBlacklist.add(Entities.SUPPLY_CACHE);
        entityBlacklist.add(Entities.SUPPLY_CACHE_SMALL);
        entityBlacklist.add(Entities.EQUIPMENT_CACHE);
        entityBlacklist.add(Entities.EQUIPMENT_CACHE_SMALL);
        entityBlacklist.add(Entities.WEAPONS_CACHE);
        entityBlacklist.add(Entities.WEAPONS_CACHE_LOW);
        entityBlacklist.add(Entities.WEAPONS_CACHE_HIGH);
        entityBlacklist.add(Entities.WEAPONS_CACHE_REMNANT);
        entityBlacklist.add(Entities.WEAPONS_CACHE_SMALL);
        entityBlacklist.add(Entities.WEAPONS_CACHE_SMALL_LOW);
        entityBlacklist.add(Entities.WEAPONS_CACHE_SMALL_HIGH);
        entityBlacklist.add(Entities.WEAPONS_CACHE_SMALL_REMNANT);
        entityBlacklist.add(Entities.HIDDEN_CACHE);
        entityBlacklist.add(Entities.ALPHA_SITE_WEAPONS_CACHE);
        entityBlacklist.add(Entities.GENERIC_PROBE_ACTIVE);
        entityBlacklist.add(Entities.WRECK);
        entityBlacklist.add(Entities.BASE_CONSTELLATION_LABEL);
        entityBlacklist.add(Entities.BASE_INTEL_ICON);
        entityBlacklist.add(Entities.EXPLOSION);
        return entityBlacklist;
    }

}
