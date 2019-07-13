package es.manuel.vera.silvestre.modelo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;
import java.util.function.Consumer;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class Crew implements Comparable<Crew> {
    private final String name;
    private final int rank;
    private final int stars;
    private final int total;
    private final int command;
    private final int diplomacy;
    private final int engineering;
    private final int security;
    private final int science;
    private final int medicine;

    public Crew(List<Object> raw) {
        name = raw.size() > 0 ? (String) raw.get(0) : "";
        rank = raw.size() > 1 ? NumberUtils.isCreatable((String) raw.get(1)) ? NumberUtils.createInteger((String) raw.get(1)) : 0 : 0;
        stars = raw.size() > 2 ? NumberUtils.isCreatable((String) raw.get(2)) ? NumberUtils.createInteger((String) raw.get(2)) : 0 : 0;
        total = raw.size() > 3 ? NumberUtils.isCreatable((String) raw.get(3)) ? NumberUtils.createInteger((String) raw.get(3)) : 0 : 0;
        command = raw.size() > 4 ? NumberUtils.isCreatable((String) raw.get(4)) ? NumberUtils.createInteger((String) raw.get(4)) : 0 : 0;
        diplomacy = raw.size() > 5 ? NumberUtils.isCreatable((String) raw.get(5)) ? NumberUtils.createInteger((String) raw.get(5)) : 0 : 0;
        engineering = raw.size() > 6 ? NumberUtils.isCreatable((String) raw.get(6)) ? NumberUtils.createInteger((String) raw.get(6)) : 0 : 0;
        security = raw.size() > 7 ? NumberUtils.isCreatable((String) raw.get(7)) ? NumberUtils.createInteger((String) raw.get(7)) : 0 : 0;
        science = raw.size() > 8 ? NumberUtils.isCreatable((String) raw.get(8)) ? NumberUtils.createInteger((String) raw.get(8)) : 0 : 0;
        medicine = raw.size() > 9 ? NumberUtils.isCreatable((String) raw.get(9)) ? NumberUtils.createInteger((String) raw.get(9)) : 0 : 0;
    }

    public boolean isValid() {
        return !StringUtils.isBlank(name);
    }

    public boolean hasProficiency(Stats stat) {
        if (stat == Stats.COMMAND) {
            return command > 0;
        }
        if (stat == Stats.DIPLOMACY) {
            return diplomacy > 0;
        }
        if (stat == Stats.ENGINEERING) {
            return engineering > 0;
        }
        if (stat == Stats.SECURITY) {
            return security > 0;
        }
        if (stat == Stats.SCIENCE) {
            return science > 0;
        }
        if (stat == Stats.MEDICINE) {
            return medicine > 0;
        }

        return false;
    }

    public Crew applyVoyageBonuses(BonusStats bonusStats) {
        CrewBuilder builder = toBuilder();
        applyBonusToStat(bonusStats, Stats.COMMAND, command, builder::command);
        applyBonusToStat(bonusStats, Stats.DIPLOMACY, diplomacy, builder::diplomacy);
        applyBonusToStat(bonusStats, Stats.ENGINEERING, engineering, builder::engineering);
        applyBonusToStat(bonusStats, Stats.SECURITY, security, builder::security);
        applyBonusToStat(bonusStats, Stats.SCIENCE, science, builder::science);
        applyBonusToStat(bonusStats, Stats.MEDICINE, medicine, builder::medicine);
        return builder.build();
    }

    private void applyBonusToStat(BonusStats bonusStats, Stats current, Integer value, Consumer<Integer> consumer) {
        if (bonusStats.getPrimary() == current) {
            consumer.accept((int) (value * 3.5));
        } else if (bonusStats.getSecondary() == current) {
            consumer.accept((int) (value * 2.5));
        } else {
            consumer.accept(value);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Crew) {
            return name.equals(((Crew) other).name);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public int compareTo(Crew other) {
        return Integer.compare(other.getTotal(), getTotal());
    }

    public int getTotal() {
        return command + diplomacy + engineering + security + science + medicine;
    }
}
