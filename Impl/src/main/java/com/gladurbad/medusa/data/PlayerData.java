package com.gladurbad.medusa.data;

import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.processor.ActionProcessor;
import com.gladurbad.medusa.data.processor.ClickProcessor;
import com.gladurbad.medusa.data.processor.CombatProcessor;
import com.gladurbad.medusa.data.processor.PositionProcessor;
import com.gladurbad.medusa.data.processor.RotationProcessor;
import com.gladurbad.medusa.data.processor.VelocityProcessor;
import com.gladurbad.medusa.exempt.ExemptProcessor;
import com.gladurbad.medusa.util.type.EvictingList;
import com.gladurbad.medusa.util.type.Pair;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
@Setter
public class PlayerData {

    private final Player player;
    private String clientBrand;
    private int totalViolations, combatViolations, movementViolations, playerViolations;
    private final long joinTime = System.currentTimeMillis();
    private final List<Check> checks;
    private final EvictingList<Pair<Location, Integer>> targetLocations = new EvictingList<>(40);

    private final ExemptProcessor exemptProcessor = new ExemptProcessor(this);
    private final CombatProcessor combatProcessor = new CombatProcessor(this);
    private final ActionProcessor actionProcessor = new ActionProcessor(this);
    private final ClickProcessor clickProcessor = new ClickProcessor(this);
    private final PositionProcessor positionProcessor = new PositionProcessor(this);
    private final RotationProcessor rotationProcessor = new RotationProcessor(this);
    private final VelocityProcessor velocityProcessor = new VelocityProcessor(this);

    public PlayerData(final Player player) {
        this.player = player;
        this.checks = Medusa.INSTANCE.getCheckManager().loadChecks(this);
    }
}
