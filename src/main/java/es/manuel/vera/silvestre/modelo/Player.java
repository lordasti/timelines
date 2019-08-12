package es.manuel.vera.silvestre.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Player{
    private final long dbid;
    private final long money;
    private final int honor;
    private final int shuttleRentalTokens;
    private final int vipPoints;
    private final int vipLevel;
    private final int replicatorUsesToday;
    private final int replicatorLimit;
    private final Character character;

    public Player(@JsonProperty("dbid") long dbid, @JsonProperty("money") long money,
        @JsonProperty("honor") int honor, @JsonProperty("shuttle_rental_tokens") int shuttleRentalTokens,
        @JsonProperty("vip_points") int vipPoints, @JsonProperty("vip_level") int vipLevel,
        @JsonProperty("replicator_uses_today") int replicatorUsesToday,
        @JsonProperty("replicator_limit") int replicatorLimit, @JsonProperty("character") Character character){
        this.dbid = dbid;
        this.money = money;
        this.honor = honor;
        this.shuttleRentalTokens = shuttleRentalTokens;
        this.vipPoints = vipPoints;
        this.vipLevel = vipLevel;
        this.replicatorUsesToday = replicatorUsesToday;
        this.replicatorLimit = replicatorLimit;
        this.character = character;
    }
}
