package purplenebula.celestialorbits.trackers;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import org.lazywizard.console.Console;

public class SystemTimeTracker implements EveryFrameScript {
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

        if (Global.getSector().isPaused()) return;

        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        if(playerFleet == null) return;

        StarSystemAPI starSystem = playerFleet.getStarSystem();
        if (starSystem == null) return;

        float days = Global.getSector().getClock().convertToDays(amount);

        MemoryAPI systemMemory = starSystem.getMemoryWithoutUpdate();

        float totalDays = 0;
        if (systemMemory.contains("$celorb_daysSpentInSystem")) {
            totalDays = systemMemory.getFloat("$celorb_daysSpentInSystem");
        }
        systemMemory.set("$celorb_daysSpentInSystem", totalDays + days);


    }
}
