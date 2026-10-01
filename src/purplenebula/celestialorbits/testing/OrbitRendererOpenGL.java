package purplenebula.celestialorbits.testing;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignEngineLayers;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.combat.ViewportAPI;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

/**
 * DEPRECATED
 */
public class OrbitRendererOpenGL implements EveryFrameScript {



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

        if (Global.getCurrentState() == GameState.CAMPAIGN) {
            if (Global.getSector().getCampaignUI().getCurrentCourseTarget() != null) {
                render(CampaignEngineLayers.TERRAIN_2,Global.getSector().getViewport());
            }
        }

    }

    public void render(CampaignEngineLayers layer, ViewportAPI viewport) {

        if (layer != CampaignEngineLayers.TERRAIN_2) return;

        SectorEntityToken currentCourseTarget = Global.getSector().getCampaignUI().getCurrentCourseTarget();

        if (!(currentCourseTarget instanceof PlanetAPI)) return;

        PlanetAPI planet = (PlanetAPI) Global.getSector().getCampaignUI().getCurrentCourseTarget();

        Vector2f center = planet.getOrbitFocus().getLocation();
        float radius = planet.getCircularOrbitRadius();

        // TEST 1 (No result)
//        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
//
//        GL11.glDisable(GL11.GL_TEXTURE_2D);
//        GL11.glEnable(GL11.GL_BLEND);
//        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//
//        GL11.glColor4f(0.8f, 0.8f, 1f, 0.25f);
//
//        GL11.glBegin(GL11.GL_LINE_LOOP);
//
//        int segments = 180;
//
//        for (int i = 0; i < segments; i++) {
//            double angle = Math.PI * 2.0 * i / segments;
//
//            float x = center.x + (float)Math.cos(angle) * radius;
//            float y = center.y + (float)Math.sin(angle) * radius;
//
//            GL11.glVertex2f(x, y);
//        }
//
//        GL11.glEnd();
//
//        GL11.glPopAttrib();

        // TEST 2
//        float radius = orbitRadius;
//        float thickness = 2f; // Default
        float thickness = Math.max(1.5f, viewport.getViewMult() * 2f); // Zoom-scaled thickness

        int segments = 180;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glDisable(GL11.GL_TEXTURE_2D);

        GL11.glColor4f(
                0.7f,
                0.8f,
                1.0f,
                0.18f
        );

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

}
